package rest;

import java.io.IOException;
import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class testHandler {
   public static void main(String[] args) throws IOException {
      MongodbConfig cfg = MongodbConfig.fromResources("mongodb.properties");
      MongoDatabaseHandler db = new MongoDatabaseHandler(cfg);
      ConfigJavalin javalinConf = new ConfigJavalin("src/main/resources/javalin.properties");
      new RestHandler(javalinConf, db);
   }
}
