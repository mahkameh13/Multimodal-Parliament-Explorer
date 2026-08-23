package databaseSpeech;

public class Protocol {
   private long date;
   private long starttime;
   private long endtime;
   private long index;
   private String title;
   private String place;
   private int wp;

   public Protocol(long date, long starttime, long endtime, long index, String title, String place, int wp) {
      this.date = date;
      this.starttime = starttime;
      this.endtime = endtime;
      this.index = index;
      this.title = title;
      this.place = place;
      this.wp = wp;
   }

   public long getDate() {
      return this.date;
   }

   public long getStarttime() {
      return this.starttime;
   }

   public long getEndtime() {
      return this.endtime;
   }

   public long getIndex() {
      return this.index;
   }

   public String getTitle() {
      return this.title;
   }

   public String getPlace() {
      return this.place;
   }

   public int getWp() {
      return this.wp;
   }
}
