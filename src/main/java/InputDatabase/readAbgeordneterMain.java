package InputDatabase;

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

public class readAbgeordneterMain {
   public static void main(String[] args) {
      ImporterConfigMember memberConfig = ImporterConfigMember.fromResources("memberdate.properties");
      Path zipPath = new MemberDownloader(memberConfig).downloadZip();
      Path xmlPath = new MemberZipExtractor(memberConfig).extractXml(zipPath);
      ReadAbgeordnete reader = new ReadAbgeordnete();
      List<Abgeordnete> abgeordneteList = reader.ReadAbgeordnete(xmlPath.toFile());
      PrintStream var10001 = System.out;
      Objects.requireNonNull(var10001);
      abgeordneteList.forEach(var10001::println);
      MongodbConfig config = MongodbConfig.fromResources("mongodb.properties");
      MongoDatabaseHandler db = new MongoDatabaseHandler(config);
      Abgeordnete_MongoDB_Impl.addAbgeordnete(db, abgeordneteList);
   }
}
