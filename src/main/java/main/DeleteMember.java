package main;

import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class DeleteMember {
   public static void main(String[] args) {
      MongodbConfig config = MongodbConfig.fromResources("mongodb.properties");

      try {
         MongoDatabaseHandler db = new MongoDatabaseHandler(config);

         try {
            db.getCollection("abgeordnete").drop();
            System.out.println("Abgeordnete erfolgreich gelöscht");
         } catch (Throwable var6) {
            try {
               db.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         db.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}
