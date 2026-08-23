package databaseMember;

import org.bson.Document;

public class PictureMember {
   private String url;
   private String ort;
   private String date;
   private String picNumber;
   private String photographer;
   private String persons;
   private String description;

   public PictureMember(String url, String ort, String date, String picNumber, String photographer, String persons, String description) {
      this.url = url;
      this.ort = ort;
      this.date = date;
      this.picNumber = picNumber;
      this.photographer = photographer;
      this.persons = persons;
      this.description = description;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getOrt() {
      return this.ort;
   }

   public void setOrt(String ort) {
      this.ort = ort;
   }

   public String getDate() {
      return this.date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getPicNumber() {
      return this.picNumber;
   }

   public void setPicNumber(String picNumber) {
      this.picNumber = picNumber;
   }

   public String getPhotographer() {
      return this.photographer;
   }

   public void setPhotographer(String photographer) {
      this.photographer = photographer;
   }

   public String getPersons() {
      return this.persons;
   }

   public void setPersons(String persons) {
      this.persons = persons;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Document toDocument() {
      Document doc = new Document();
      if (this.url != null) {
         doc.append("url", this.url);
      }

      if (this.description != null) {
         doc.append("description", this.description);
      }

      if (this.persons != null) {
         doc.append("persons", this.persons);
      }

      if (this.photographer != null) {
         doc.append("photographer", this.photographer);
      }

      if (this.date != null) {
         doc.append("date", this.date);
      }

      if (this.picNumber != null) {
         doc.append("picNumber", this.picNumber);
      }

      if (this.ort != null) {
         doc.append("ort", this.ort);
      }

      return doc;
   }

   public String toString() {
      return "PictureMember{url='" + this.url + "', ort='" + this.ort + "', date='" + this.date + "', picNumber='" + this.picNumber + "', photographer='" + this.photographer + "', persons='" + this.persons + "', description='" + this.description + "'}";
   }
}
