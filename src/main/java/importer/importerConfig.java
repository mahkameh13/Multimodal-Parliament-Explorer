package importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class importerConfig {
   private final Set<String> ajaxBaseUrls;

   public importerConfig(Set<String> ajaxBaseUrls) {
      this.ajaxBaseUrls = ajaxBaseUrls;
   }

   public Set<String> getAjaxBaseUrls() {
      return this.ajaxBaseUrls;
   }

   public static importerConfig fromResources(String propertiesFile) {
      Properties props = new Properties();

      try {
         InputStream in = importerConfig.class.getClassLoader().getResourceAsStream(propertiesFile);

         try {
            if (in == null) {
               throw new IllegalStateException("Resource not found: " + propertiesFile);
            }

            props.load(in);
         } catch (Throwable var6) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (in != null) {
            in.close();
         }
      } catch (IOException e) {
         throw new RuntimeException("Failed loading:" + propertiesFile, e);
      }

      String rawUrl = requireProp(props, "ajax_base_urls");
      Set<String> urls = parseCsvSet(rawUrl);
      return new importerConfig(urls);
   }

   private static Set<String> parseCsvSet(String raw) {
      Set<String> set = new LinkedHashSet<>();

      for(String part : raw.split(",")) {
         String s = part.trim();
         if (!s.isEmpty()) {
            set.add(s);
         }
      }

      return set;
   }

   private static String requireProp(Properties props, String key) {
      String value = props.getProperty(key);
      if (value != null && !value.isBlank()) {
         return value.trim();
      } else {
         throw new IllegalStateException("Missing property:" + key);
      }
   }
}
