package databaseSpeech;

public class Comment {
   private String id;
   private String content;
   private String speaker;

   public Comment(String id, String content, String speaker) {
      this.id = id;
      this.content = content;
   }

   public String getId() {
      return this.id;
   }

   public String getContent() {
      return this.content;
   }

   public String getSpeaker() {
      return this.speaker;
   }
}
