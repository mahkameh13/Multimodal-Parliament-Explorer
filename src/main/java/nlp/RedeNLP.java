package nlp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import databaseSpeech.Speech;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nlp.types.PlenarSpeech;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.type.AudioToken;
import org.xml.sax.SAXException;

public class RedeNLP {
   private final MongoCollection documentMongoCollection;
   private final PipelineDuui pipeline;
   private final CasStorage casStorage;
   private final LanguageFeatureExtract featureStats;
   private final LanguageFeatureExtract featureTopics;
   private final LanguageFeatureExtract featureSentences;
   private final LanguageFeatureExtract featureLemma;

   public RedeNLP(MongoCollection documentMongoCollection, int workercompose) throws IOException, URISyntaxException, CompressorException, InvalidXMLException, SAXException {
      this.documentMongoCollection = documentMongoCollection;
      this.pipeline = new PipelineDuui(workercompose);
      this.casStorage = new CasStorageInMongo(documentMongoCollection);
      this.featureStats = new FeatureStats();
      this.featureSentences = new FeatureSentences();
      this.featureTopics = new FeatureTopics();
      this.featureLemma = new FeatureLemma();
   }

   public void RedeProcess(Speech rede) throws ResourceInitializationException, CASException, IOException {
      Document document = this.findRede(rede.getSpeechId());
      if (document == null) {
         throw new IllegalStateException("Rede nicht in MongoDB" + rede.getSpeechId());
      } else {
         JCas jcas = rede.toCAS();
         if (jcas.getDocumentText() == null) {
            jcas.setDocumentText("");
         }

         List<String> unprocessedComments = document.getList("comments", String.class);
         JSONArray processedComments = this.extractComments(unprocessedComments, rede.getText(), jcas);
         if (this.casStorage.checkCasXmi(rede.getSpeechId())) {
            String xmi = this.casStorage.loadXmi(rede.getSpeechId());
            Serialize.deserializeCas(jcas, xmi);
            System.out.println("Cas wurde geladen" + rede.getSpeechId());
            this.plenarSpeechAnnotation(jcas, rede);
            String annotationXmi = Serialize.serializeCasObj(jcas);
            this.casStorage.saveCasXmi(rede.getSpeechId(), annotationXmi);
            if (!document.containsKey("nlp")) {
               JSONObject nlpObject = this.extractNlp(jcas, processedComments);
               this.saveInMongoDB(rede.getSpeechId(), nlpObject);
               System.out.println("NLP extrahiert" + rede.getSpeechId());
            } else if (document.get("nlp") == null) {
               JSONObject nlpObject = this.extractNlp(jcas, processedComments);
               this.saveInMongoDB(rede.getSpeechId(), nlpObject);
               System.out.println("NLP extrahiert" + rede.getSpeechId());
            }

         } else {
            try {
               this.pipeline.run(jcas);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }

            this.plenarSpeechAnnotation(jcas, rede);
            JSONObject nlpObject = this.extractNlp(jcas, processedComments);
            this.saveInMongoDB(rede.getSpeechId(), nlpObject);
            String xmi = Serialize.serializeCasObj(jcas);
            this.casStorage.saveCasXmi(rede.getSpeechId(), xmi);
            System.out.println("Cas gespeichert:" + rede.getSpeechId());
         }
      }
   }

   public void allRedeNLP(List redenList) throws ResourceInitializationException, CASException, IOException {
      for(Speech rede : redenList) {
         this.RedeProcess(rede);
      }

   }

   private Document findRede(String speechId) {
      return (Document)this.documentMongoCollection.find(Filters.eq("redeId", speechId)).first();
   }

   private JSONArray extractComments(List comments, String completeText, JCas jcas) {
      JSONArray commentArray = new JSONArray();
      if (comments == null) {
         return commentArray;
      } else {
         int startText = 0;

         for(String comment : comments) {
            if (comment != null && !comment.isBlank()) {
               String commentText = comment.trim();
               int begin = -1;
               int end = -1;
               if (completeText != null && !completeText.isBlank()) {
                  begin = completeText.indexOf(commentText, startText);
                  if (begin < 0) {
                     begin = completeText.indexOf(commentText);
                  }

                  if (begin >= 0) {
                     end = begin + commentText.length();
                     startText = end;
                  }
               }

               JSONObject commentObject = this.buildCommentStructure(commentText, begin, end);
               commentArray.put(commentObject);
            }
         }

         return commentArray;
      }
   }

   private JSONObject buildCommentStructure(String commentText, int begin, int end) {
      JSONObject commentObject = new JSONObject();
      commentObject.put("begin", begin);
      commentObject.put("end", end);
      commentObject.put("text", commentText);
      String speaker = "unbekannt";
      String fraction = "unbekannt";
      Pattern pattern3 = Pattern.compile("\\(Beifall bei (.*?)\\)");
      Matcher matcher3 = pattern3.matcher(commentText);
      Pattern pattern = Pattern.compile("\\(Zuruf von der (.*?): (.*?)\\)");
      Matcher matcher = pattern.matcher(commentText);
      Pattern pattern2 = Pattern.compile("\\((.*?) \\[(.*?)\\]: (.*?)\\)");
      Matcher matcher2 = pattern2.matcher(commentText);
      if (matcher3.find()) {
         commentObject.put("fraction", matcher3.group(1).trim());
         commentObject.put("textTyp", "Beifall");
         return commentObject;
      } else {
         if (matcher.find()) {
            fraction = matcher.group(1).trim();
            commentObject.put("textType", "Zuruf");
            commentObject.put("text", matcher.group(2).trim());
         }

         if (matcher2.find()) {
            speaker = matcher2.group(1).trim();
            fraction = matcher2.group(2).trim();
            commentObject.put("text", matcher2.group(3).trim());
         }

         commentObject.put("speaker", speaker);
         commentObject.put("fraction", fraction);
         return commentObject;
      }
   }

   private void saveInMongoDB(String speechId, JSONObject nlpObject) {
      Document documentFilter = new Document("redeId", speechId);
      Document document = new Document();
      if (nlpObject.has("stats")) {
         document.append("nlp.stats", Document.parse(nlpObject.getJSONObject("stats").toString()));
      }

      if (nlpObject.has("sentences")) {
         document.append("nlp.sentences", nlpObject.getJSONArray("sentences").toList());
      }

      if (nlpObject.has("topicsAll")) {
         document.append("nlp.topicsAll", nlpObject.getJSONArray("topicsAll").toList());
      }

      if (nlpObject.has("topicsTop5")) {
         document.append("nlp.topicsTop5", nlpObject.getJSONArray("topicsTop5").toList());
      }

      if (nlpObject.has("tokens")) {
         document.append("nlp.tokens", nlpObject.getJSONArray("tokens").toList());
      }

      if (nlpObject.has("comments")) {
         document.append("nlp.comments", nlpObject.getJSONArray("comments").toList());
      }

      Document documentUpdate = new Document("$set", document);
      UpdateResult resultOfDocument = this.documentMongoCollection.updateOne(documentFilter, documentUpdate);
      PrintStream var10000 = System.out;
      long var10001 = resultOfDocument.getMatchedCount();
      var10000.println("Match " + var10001 + "Modifiziert " + resultOfDocument.getModifiedCount());
   }

   private void plenarSpeechAnnotation(JCas jcas, Speech rede) {
      if (JCasUtil.select(jcas, PlenarSpeech.class).isEmpty()) {
         PlenarSpeech plenarSpeech = new PlenarSpeech(jcas);
         plenarSpeech.setBegin(0);
         if (jcas.getDocumentText() != null) {
            plenarSpeech.setEnd(jcas.getDocumentText().length());
         } else {
            plenarSpeech.setEnd(0);
         }

         plenarSpeech.setSpeechId(rede.getSpeechId() == null ? "" : rede.getSpeechId());
         plenarSpeech.setSpeakerName(rede.getspeaker() == null ? "" : rede.getspeaker());
         plenarSpeech.setFraction("");
         plenarSpeech.addToIndexes();
      }
   }

   private JSONObject extractNlp(JCas jcas, JSONArray processedComments) {
      JSONObject jsonOutput = new JSONObject();
      FeatureSentences sentences = new FeatureSentences();
      FeatureStats stats = new FeatureStats();
      FeatureTopics topics = new FeatureTopics();
      jsonOutput.put("stats", stats.extractFeature(jcas));
      JSONObject topicsObjects = topics.extractFeature(jcas);
      jsonOutput.put("topicsAll", topicsObjects.getJSONArray("topicsAll"));
      jsonOutput.put("topicsTop5", topicsObjects.getJSONArray("topicsTop5"));
      jsonOutput.put("sentences", sentences.extractFeature(jcas).getJSONArray("sentences"));
      jsonOutput.put("tokens", this.featureLemma.extractFeature(jcas).getJSONArray("tokens"));
      jsonOutput.put("comments", processedComments);
      return jsonOutput;
   }

   public void videoProcess(Speech rede, Path path) throws Exception {
      JCas jcas = rede.toCAS();
      JCas whisperCas = JCasFactory.createJCas();
      byte[] videoBytes = Files.readAllBytes(path);
      String encodedString = Base64.getEncoder().encodeToString(videoBytes);
      String contentType = Files.probeContentType(path);
      if (contentType == null) {
         contentType = "video/mp4";
      }

      JCas videoCas = whisperCas.createView("video");
      videoCas.setSofaDataString(encodedString, contentType);
      videoCas.setDocumentLanguage("de");
      JCas transcriptCas = whisperCas.createView("transcript");
      transcriptCas.setDocumentLanguage("de");
      PrintStream var10000 = System.out;
      String var10001 = rede.getSpeechId();
      var10000.println("Starte Whisper für: " + var10001 + "Videodatei" + String.valueOf(path.getFileName()));

      try {
         this.pipeline.runWhisper(whisperCas);
      } catch (Exception e) {
         var10000 = System.out;
         var10001 = rede.getSpeechId();
         var10000.println("Whisper funktioniert nicht für :" + var10001 + " " + String.valueOf(path));
         e.printStackTrace();
         return;
      }

      System.out.println("Whisper ist fertig für: " + rede.getSpeechId());
      JSONArray audioArray = new JSONArray();

      for(AudioToken audioToken : JCasUtil.select(transcriptCas, AudioToken.class)) {
         JSONObject audioObject = new JSONObject();
         audioObject.put("text", audioToken.getCoveredText());
         audioObject.put("start", audioToken.getTimeStart());
         audioObject.put("end", audioToken.getTimeEnd());
         audioArray.put(audioObject);
      }

      String videoText = transcriptCas.getDocumentText();
      int textLength = 0;
      if (videoText != null) {
         textLength = videoText.length();
      }

      System.out.println("Länge: " + textLength + " Rede: " + rede.getSpeechId());
      if (videoText == null) {
         System.out.println("Whisper liefert keinen Text" + rede.getSpeechId());
      } else if (videoText.isBlank()) {
         System.out.println("Whisper liefert leeres Feld" + rede.getSpeechId());
      } else {
         try {
            JCas mainTranscriptCas = jcas.getView("transcript");
         } catch (Exception var18) {
            JCas mainTranscriptCas = jcas.createView("transcript");
         }

         JCas onlyTranscript = JCasFactory.createText(videoText, "de");
         this.pipeline.run(onlyTranscript);
         System.out.println("Video soll in Datenbank gepeichert werden: " + rede.getSpeechId());
         JSONObject transcriptFeatures = this.extractNlp(onlyTranscript, new JSONArray());
         Document documentFilter = new Document("redeId", rede.getSpeechId());
         Document updateDocument = new Document("$set", (new Document("nlp.video", Document.parse(transcriptFeatures.toString()))).append("video.path", path.getFileName().toString()).append("video.text", videoText).append("video.text_length", textLength).append("audio.token", audioArray.toString()));
         this.documentMongoCollection.updateOne(documentFilter, updateDocument);
         System.out.println("NLP für Video funktioniert: " + rede.getSpeechId());
      }
   }
}
