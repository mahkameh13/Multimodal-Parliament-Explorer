package mongodbConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongodbConfig {
   private String host;
   private int port;
   private String user;
   private String database;
   private String password;

   public MongodbConfig(String host, int port, String user, String database, String password) {
      this.host = host;
      this.port = port;
      this.user = user;
      this.database = database;
      this.password = password;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getUser() {
      return this.user;
   }

   public String getDatabase() {
      return this.database;
   }

   public String getPassword() {
      return this.password;
   }

   public static MongodbConfig fromResources(String propertiesFile) {
      Properties props = new Properties();

      try {
         InputStream in = MongodbConfig.class.getClassLoader().getResourceAsStream(propertiesFile);

         try {
            if (in == null) {
               throw new IllegalStateException("Resource not found");
            }

            props.load(in);
         } catch (Throwable var8) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (in != null) {
            in.close();
         }
      } catch (IOException var9) {
         throw new RuntimeException("Failed loading");
      }

      String host = requireProp(props, "remote_host");
      String database = requireProp(props, "remote_database");
      String user = requireProp(props, "remote_user");
      String password = requireProp(props, "remote_password");
      int port = Integer.parseInt(requireProp(props, "remote_port"));
      return new MongodbConfig(host, port, user, database, password);
   }

   private static String requireProp(Properties props, String key) {
      String value = props.getProperty(key);
      if (value != null && !value.isBlank()) {
         return value.trim();
      } else {
         throw new IllegalStateException("Missing property" + key);
      }
   }
}
