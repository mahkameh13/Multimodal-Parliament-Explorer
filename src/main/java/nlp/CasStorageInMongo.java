package nlp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.Date;
import org.bson.Document;

public class CasStorageInMongo implements CasStorage {
   private final MongoCollection collection;

   public CasStorageInMongo(MongoCollection collection) {
      this.collection = collection;
      System.out.println("CasStorage initialized for collection: " + String.valueOf(collection.getNamespace()));
   }

   public boolean checkCasXmi(String speechId) {
      try {
         Document document = (Document)this.collection.find(Filters.eq("redeId", speechId)).projection(new Document("cas_xmi", 1)).first();
         if (document == null) {
            System.out.println("No document found for rid: " + speechId);
            return false;
         } else if (!document.containsKey("cas_xmi")) {
            System.out.println("Document found but no cas_xmi field for rid: " + speechId);
            return false;
         } else {
            String xmi = document.getString("cas_xmi");
            boolean exists = xmi != null && !xmi.trim().isEmpty();
            System.out.println("CAS XMI exists for " + speechId + ": " + exists + " (length: " + (xmi != null ? xmi.length() : 0) + ")");
            return exists;
         }
      } catch (Exception e) {
         System.err.println("Error checking CAS XMI for " + speechId + ": " + e.getMessage());
         return false;
      }
   }

   public String loadXmi(String speechId) {
      try {
         Document document = (Document)this.collection.find(Filters.eq("redeId", speechId)).projection(new Document("cas_xmi", 1)).first();
         if (document == null) {
            System.out.println("No document found when loading XMI for: " + speechId);
            return null;
         } else {
            String xmi = document.getString("cas_xmi");
            System.out.println("Loaded XMI for " + speechId + ", length: " + (xmi != null ? xmi.length() : 0));
            return xmi;
         }
      } catch (Exception e) {
         System.err.println("Error loading XMI for " + speechId + ": " + e.getMessage());
         return null;
      }
   }

   public void saveCasXmi(String speechId, String xmi) {
      try {
         System.out.println("Saving CAS XMI for " + speechId + ", XMI length: " + xmi.length());
         this.collection.updateOne(new Document("redeId", speechId), new Document("$set", (new Document("cas_xmi", xmi)).append("cas_updated", new Date())));
         System.out.println("CAS XMI saved successfully for: " + speechId);
      } catch (Exception e) {
         System.err.println("Error saving CAS XMI for " + speechId + ": " + e.getMessage());
         throw new RuntimeException("Failed to save CAS XMI", e);
      }
   }
}
