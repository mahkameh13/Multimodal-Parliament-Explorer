package importer;

import InputDatabase.ReadSpeechAndProtocol;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class ProtocolTimer {
   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
   private final MongoDatabaseHandler db;
   private final Set<String> ajaxBaseUrls;

   public ProtocolTimer(MongodbConfig config, importerConfig impConfig) {
      this.db = new MongoDatabaseHandler(config);
      this.ajaxBaseUrls = impConfig.getAjaxBaseUrls();
   }

   public void start(long periodMinutes) {
      this.scheduler.scheduleAtFixedRate(() -> runOnce(this.db, this.ajaxBaseUrls), 0L, periodMinutes, TimeUnit.MINUTES);
   }

   private static void runOnce(MongoDatabaseHandler db, Set<String> ajaxBaseUrls) {
      try {
         ReadSpeechAndProtocol importer = new ReadSpeechAndProtocol(db);
         int imported = 0;
         int skipped = 0;
         int totalLinks = 0;

         for(String ajaxBaseUrl : ajaxBaseUrls) {
            FindData finder = new FindData(ajaxBaseUrl);
            List<String> xmlLinks = finder.findXmlLinks();
            totalLinks += xmlLinks.size();

            for(String url : xmlLinks) {
               if (importer.alreadyImported(url)) {
                  ++skipped;
               } else {
                  importer.importOrUpdate(url);
               }
            }
         }

         System.out.println("Done.Links =" + totalLinks + " newImported=" + imported + " skipped" + skipped);
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
         e.printStackTrace();
      }

   }
}
