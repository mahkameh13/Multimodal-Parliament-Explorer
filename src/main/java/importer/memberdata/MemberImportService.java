package importer.memberdata;

import com.mongodb.client.model.ReplaceOptions;
import databaseMember.Member;
import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;

public class MemberImportService {
   private final MongoDatabaseHandler db;

   public MemberImportService(MongoDatabaseHandler db) {
      this.db = db;
   }

   public void importOrUpdate(Member member) {
      Document doc = (new Document()).append("id", member.getId()).append("vorname", member.getVorname()).append("nachname", member.getNachname()).append("anrede", member.getAnrede()).append("partei", member.getPartei()).append("fraktionslist", member.getFraktionList()).append("currentfraktion", member.getCurrentFraktion()).append("geburtsdatum", member.getGeburtsdatum()).append("geschlecht", member.getGeschlecht()).append("religion", member.getReligion()).append("beruf", member.getBeruf()).append("currentFunktion", member.getCurrentFunktion());
      this.db.getCollection("abgeordnete").replaceOne(new Document("id", member.getId()), doc, (new ReplaceOptions()).upsert(true));
   }
}
