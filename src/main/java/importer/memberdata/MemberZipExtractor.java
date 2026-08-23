package importer.memberdata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MemberZipExtractor {
   private final ImporterConfigMember config;

   public MemberZipExtractor(ImporterConfigMember config) {
      this.config = config;
   }

   public Path extractXml(Path zipPath) {
      try {
         Path extractDir = Path.of(this.config.getMemberExtractDir());
         Files.createDirectories(extractDir);
         Path xmlPath = null;
         InputStream fis = Files.newInputStream(zipPath);

         try {
            ZipInputStream zis = new ZipInputStream(fis);

            ZipEntry entry;
            try {
               for(; (entry = zis.getNextEntry()) != null; zis.closeEntry()) {
                  Path outPath = extractDir.resolve(entry.getName()).normalize();
                  if (!outPath.startsWith(extractDir)) {
                     throw new IOException(entry.getName() + " ungültig");
                  }

                  if (entry.isDirectory()) {
                     Files.createDirectories(outPath);
                  } else {
                     if (outPath.getParent() != null) {
                        Files.createDirectories(outPath.getParent());
                     }

                     Files.copy(zis, outPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                     if (entry.getName().equalsIgnoreCase(this.config.getMemberXmlFilename())) {
                        xmlPath = outPath;
                     }
                  }
               }
            } catch (Throwable var10) {
               try {
                  zis.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            zis.close();
         } catch (Throwable var11) {
            if (fis != null) {
               try {
                  fis.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (fis != null) {
            fis.close();
         }

         if (xmlPath == null) {
            throw new IllegalStateException("XML_Datei nicht gefunden");
         } else {
            return xmlPath;
         }
      } catch (IOException e) {
         throw new RuntimeException("Zip konnte nicht entpackt werden", e);
      }
   }
}
