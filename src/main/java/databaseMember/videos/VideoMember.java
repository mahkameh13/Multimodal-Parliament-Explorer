package databaseMember.videos;

import org.bson.Document;

public class VideoMember {
   private String videoUrl;
   private String posterUrl;
   private String title;
   private String date;
   private String description;

   public VideoMember(String videoUrl, String posterUrl, String title, String date, String description) {
      this.videoUrl = videoUrl;
      this.posterUrl = posterUrl;
      this.title = title;
      this.date = date;
      this.description = description;
   }

   public String getVideoUrl() {
      return this.videoUrl;
   }

   public void setVideoUrl(String videoUrl) {
      this.videoUrl = videoUrl;
   }

   public String getPosterUrl() {
      return this.posterUrl;
   }

   public void setPosterUrl(String posterUrl) {
      this.posterUrl = posterUrl;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDate() {
      return this.date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Document toDocument() {
      Document doc = new Document();
      if (this.videoUrl != null) {
         doc.append("videoUrl", this.videoUrl);
      }

      if (this.posterUrl != null) {
         doc.append("posterUrl", this.posterUrl);
      }

      if (this.title != null) {
         doc.append("title", this.title);
      }

      if (this.date != null) {
         doc.append("date", this.date);
      }

      if (this.description != null) {
         doc.append("description", this.description);
      }

      return doc;
   }

   public String toString() {
      return "VideoMember{videoUrl='" + this.videoUrl + "', posterUrl=' " + this.posterUrl + "', title=' " + this.title + "', date=' " + this.date + "', description' " + this.description + "'}";
   }
}
