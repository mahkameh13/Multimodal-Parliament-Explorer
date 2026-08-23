package rest;
import io.javalin.http.staticfiles.Location;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;
import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

/**
 * REST-Handler für API-Endpunkte und Dashboard.
 * @author Nina Lutz
 * @author Mahkameh Momeni
 */
public class RestHandler {

    private Javalin handler ;
    private TemplatesHandler templates;
    private MongoDatabaseHandler db;
    public RestHandler(ConfigJavalin config, MongoDatabaseHandler db) throws IOException {
        this.templates = new TemplatesHandler(this, "src/main/resources/templates");
        // Geändert von Mahkameh Momeni: staticFiles-Konfiguration für Javalin überarbeitet
        this.handler = Javalin.create(conf -> {
            conf.fileRenderer(new JavalinFreemarker(this.templates.getConfiguration()));
            // Statische Dateien aus src/main/resources/js unter /js bereitstellen
            conf.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/js";     // URL-Pfad
                staticFiles.directory  = "/js";     // Ordner im classpath (src/main/resources/js)
                staticFiles.location   = Location.CLASSPATH;
            });
        });
        this.db = db;
        this.handler.start(config.getServerPort());
        this.init();

    }

    /**
     * Ermittelt die für die Visualisierung zu verwendende Reden-Liste
     * gemäß Filterparametern (Aufgabe 3.1.b: Zeitraum, Zeitpunkt, Fraktion, speechIds, topic).
     * @author Mahkameh Momeni
     */
    private java.util.List<Document> getFilteredSpeeches(io.javalin.http.Context ctx) {
        String speechIds = ctx.queryParam("speechIds");
        String topic = ctx.queryParam("topic");
        String fromDate = ctx.queryParam("fromDate");
        String toDate = ctx.queryParam("toDate");
        String fraction = ctx.queryParam("fraction");

        java.util.List<Document> list = (speechIds != null && !speechIds.isBlank())
                ? Utils.getSpeeches(db, speechIds)
                : Utils.getAll(db);

        list = Utils.filterByDateRange(list, fromDate, toDate);
        list = Utils.filterByFraction(list, fraction);
        if (topic != null && !topic.isBlank()) {
            list = Utils.filterByTopic(list, topic);
        }
        return list;
    }

    private void init(){

        // OpenAPI Datei ausliefern
        this.handler.get("/openapi.yaml", ctx -> {
            ctx.result(
                    java.nio.file.Files.readString(
                            java.nio.file.Paths.get("src/main/resources/openapi.yaml")
                    )
            );
            ctx.contentType("text/yaml");
        });

        // Swagger UI öffnen
        this.handler.get("/swagger", ctx -> {
            ctx.redirect("/webjars/swagger-ui/index.html?url=/openapi.yaml");
        });

        // Root-URL auf das Dashboard weiterleiten
        // Geändert von Mahkameh Momeni (Aufgabe 3.1.b – UX)
        this.handler.get("/", ctx -> ctx.redirect("/dashboard"));

        // Dashboard-Seite mit den d3.js-Visualisierungen rendern
        // Implementiert von Mahkameh Momeni (Aufgabe 3.1.a)
        this.handler.get("/dashboard", ctx -> ctx.render("dashboard.ftl"));
        this.handler.get("/api/member", ctx -> {
            String speechid = ctx.queryParam("speechid");
            Document filter = new Document("speechid", speechid);
            Document doc = db.readOne("member", filter);
            ctx.json(doc);
        });
        this.handler.delete("/api/member/", ctx -> {
            String speechid = ctx.queryParam("speechid");
            Document filter = new Document("speechid", speechid);
            db.deleteOne("member", filter);
            ctx.json("ok");
        });
        this.handler.post("/api/member", ctx -> {

        });
        this.handler.get("api/speech", ctx -> {
            String speechid = ctx.queryParam("speechid");
            Document filter = new Document("speechid", speechid);
            Document doc = db.readOne("speech", filter);
            ctx.json(doc);
        });
        this.handler.post("api/speech", ctx -> {

        });
        this.handler.delete("api/speech/", ctx -> {
            String speechid = ctx.queryParam("speechid");
            Document filter = new Document("speechid", speechid);
            db.deleteOne("protocols", filter);
        });


        // Geändert von Mahkameh Momeni (Aufgabe 3.1.b – Filter)
        this.handler.get("api/visualization/topics", ctx -> {
            java.util.List<Document> list = getFilteredSpeeches(ctx);
            java.util.Map<String, Double> map = Utils.getTopic(list);
            ctx.json(map);
        });

        // Geändert von Mahkameh Momeni (Aufgabe 3.1.b – Filter)
        this.handler.get("api/visualization/pos", ctx -> {
            java.util.List<Document> list = getFilteredSpeeches(ctx);
            java.util.Map<String, Integer> posCounts = Utils.getPosAgg(list);
            ctx.json(posCounts);
        });

        // Geändert von Mahkameh Momeni (Aufgabe 3.1.b – Filter)
        this.handler.get("api/visualization/sentiment", ctx -> {
            java.util.List<Document> list = getFilteredSpeeches(ctx);
            java.util.Map<String, Double> sentiment = Utils.getSentimentSpeech(list);
            ctx.json(sentiment);
        });

        // Geändert von
        // @author Mahkameh Momeni (Aufgabe 3.1.b – Filter)
        this.handler.get("api/visualization/namedEntities", ctx -> {
            java.util.List<Document> list = getFilteredSpeeches(ctx);
            java.util.Map<String, Integer> entities = Utils.getEntites(list);
            ctx.json(entities);
        });

        // Generische Suchfunktion für Reden (Aufgabe 3.1.c)
        // Implementiert von
        // @author Mahkameh Momeni
        this.handler.get("/api/search/speeches", ctx -> {
            String query = ctx.queryParam("query");
            String fromDate = ctx.queryParam("fromDate");
            String toDate = ctx.queryParam("toDate");
            String fraction = ctx.queryParam("fraction");

            // Alle Reden holen und wie in 3.1.b filtern
            List<Document> speeches = Utils.getAll(db);
            speeches = Utils.filterByDateRange(speeches, fromDate, toDate);
            speeches = Utils.filterByFraction(speeches, fraction);

            List<Map<String, Object>> results = new ArrayList<>();

            if (query != null && !query.isBlank()) {
                String qLower = query.toLowerCase();

                for (Document speech : speeches) {
                    String text = speech.getString("text");
                    String speaker = speech.getString("speakerName");
                    if (speaker == null) {
                        speaker = speech.getString("speaker");
                    }
                    String fractionField = speech.getString("fraction");
                    String date = speech.getString("date");
                    if (date == null) {
                        date = speech.getString("datum");
                    }

                    // Topics als zusätzlicher Suchraum (optional)
                    String topicsJoined = "";
                    Document nlp = speech.get("nlp", Document.class);
                    if (nlp != null) {
                        @SuppressWarnings("unchecked")
                        List<Document> topicsTop5 = (List<Document>) nlp.get("topicsTop5");
                        if (topicsTop5 != null) {
                            StringBuilder sb = new StringBuilder();
                            for (Document t : topicsTop5) {
                                if (sb.length() > 0) sb.append(" ");
                                sb.append(t.getString("category"));
                            }
                            topicsJoined = sb.toString();
                        }
                    }

                    boolean matches =
                            (text != null && text.toLowerCase().contains(qLower)) ||
                                    (speaker != null && speaker.toLowerCase().contains(qLower)) ||
                                    (!topicsJoined.isEmpty() && topicsJoined.toLowerCase().contains(qLower));

                    if (matches) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("speechId", speech.getString("redeId"));
                        item.put("speaker", speaker);
                        item.put("fraction", fractionField);
                        item.put("date", date);
                        if (text != null) {
                            item.put("preview", text.length() > 200 ? text.substring(0, 200) + "..." : text);
                        } else {
                            item.put("preview", "");
                        }
                        // Durchschnitts-Sentiment (nutzt deine bestehende Utils-Methode)
                        item.put("sentiment", Utils.getSentimentSpeech(speech));

                        results.add(item);
                    }
                }
            }

            ctx.json(results);
        }); }
}
