package importer;

import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;

public class DeleteDatabase {
   public static void deleteDatabase(MongoDatabaseHandler db) {
      db.deleteMany("protocols", new Document());
      db.deleteMany("speeches", new Document());
   }
}
