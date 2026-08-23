package rest;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;
import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Configures the web pages and REST endpoints of the application.
 */
public final class RestHandler {
    private static final String MEMBER_COLLECTION = "member";
    private static final String SPEECH_COLLECTION = "speeches_nlp";

    private final Javalin app;
    private final MongoDatabaseHandler database;

    public RestHandler(ConfigJavalin config, MongoDatabaseHandler database) throws IOException {
        this.database = database;
        TemplatesHandler templates = new TemplatesHandler(this, "src/main/resources/templates");
        this.app = Javalin.create(javalin -> {
            javalin.fileRenderer(new JavalinFreemarker(templates.getConfiguration()));
            javalin.staticFiles.add(files -> {
                files.hostedPath = "/js";
                files.directory = "/js";
                files.location = Location.CLASSPATH;
            });
        });
        registerRoutes();
        app.start(config.getServerPort());
    }

    private void registerRoutes() {
        app.get("/openapi.yaml", this::serveOpenApi);
        app.get("/swagger", context ->
                context.redirect("/webjars/swagger-ui/index.html?url=/openapi.yaml"));
        app.get("/", context -> context.redirect("/dashboard"));
        app.get("/dashboard", context -> context.render("dashboard.ftl"));

        app.get("/api/member", context -> getBySpeechId(context, MEMBER_COLLECTION));
        app.post("/api/member", context -> create(context, MEMBER_COLLECTION));
        app.delete("/api/member", context -> deleteBySpeechId(context, MEMBER_COLLECTION));

        app.get("/api/speech", context -> getBySpeechId(context, SPEECH_COLLECTION));
        app.post("/api/speech", context -> create(context, SPEECH_COLLECTION));
        app.delete("/api/speech", context -> deleteBySpeechId(context, SPEECH_COLLECTION));

        app.get("/api/visualization/topics",
                context -> context.json(Utils.getTopic(filteredSpeeches(context))));
        app.get("/api/visualization/pos",
                context -> context.json(Utils.getPosAgg(filteredSpeeches(context))));
        app.get("/api/visualization/sentiment",
                context -> context.json(Utils.getSentimentSpeech(filteredSpeeches(context))));
        app.get("/api/visualization/namedEntities",
                context -> context.json(Utils.getEntites(filteredSpeeches(context))));

        app.get("/api/search/speeches", this::searchSpeeches);
    }

    private void serveOpenApi(Context context) throws IOException {
        InputStream resource = RestHandler.class.getClassLoader().getResourceAsStream("openapi.yaml");
        if (resource == null) {
            context.status(HttpStatus.NOT_FOUND).result("OpenAPI description not found");
            return;
        }
        context.contentType("text/yaml").result(resource);
    }

    private void getBySpeechId(Context context, String collection) {
        Document result = database.readOne(collection, speechIdFilter(context, collection));
        if (result == null) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }
        context.json(result);
    }

    private void create(Context context, String collection) {
        if (context.body().isBlank()) {
            context.status(HttpStatus.BAD_REQUEST).result("Request body must contain a JSON document");
            return;
        }
        Document document;
        try {
            document = Document.parse(context.body());
        } catch (RuntimeException exception) {
            context.status(HttpStatus.BAD_REQUEST).result("Invalid JSON document");
            return;
        }
        database.create(collection, document);
        context.status(HttpStatus.CREATED);
    }

    private void deleteBySpeechId(Context context, String collection) {
        database.deleteOne(collection, speechIdFilter(context, collection));
        context.status(HttpStatus.NO_CONTENT);
    }

    private Document speechIdFilter(Context context, String collection) {
        String speechId = context.queryParam("speechid");
        if (speechId == null || speechId.isBlank()) {
            throw new BadRequestResponse("Missing query parameter: speechid");
        }
        String field = SPEECH_COLLECTION.equals(collection) ? "redeId" : "speechid";
        return new Document(field, speechId.trim());
    }

    @SuppressWarnings("unchecked")
    private List<Document> filteredSpeeches(Context context) {
        String ids = context.queryParam("speechIds");
        List<Document> speeches;
        if (ids == null || ids.isBlank()) {
            speeches = Utils.getAll(database);
        } else {
            speeches = new ArrayList<>();
            for (String id : ids.split("\\s*,\\s*")) {
                Document speech = Utils.getSpeech(database, id);
                if (speech != null) {
                    speeches.add(speech);
                }
            }
        }

        speeches = Utils.filterByDateRange(
                speeches, context.queryParam("fromDate"), context.queryParam("toDate"));
        speeches = Utils.filterByFraction(speeches, context.queryParam("fraction"));
        String topic = context.queryParam("topic");
        return topic == null || topic.isBlank() ? speeches : Utils.filterByTopic(speeches, topic);
    }

    private void searchSpeeches(Context context) {
        String query = context.queryParam("query");
        if (query == null || query.isBlank()) {
            context.json(List.of());
            return;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<Document> speeches = Utils.filterByDateRange(
                Utils.getAll(database), context.queryParam("fromDate"), context.queryParam("toDate"));
        speeches = Utils.filterByFraction(speeches, context.queryParam("fraction"));

        List<Map<String, Object>> results = new ArrayList<>();
        for (Document speech : speeches) {
            String text = speech.getString("text");
            String speaker = firstPresent(speech, "speakerName", "speaker");
            String topics = topicText(speech);
            if (!matches(normalizedQuery, text, speaker, topics)) {
                continue;
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("speechId", firstPresent(speech, "redeId", "speechid"));
            result.put("speaker", speaker);
            result.put("fraction", firstPresent(speech, "fraktion", "fraction"));
            result.put("date", firstPresent(speech, "date", "datum"));
            result.put("preview", preview(text));
            Double sentiment = averageSentiment(speech);
            if (sentiment != null) {
                result.put("sentiment", sentiment);
            }
            results.add(result);
        }
        context.json(results);
    }

    static boolean matches(String normalizedQuery, String... values) {
        for (String value : values) {
            if (value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                return true;
            }
        }
        return false;
    }

    private static String firstPresent(Document document, String... fields) {
        for (String field : fields) {
            String value = document.getString(field);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private static String preview(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= 200 ? text : text.substring(0, 200) + "...";
    }

    private static String topicText(Document speech) {
        Document nlp = speech.get("nlp", Document.class);
        if (nlp == null) {
            return "";
        }
        List<Document> topics = nlp.getList("topicsTop5", Document.class);
        if (topics == null) {
            return "";
        }
        return topics.stream()
                .map(topic -> topic.getString("category"))
                .filter(category -> category != null)
                .reduce("", (left, right) -> left + " " + right);
    }

    private static Double averageSentiment(Document speech) {
        Document nlp = speech.get("nlp", Document.class);
        if (nlp == null) {
            return null;
        }
        List<Document> sentences = nlp.getList("sentences", Document.class);
        if (sentences == null || sentences.isEmpty()) {
            return null;
        }
        double total = 0;
        int count = 0;
        for (Document sentence : sentences) {
            Number sentiment = sentence.get("sentiment", Number.class);
            if (sentiment != null) {
                total += sentiment.doubleValue();
                count++;
            }
        }
        return count == 0 ? null : total / count;
    }
}
