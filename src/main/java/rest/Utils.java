package rest;

import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;
import org.bson.conversions.Bson;

public class Utils {
   private static final String collectionName = "speeches_nlp";

   public static Document getSpeech(MongoDatabaseHandler db, String speechId) {
      Bson filter = Filters.eq("redeId", speechId);
      return db.readOne(collectionName, filter);
   }

   public static List<Document> getSpeeches(MongoDatabaseHandler db, String speechIds) {
      List<Document> speeches = new ArrayList<>();

      for(String speechId : speechIds.split(", ")) {
         speeches.add(getSpeech(db, speechId));
      }

      return speeches;
   }

   public static List<Document> getAll(MongoDatabaseHandler db) {
      List<Document> list = new ArrayList<>();
      db.getCollection(collectionName).find().into(list);
      return list;
   }

   public static Map<String, Integer> getPosAgg(List<Document> list) {
      List<Document> pos = new ArrayList<>();

      for(Document d : list) {
         pos.add((Document)((Document)((Document)d.get("nlp", Document.class)).get("stats", Document.class)).get("posCounts", Document.class));
      }

      Map<String, Integer> posCounts = new HashMap<>();

      for(Document d : pos) {
         for(String key : d.keySet()) {
            int value = d.getInteger(key);
            posCounts.put(key, (Integer)posCounts.getOrDefault(key, 0) + value);
         }
      }

      return posCounts;
   }

   public static Double getSentimentSpeech(Document speech) {
      Document nlp = (Document)speech.get("nlp");
      List<Document> sentences = (List)nlp.get("sentences");
      double sentimentAgg = (double)0.0F;

      for(Document sentence : sentences) {
         Double sentiment = sentence.getDouble("sentiment");
         sentimentAgg += sentiment;
      }

      sentimentAgg /= (double)sentences.size();
      return sentimentAgg;
   }

   public static Map<String, Double> getSentimentSpeech(List<Document> speeches) {
      Map<String, Double> sentiments = new HashMap<>();

      for(Document speech : speeches) {
         sentiments.put(speech.getString("redeId"), getSentimentSpeech(speech));
      }

      return sentiments;
   }

   public static Map<String, Integer> getEntities(Document speech) {
      Document nlp = (Document)speech.get("nlp");
      List<Document> sentences = (List)nlp.get("sentences");
      Map<String, Integer> entitiesCount = new HashMap<>();

      for(Document sentence : sentences) {
         List<Document> entities = (List)sentence.get("entities");
         if (entities != null) {
            for(Document entity : entities) {
               String type = entity.getString("type");
               entitiesCount.put(type, (Integer)entitiesCount.getOrDefault(type, 0) + 1);
            }
         }
      }

      return entitiesCount;
   }

   public static Map<String, Integer> getEntites(List<Document> speeches) {
      Map<String, Integer> entitiesCount = new HashMap<>();

      for(Document speech : speeches) {
         Map<String, Integer> speechCount = getEntities(speech);

         for(Map.Entry<String, Integer> entry : speechCount.entrySet()) {
            entitiesCount.merge(entry.getKey(), entry.getValue(), Integer::sum);
         }
      }

      return entitiesCount;
   }

   public static Map<String, Double> getTopic(Document speech) {
      Document nlp = (Document)speech.get("nlp");
      List<Document> topicsDoc = (List)nlp.get("topicsTop5");
      Map<String, Double> topicsMap = new HashMap<>();

      for(Document topic : topicsDoc) {
         topicsMap.put((String)topic.get("category", String.class), (Double)topic.get("score", Double.class));
      }

      return topicsMap;
   }

   public static Map<String, Double> getTopic(List<Document> speeches) {
      Map<String, Double> topicsMap = new HashMap<>();

      for(Document speech : speeches) {
         Map<String, Double> topics = getTopic(speech);
         Map.Entry<String, Double> maxEntry = (Map.Entry)Collections.max(topics.entrySet(), Entry.comparingByValue());
         topicsMap.put((String)maxEntry.getKey(), (Double)topicsMap.getOrDefault(maxEntry.getKey(), (double)0.0F) + (double)1.0F);
      }

      topicsMap.replaceAll((key, value) -> value / (double)speeches.size());
      return topicsMap;
   }

   public static List<Document> filterByTopic(List<Document> speeches, String topic) {
      List<Document> resultList = new ArrayList<>();

      for(Document document : speeches) {
         Map<String, Double> map = getTopic(document);
         Map.Entry<String, Double> maxEntry = (Map.Entry)Collections.max(map.entrySet(), Entry.comparingByValue());
         if (Objects.equals(maxEntry.getKey(), topic)) {
            resultList.add(document);
         }
      }

      return resultList;
   }

   public static List<Document> filterByDateRange(List<Document> speeches, String fromDate, String toDate) {
      if (fromDate == null && toDate == null) {
         return speeches;
      } else {
         List<Document> result = new ArrayList<>();

         for(Document d : speeches) {
            String date = d.getString("date");
            if (date == null) {
               date = d.getString("datum");
            }

            if (date != null && (fromDate == null || date.compareTo(fromDate) >= 0) && (toDate == null || date.compareTo(toDate) <= 0)) {
               result.add(d);
            }
         }

         return result;
      }
   }

   public static List<Document> filterByFraction(List<Document> speeches, String fraction) {
      if (fraction != null && !fraction.isBlank()) {
         List<Document> result = new ArrayList<>();

         for(Document d : speeches) {
            String f = d.getString("fraktion");
            if (f == null) {
               f = d.getString("fraction");
            }

            if (fraction.equalsIgnoreCase(f)) {
               result.add(d);
            }
         }

         return result;
      } else {
         return speeches;
      }
   }
}
