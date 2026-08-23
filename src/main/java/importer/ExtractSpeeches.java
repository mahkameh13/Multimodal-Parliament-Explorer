package importer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public class ExtractSpeeches {
   public ParsedProtocol parse(String sourceUrl, String xml) {
      Document d = Jsoup.parse(xml, "", Parser.xmlParser());
      String legislatureperiod = text(d.selectFirst("wahlperiode"));
      String session = firstNonEmpty(text(d.selectFirst("sitzungsnr")), text(d.selectFirst("sitzungsnummer")));
      String date = firstNonEmpty(text(d.selectFirst("datum")), text(d.selectFirst("sitzungsdatum")));
      List<ParsedSpeech> speeches = new ArrayList();

      for(Element speech : d.select("rede")) {
         String speechId = firstNonEmpty(attr(speech, "id"), attr(speech, "xml:id"));
         String speakerName = firstNonEmpty(text(speech.selectFirst("redner name")), text(speech.selectFirst("name")), text(speech.selectFirst("redner")));
         String fraction = firstNonEmpty(text(speech.selectFirst("fraktion")), text(speech.selectFirst("redner fraktion")));
         List<String> comments = extractComments(speech);
         String speechText = buildPlainSpeechText(speech);
         if (speechText.isEmpty()) {
            speechText = speech.text().trim();
         }

         if (!speechText.isBlank()) {
            String agendaItem = firstNonEmpty(text(speech.selectFirst("tagesordnungspunkt")), text(speech.selectFirst("top")), text(speech.selectFirst("tagesordnugnspunktbezeichnung")), text(speech.parent() != null ? speech.parent().selectFirst("tagesordnungspunkt") : null));
            String speechKey;
            if (!speechId.isBlank()) {
               speechKey = sourceUrl + "#" + speechId;
            } else {
               speechKey = sourceUrl + "#hash_" + sha1((speakerName == null ? "" : speakerName) + "|" + speechText);
            }

            speeches.add(new ParsedSpeech(speechKey, sourceUrl, speechId, speakerName, fraction, date, agendaItem, speechText, comments));
         }
      }

      return new ParsedProtocol(sourceUrl, legislatureperiod, session, date, speeches);
   }

   private static boolean isComment(Element el) {
      String tag = el.tagName().toLowerCase();
      if (!tag.equals("kommentar") && !tag.equals("comment") && !tag.equals("anmerkung")) {
         if (!tag.equals("zwischenruf") && !tag.equals("beifall") && !tag.equals("unruhe")) {
            String typ = el.attr("typ".toLowerCase());
            return typ.contains("kommentar") || typ.contains("zwischenruf") || typ.contains("beifall");
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private static List extractComments(Element speech) {
      List<String> comments = new ArrayList();

      for(Element el : speech.getAllElements()) {
         if (el != speech && isComment(el)) {
            String t = el.text().trim();
            if (!t.isBlank()) {
               comments.add(t);
            }
         }
      }

      return comments;
   }

   private static String buildPlainSpeechText(Element speech) {
      Element clone = speech.clone();

      for(Element el : new ArrayList(clone.getAllElements())) {
         if (el != clone && isComment(el)) {
            el.remove();
         }
      }

      String t = clone.select("p").text().trim();
      if (t.isBlank()) {
         t = clone.text().trim();
      }

      return t;
   }

   private static String sha1(String s) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
         StringBuilder sb = new StringBuilder();

         for(byte b : digest) {
            sb.append(String.format("%02x", b));
         }

         return sb.toString();
      } catch (Exception var8) {
         return Integer.toHexString(s.hashCode());
      }
   }

   private static String text(Element e) {
      return e == null ? "" : e.text().trim();
   }

   private static String attr(Element e, String a) {
      return e == null ? "" : e.attr(a).trim();
   }

   private static String firstNonEmpty(String... vals) {
      for(String v : vals) {
         if (v != null && !v.isBlank()) {
            return v;
         }
      }

      return "";
   }

   public static record ParsedProtocol(String sourceUrl, String legislatureperiod, String session, String date, List speeches) {
   }

   public static record ParsedSpeech(String speechKey, String protocolSourceUrl, String speechId, String speakerName, String fraction, String date, String agendaItem, String text, List comments) {
   }
}
