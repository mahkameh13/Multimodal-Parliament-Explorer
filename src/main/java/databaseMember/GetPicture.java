package databaseMember;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class GetPicture {
   public static void main(String[] args) throws IOException {
      PictureMember pic = getPicture("Abelein", "Manfred");
      System.out.println(pic);
   }

   public static PictureMember getPicture(String nachname, String vorname) throws IOException {
      String url = "https://bilddatenbank.bundestag.de/search/picture-result?filterQuery%5Bname%5D%5B%5D=" + nachname + "%2C+" + vorname + "&sortVal=3";
      Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
      Elements images = doc.select("div.rowGridContainer div.item");
      Element firstImage = images.first();
      if (images.size() == 0) {
         return null;
      } else {
         Element aElement = firstImage.selectFirst("a");
         Element image = aElement.select("img").first();
         String dataCaption = aElement.attr("data-caption");
         String decoded = Parser.unescapeEntities(dataCaption, true);
         System.out.println(decoded);
         Document captionDoc = Jsoup.parse(decoded);
         Element contentDiv = captionDoc.selectFirst("div.col-md-9");
         String htmlText = contentDiv.html();
         String[] lines = contentDiv.html().split("<br>");
         String description = lines[0];
         System.out.println(description);
         String ort = null;
         String datum = null;
         String bildnummer = null;
         String fotograf = null;
         String persons = null;

         for(String line : lines) {
            line = line.trim();
            if (line.startsWith("Aufgenommen:")) {
               datum = line.replace("Aufgenommen:", "").trim();
               System.out.println("Datum: " + datum);
            } else if (line.startsWith("Bildnummer:")) {
               bildnummer = line.replace("Bildnummer:", "").trim();
               System.out.println("Bildnummer: " + bildnummer);
            } else if (line.startsWith("Fotograf/in:")) {
               fotograf = line.replace("Fotograf/in:", "").trim();
               System.out.println("Fotograf: " + fotograf);
            } else if (line.startsWith("Ort:")) {
               ort = line.replace("Ort:", "").trim();
               System.out.println("Ort: " + ort);
            } else if (line.startsWith("Abgebildete Personen:")) {
               persons = line.replace("Abgebildete Personen:", "").trim();
               System.out.println("Abgebildete Personen: " + persons);
            } else if (line.startsWith("Abgebildete Person:")) {
               persons = line.replace("Abgebildete Person:", "").trim();
               System.out.println("Abgebildete Person: " + persons);
            }
         }

         String imageUrl = image.absUrl("src");
         System.out.println(imageUrl);
         PictureMember pictureMember = new PictureMember(imageUrl, ort, datum, bildnummer, fotograf, persons, description);
         return pictureMember;
      }
   }
}
