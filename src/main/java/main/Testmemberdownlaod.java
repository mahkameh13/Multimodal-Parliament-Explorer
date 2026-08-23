package main;

import InputDatabase.ReadAbgeordnete;
import databaseMember.Abgeordnete;
import databaseMember.Abgeordnete_MongoDB_Impl;
import importer.memberdata.ImporterConfigMember;
import importer.memberdata.MemberDownloader;
import importer.memberdata.MemberZipExtractor;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class Testmemberdownlaod {
   public static void main(String[] args) {
      MongodbConfig mongoCfg = MongodbConfig.fromResources("mongodb.properties");
      ImporterConfigMember config = ImporterConfigMember.fromResources("memberdate.properties");

      try {
         MongoDatabaseHandler db = new MongoDatabaseHandler(mongoCfg);

         try {
            MemberDownloader downloader = new MemberDownloader(config);
            Path zipPath = downloader.downloadZip();
            MemberZipExtractor extractor = new MemberZipExtractor(config);
            Path xmlPath = extractor.extractXml(zipPath);
            System.out.println("Zip gespeichert unter: " + String.valueOf(zipPath));
            System.out.println("XML gefunden unter: " + String.valueOf(xmlPath));
            ReadAbgeordnete reader = new ReadAbgeordnete();
            List<Abgeordnete> members = reader.ReadAbgeordnete(xmlPath.toFile());
            PrintStream var10001 = System.out;
            Objects.requireNonNull(var10001);
            members.forEach(var10001::println);
            Abgeordnete_MongoDB_Impl.addAbgeordnete(db, members);
            System.out.println("Gefundene Mitglieder: " + members.size());
         } catch (Throwable var11) {
            try {
               db.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         db.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}
