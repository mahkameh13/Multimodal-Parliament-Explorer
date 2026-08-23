package databaseMember.videos;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetVideo {
   public static void main(String[] args) throws IOException {
      String videoPageUrl = "URL OPTIONAL MIT PROPERTIES NOCH MacHEN";
      VideoMember video = getVideo(videoPageUrl);
      System.out.println(video);
   }

   public static VideoMember getVideo(String pageUrl) throws IOException {
      if (pageUrl != null && !pageUrl.isBlank()) {
         if (isDirectVideoUrl(pageUrl)) {
            return new VideoMember(pageUrl, (String)null, (String)null, (String)null, "Direkte VideoUrl");
         } else {
            Document doc = Jsoup.connect(pageUrl).userAgent("Mozilla/5.0").get();
            String title = extractTitle(doc);
            String description = extractDescription(doc);
            String date = extractDate(doc);
            String videoUrl = extractVideoUrl(doc);
            String posterUrl = extractPosterUrl(doc);
            return new VideoMember(videoUrl, posterUrl, title, date, description);
         }
      } else {
         throw new IllegalArgumentException("page Url darf nicht leer sein");
      }
   }

   public static boolean isDirectVideoUrl(String url) {
      String lower = url.toLowerCase();
      return lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".m3u8");
   }

   public static String extractVideoUrl(Document doc) {
      Elements videos = doc.select("video[src]");
      if (!videos.isEmpty()) {
         String src = videos.first().absUrl("src");
         if (!src.isBlank()) {
            return src;
         }
      }

      for(Element source : doc.select("video source[src]")) {
         String src = source.absUrl("src");
         if (!src.isBlank()) {
            return src;
         }
      }

      String ogVideo = metaContent(doc, "meta[property=og:video]");
      if (!ogVideo.isBlank()) {
         return ogVideo;
      } else {
         String ogVideoSecure = metaContent(doc, "meta[property=og:video:secure_url]");
         if (!ogVideoSecure.isBlank()) {
            return ogVideoSecure;
         } else {
            for(Element link : doc.select("[href]")) {
               String href = link.absUrl("href");
               if (isDirectVideoUrl(href)) {
                  return href;
               }
            }

            return null;
         }
      }
   }

   private static String extractPosterUrl(Document doc) {
      String ogImage = metaContent(doc, "meta[properts=og:image]");
      if (!ogImage.isBlank()) {
         return ogImage;
      } else {
         Element video = doc.selectFirst("video[poster]");
         if (video != null) {
            String poster = video.absUrl("poster");
            if (!poster.isBlank()) {
               return poster;
            }
         }

         return null;
      }
   }

   private static String extractTitle(Document doc) {
      String ogTitle = metaContent(doc, "meta[properts=og:title]");
      if (!ogTitle.isBlank()) {
         return ogTitle;
      } else {
         Element title = doc.selectFirst("title");
         return title != null ? title.text().trim() : null;
      }
   }

   private static String extractDescription(Document doc) {
      String ogDescription = metaContent(doc, "meta[properts=og:description]");
      if (!ogDescription.isBlank()) {
         return ogDescription;
      } else {
         String metaDescription = metaContent(doc, "meta[name=description]");
         return !metaDescription.isBlank() ? metaDescription : null;
      }
   }

   private static String extractDate(Document doc) {
      String articleDate = metaContent(doc, "meta[property= article: published_time]");
      if (!articleDate.isBlank()) {
         return articleDate;
      } else {
         String date = metaContent(doc, "meta[name=date]");
         if (!date.isBlank()) {
            return date;
         } else {
            Element time = doc.selectFirst("time[datetime]");
            if (time != null) {
               String dt = time.attr("datetime").trim();
               if (!dt.isBlank()) {
                  return dt;
               }
            }

            return null;
         }
      }
   }

   private static String metaContent(Document doc, String cssQuery) {
      Element meta = doc.selectFirst(cssQuery);
      if (meta == null) {
         return "";
      } else {
         String content = meta.attr("content");
         return content == null ? "" : content.trim();
      }
   }
}
