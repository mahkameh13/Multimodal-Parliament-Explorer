package main;

import InputDatabase.ReadSpeechAndProtocol;
import importer.FindData;
import importer.ProtocolTimer;
import importer.importerConfig;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class Test {
   public static void main(String[] args) {
      MongodbConfig cfg = MongodbConfig.fromResources("mongodb.properties");
      importerConfig importerCfg = importerConfig.fromResources("importwps.properties");
      Set<String> ajaxBaseUrls = importerCfg.getAjaxBaseUrls();

      try {
         MongoDatabaseHandler db = new MongoDatabaseHandler(cfg);

         label66: {
            try {
               Set<String> allLinks = new LinkedHashSet();

               for(String ajaxBaseUrl : ajaxBaseUrls) {
                  FindData finder = new FindData(ajaxBaseUrl);
                  List<String> xmlLinks = finder.findXmlLinks();
                  allLinks.addAll(xmlLinks);
               }

               List<String> xmlLinks = new ArrayList(allLinks);
               System.out.println("Found XML Link: " + xmlLinks.size());
               if (!xmlLinks.isEmpty()) {
                  ReadSpeechAndProtocol importer = new ReadSpeechAndProtocol(db);
                  int ok = 0;
                  int skipped = 0;
                  int failed = 0;

                  for(String url : xmlLinks) {
                     try {
                        if (importer.alreadyImported(url)) {
                           ++skipped;
                        } else {
                           importer.importOrUpdate(url);
                           ++ok;
                        }
                     } catch (Exception e) {
                        ++failed;
                        System.out.println("failed" + url);
                        e.printStackTrace();
                     }
                  }

                  System.out.println("Imported" + ok + "skipped" + skipped + "failed" + failed);
                  break label66;
               }

               System.out.println("No XML links found");
            } catch (Throwable var16) {
               try {
                  db.close();
               } catch (Throwable var14) {
                  var16.addSuppressed(var14);
               }

               throw var16;
            }

            db.close();
            return;
         }

         db.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      ProtocolTimer timer = new ProtocolTimer(cfg, importerCfg);
      timer.start(1L);
      System.out.println("Checking every mins.");
   }
}
