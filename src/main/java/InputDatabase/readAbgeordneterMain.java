package InputDatabase;

import databaseMember.Abgeordnete;
import databaseMember.Abgeordnete_MongoDB_Impl;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import mongodbConnection.MongoDatabaseHandler;
import mongodbConnection.MongodbConfig;

public class readAbgeordneterMain {
   public static void main(String[] args) {
      File file = new File("src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML");
      ReadAbgeordnete reader = new ReadAbgeordnete();
      List<Abgeordnete> abgeordneteList = reader.ReadAbgeordnete(file);
      PrintStream var10001 = System.out;
      Objects.requireNonNull(var10001);
      abgeordneteList.forEach(var10001::println);
      MongodbConfig config = MongodbConfig.fromResources("mongodb.properties");
      MongoDatabaseHandler db = new MongoDatabaseHandler(config);
      Abgeordnete_MongoDB_Impl.addAbgeordnete(db, abgeordneteList);
   }
}
