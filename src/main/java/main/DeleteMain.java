package main;

import importer.DeleteDatabase;
import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class DeleteMain {
   public static void main(String[] args) {
      MongodbConfig config = MongodbConfig.fromResources("mongodb.properties");
      MongoDatabaseHandler db = new MongoDatabaseHandler(config);
      DeleteDatabase.deleteDatabase(db);
   }
}
