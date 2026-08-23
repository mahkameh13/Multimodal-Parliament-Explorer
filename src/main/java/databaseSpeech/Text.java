package databaseSpeech;

public class Text {
   private String id;
   private String content;
   private String type;

   public Text(String id, String content, String type) {
      this.id = id;
      this.content = content;
      this.type = type;
   }

   public String getId() {
      return this.id;
   }

   public String getContent() {
      return this.content;
   }

   public String getType() {
      return this.type;
   }
}
