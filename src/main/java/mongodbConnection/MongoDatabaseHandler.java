package mongodbConnection;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDatabaseHandler implements AutoCloseable {
   private final MongoClient client;
   private final MongoDatabase db;

   public MongoDatabaseHandler(MongodbConfig config) {
      String connectionString = this.buildConnectionString(config);
      this.client = MongoClients.create(connectionString);
      this.db = this.client.getDatabase(config.getDatabase());
   }

   private String buildConnectionString(MongodbConfig c) {
      String var10000 = c.getUser();
      return "mongodb://" + var10000 + ":" + c.getPassword() + "@" + c.getHost() + ":" + c.getPort() + "/" + c.getDatabase();
   }

   public MongoDatabase getDatabase() {
      return this.db;
   }

   public MongoCollection getCollection(String collectionName) {
      return this.db.getCollection(collectionName);
   }

   public void close() {
      this.client.close();
   }

   public void create(String collectionName, Document doc) {
      this.getCollection(collectionName).insertOne(doc);
   }

   public Document readOne(String collectionName, Bson filter) {
      return (Document)this.getCollection(collectionName).find(filter).first();
   }

   public List readMany(String collectionName, Bson filter, int limit) {
      List<Document> result = new ArrayList();
      FindIterable<Document> iterable = this.getCollection(collectionName).find(filter);
      if (limit > 0) {
         iterable = iterable.limit(limit);
      }

      MongoCursor var6 = iterable.iterator();

      while(var6.hasNext()) {
         Document d = (Document)var6.next();
         result.add(d);
      }

      return result;
   }

   public UpdateResult updateOne(String collectionName, Bson filter, Bson update) {
      return this.getCollection(collectionName).updateOne(filter, update);
   }

   public UpdateResult updateMany(String collectionName, Bson filter, Bson update) {
      return this.getCollection(collectionName).updateMany(filter, update);
   }

   public DeleteResult deleteOne(String collectionName, Bson filter) {
      return this.getCollection(collectionName).deleteOne(filter);
   }

   public DeleteResult deleteMany(String collectionName, Bson filter) {
      return this.getCollection(collectionName).deleteMany(filter);
   }

   public void upsert(String collection, Bson filter, Document doc) {
      this.getCollection(collection).replaceOne(filter, doc, (new ReplaceOptions()).upsert(true));
   }

   public long count(String collectionName) {
      return this.getCollection(collectionName).countDocuments();
   }

   public long count(String collectionName, Bson filter) {
      return this.getCollection(collectionName).countDocuments(filter);
   }

   public List aggregate(String collectionName, List pipeline) {
      List<Document> result = new ArrayList();
      MongoCursor var4 = this.getCollection(collectionName).aggregate(pipeline).iterator();

      while(var4.hasNext()) {
         Document d = (Document)var4.next();
         result.add(d);
      }

      return result;
   }
}
