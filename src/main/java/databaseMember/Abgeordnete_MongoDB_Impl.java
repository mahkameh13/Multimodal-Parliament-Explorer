package databaseMember;

import com.mongodb.MongoWriteException;
import interfaces.AbgeordneteInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;

public class Abgeordnete_MongoDB_Impl implements AbgeordneteInterface {
   private Document document;
   private Map funktion = new HashMap();

   public Abgeordnete_MongoDB_Impl(Abgeordnete abgeordnete) {
      this.document = (new Document("_id", abgeordnete.getId())).append("vorname", abgeordnete.getVorname()).append("nachname", abgeordnete.getNachname()).append("anrede", abgeordnete.getAnrede()).append("geburtsdatum", abgeordnete.getGeburtsdatum()).append("geschlecht", abgeordnete.getGeschlecht()).append("religion", abgeordnete.getReligion()).append("beruf", abgeordnete.getBeruf()).append("partei", abgeordnete.getPartei()).append("fraktion", abgeordnete.getCurrentFraktion()).append("funktion", abgeordnete.getCurrentFunktion()).append("fraktionenlist", abgeordnete.getFraktionList());
      if (abgeordnete.getPictureMember() != null) {
         this.document.append("picture", abgeordnete.getPictureMember().toDocument());
      }

   }

   public Abgeordnete_MongoDB_Impl(Document document) {
      this.document = document;
   }

   public Document saveToDatabase() {
      return this.document;
   }

   public void getabgeordnete(Document document) {
      this.document = document;
   }

   public static void addAbgeordnete(MongoDatabaseHandler db, List<Abgeordnete> abgeordneteList) {
      for(Abgeordnete abgeordneter : abgeordneteList) {
         Abgeordnete_MongoDB_Impl abgeordneteMongoDB = new Abgeordnete_MongoDB_Impl(abgeordneter);

         try {
            db.create("members", abgeordneteMongoDB.saveToDatabase());
         } catch (MongoWriteException var6) {
            break;
         }
      }

   }

   public String getId() {
      return this.document.getString("_id");
   }

   public void setId(String id) {
      this.document.append("_id", id);
   }

   public String getVorname() {
      return this.document.getString("vorname");
   }

   public void setVorname(String vorname) {
      this.document.append("vorname", vorname);
   }

   public String getNachname() {
      return this.document.getString("nachname");
   }

   public void setNachname(String nachname) {
      this.document.append("nachname", nachname);
   }

   public String getAnrede() {
      return "";
   }

   public void setAnrede(String anrede) {
      this.document.append("anrede", anrede);
   }

   public String getGeburtsdatum() {
      return this.document.getString("geburtsdatum");
   }

   public void setGeburtsdatum(String geburtsdatum) {
      this.document.append("geburtsdatum", geburtsdatum);
   }

   public String getGeschlecht() {
      return this.document.getString("geschlecht");
   }

   public void setGeschlecht(String geschlecht) {
      this.document.append("geschlecht", geschlecht);
   }

   public String getReligion() {
      return this.document.getString("religion");
   }

   public void setReligion(String religion) {
      this.document.append("religion", religion);
   }

   public String getBeruf() {
      return this.document.getString("beruf");
   }

   public void setBeruf(String beruf) {
      this.document.append("beruf", beruf);
   }

   public String getPartei() {
      return this.document.getString("partei");
   }

   public void setPartei(String partei) {
      this.document.append("partei", partei);
   }

   public Map getFunktion() {
      return this.funktion;
   }

   public void setFunktion(Map funktion) {
      this.document.append("funktion", funktion);
   }

   public Map getFraktionenList() {
      return (Map)this.document.get("fraktionenlist");
   }
}
