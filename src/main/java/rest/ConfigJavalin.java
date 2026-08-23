package rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigJavalin extends Properties {
   public ConfigJavalin(String filePaht) throws IOException {
      FileInputStream fis = new FileInputStream(filePaht);

      try {
         this.load(fis);
      } catch (Throwable var6) {
         try {
            fis.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      fis.close();
   }

   public int getServerPort() {
      return Integer.parseInt(this.getProperty("server.port"));
   }
}
