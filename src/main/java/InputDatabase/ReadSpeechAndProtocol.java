package InputDatabase;

import com.mongodb.client.model.Filters;
import importer.DownloadData;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import mongodbConnection.MongoDatabaseHandler;
import org.bson.Document;

public class ReadSpeechAndProtocol {
   private final MongoDatabaseHandler db;
   private final DownloadData downloader = new DownloadData();
   private final ExtractSpeeches extractor = new ExtractSpeeches();

   public ReadSpeechAndProtocol(MongoDatabaseHandler db) {
      this.db = db;
   }

   public boolean alreadyImported(String sourceUrl) {
      return this.db.readOne("protocols", Filters.eq("sourceUrl", sourceUrl)) != null;
   }

   public void importOrUpdate(String sourceUrl) throws Exception {
      String xml = this.downloader.downloadAsString(sourceUrl);
      ExtractSpeeches.ParsedProtocol parsed = this.extractor.parse(sourceUrl, xml);
      Document doc = (new Document()).append("sourceUrl", sourceUrl).append("downloadedAt", Instant.now().toString()).append("xml", xml).append("wahlperiode", parsed.legislatureperiod()).append("sitzung", parsed.session()).append("datum", parsed.date());
      this.db.upsert("protocols", Filters.eq("sourceUrl", sourceUrl), doc);
      Set<String> seenKeys = new HashSet();
      int speechCount = 0;

      for(ExtractSpeeches.ParsedSpeech s : parsed.speeches()) {
         seenKeys.add(s.speechKey());
         Document speechDoc = (new Document()).append("speechKey", s.speechKey()).append("protocolSourceUrl", s.protocolSourceUrl()).append("redeId", s.speechId()).append("speakerName", s.speakerName()).append("fraktion", s.fraction()).append("datum", s.date()).append("tagesordnungspunkt", s.agendaItem()).append("text", s.text()).append("comments", s.comments());
         this.db.upsert("speeches", Filters.eq("speechKey", s.speechKey()), speechDoc);
         ++speechCount;
      }

      System.out.println("Imported protocol:" + sourceUrl + " | speeches: " + speechCount);
   }
}
