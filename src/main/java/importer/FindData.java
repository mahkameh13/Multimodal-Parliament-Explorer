package importer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FindData {
   private final HttpClient client = HttpClient.newHttpClient();
   private final String ajaxBaseUrls;

   public FindData(String ajaxBaseUrls) {
      this.ajaxBaseUrls = ajaxBaseUrls;
   }

   public List findXmlLinks() throws IOException, InterruptedException {
      Set<String> links = new LinkedHashSet();
      int offset = 0;
      int limit = 50;

      while(true) {
         String url = this.ajaxBaseUrls + "?noFilterSet=true&limit=" + limit + "&offset=" + offset;
         HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "Mozilla/5.0").GET().build();
         HttpResponse<String> resp = this.client.send(req, BodyHandlers.ofString());
         if (resp.statusCode() != 200) {
            int var10002 = resp.statusCode();
            throw new IOException("HTTP" + var10002 + " for " + url);
         }

         Document doc = Jsoup.parse((String)resp.body());
         List<Element> aTags = doc.select("a[href$=.xml]");

         for(Element a : aTags) {
            String xmlUrl = a.absUrl("href").trim();
            if (!xmlUrl.isBlank()) {
               links.add(xmlUrl);
            }
         }

         Element meta = doc.selectFirst("div.meta-slider[data-nextoffset]");
         if (meta == null) {
            break;
         }

         String next = meta.attr("data-nextoffset").trim();
         if (next.isEmpty()) {
            break;
         }

         int nextOffset = Integer.parseInt(next);
         if (nextOffset <= offset || aTags.isEmpty()) {
            break;
         }

         offset = nextOffset;
      }

      return new ArrayList(links);
   }
}
