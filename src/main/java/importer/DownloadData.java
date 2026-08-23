package importer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class DownloadData {
   private final HttpClient client = HttpClient.newHttpClient();

   public String downloadAsString(String url) throws IOException, InterruptedException {
      HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
      HttpResponse<String> resp = this.client.send(req, BodyHandlers.ofString());
      if (resp.statusCode() != 200) {
         int var10002 = resp.statusCode();
         throw new IOException("HTTP " + var10002 + " for " + url);
      } else {
         return (String)resp.body();
      }
   }
}
