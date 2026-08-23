package databaseSpeech;

public class Agenda {
   private String index;
   private String id;
   private String title;

   public Agenda(String index, String id, String title) {
      this.index = index;
      this.id = id;
      this.title = title;
   }

   public String getIndex() {
      return this.index;
   }

   public String getId() {
      return this.id;
   }

   public String gettitle() {
      return this.title;
   }
}
