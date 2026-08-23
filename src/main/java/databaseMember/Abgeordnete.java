package databaseMember;

import interfaces.AbgeordneteInterface;
import java.util.Map;

public class Abgeordnete implements AbgeordneteInterface {
   private String id;
   private String vorname;
   private String nachname;
   private String anrede;
   private String geburtsdatum;
   private String geschlecht;
   private String religion;
   private String beruf;
   private String partei;
   private Map fraktionList;
   private String currentFraktion;
   private String currentFunktion;
   private PictureMember pictureMember;

   public Abgeordnete(String id, String vorname, String nachname, String anrede, String geburtsdatum, String geschlecht, String religion, String beruf, Map funktion, String partei, PictureMember pictureMember) {
      this.id = id;
      this.vorname = vorname;
      this.nachname = nachname;
      this.anrede = anrede;
      this.geburtsdatum = geburtsdatum;
      this.geschlecht = geschlecht;
      this.religion = religion;
      this.beruf = beruf;
      this.fraktionList = funktion;
      this.partei = partei;
      this.currentFraktion = (String)funktion.keySet().stream().reduce((a, b) -> b).orElse((Object)null);
      this.currentFunktion = (String)funktion.get(this.currentFraktion);
      this.pictureMember = pictureMember;
   }

   public String getCurrentFunktion() {
      return this.currentFunktion;
   }

   public String getCurrentFraktion() {
      return this.currentFraktion;
   }

   public void setCurrentFraktion(String currentFraktion) {
      this.currentFraktion = currentFraktion;
   }

   public String getVorname() {
      return this.vorname;
   }

   public void setVorname(String vorname) {
      this.vorname = vorname;
   }

   public String getNachname() {
      return this.nachname;
   }

   public void setNachname(String nachname) {
      this.nachname = nachname;
   }

   public String getAnrede() {
      return this.anrede;
   }

   public void setAnrede(String anrede) {
      this.anrede = anrede;
   }

   public String getGeburtsdatum() {
      return this.geburtsdatum;
   }

   public void setGeburtsdatum(String geburtsdatum) {
      this.geburtsdatum = geburtsdatum;
   }

   public String getGeschlecht() {
      return this.geschlecht;
   }

   public void setGeschlecht(String geschlecht) {
      this.geschlecht = geschlecht;
   }

   public String getReligion() {
      return this.religion;
   }

   public void setReligion(String religion) {
      this.religion = religion;
   }

   public PictureMember getPictureMember() {
      return this.pictureMember;
   }

   public void setPictureMember(PictureMember pictureMember) {
      this.pictureMember = pictureMember;
   }

   public String getBeruf() {
      return this.beruf;
   }

   public void setBeruf(String beruf) {
      this.beruf = beruf;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getPartei() {
      return this.partei;
   }

   public void setPartei(String partei) {
      this.partei = partei;
   }

   public void setFraktionList(Map fraktionList) {
      this.fraktionList = fraktionList;
   }

   public Map getFraktionList() {
      return this.fraktionList;
   }

   public String toString() {
      String var10000 = this.id;
      return var10000 + this.vorname + " " + this.nachname + ", " + this.geburtsdatum + ", " + this.religion + ", " + this.beruf + "," + this.partei + ", " + String.valueOf(this.getFraktionList());
   }
}
