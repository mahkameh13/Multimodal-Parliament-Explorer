package importer.memberdata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MemberDownloader {
   private final ImporterConfigMember config;

   public MemberDownloader(ImporterConfigMember config) {
      this.config = config;
   }

   public Path downloadZip() {
      try {
         Path downloadDir = Path.of(this.config.getMemberDownloadDir());
         Files.createDirectories(downloadDir);
         Path zipPath = downloadDir.resolve(this.config.getMemberZipFilename());
         InputStream in = URI.create(this.config.getMemberZipUrl()).toURL().openStream();

         try {
            Files.copy(in, zipPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
         } catch (Throwable var7) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (in != null) {
            in.close();
         }

         return zipPath;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
