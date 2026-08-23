package importer.memberdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ImporterConfigMember {
   private final String memberZipUrl;
   private final String memberDownloadDir;
   private final String memberExtractDir;
   private final String memberZipFilename;
   private final String memberXmlFilename;
   private final int memberLegislatureperiod;

   public ImporterConfigMember(String memberZipUrl, String memberDownloadDir, String memberExtractDir, String memberZipFilename, String memberXmlFilename, int memberLegislatureperiod) {
      this.memberZipUrl = memberZipUrl;
      this.memberDownloadDir = memberDownloadDir;
      this.memberExtractDir = memberExtractDir;
      this.memberZipFilename = memberZipFilename;
      this.memberXmlFilename = memberXmlFilename;
      this.memberLegislatureperiod = memberLegislatureperiod;
   }

   public String getMemberZipUrl() {
      return this.memberZipUrl;
   }

   public String getMemberDownloadDir() {
      return this.memberDownloadDir;
   }

   public String getMemberExtractDir() {
      return this.memberExtractDir;
   }

   public String getMemberZipFilename() {
      return this.memberZipFilename;
   }

   public String getMemberXmlFilename() {
      return this.memberXmlFilename;
   }

   public int getMemberLegislatureperiod() {
      return this.memberLegislatureperiod;
   }

   public static ImporterConfigMember fromResources(String propertiesFile) {
      Properties props = new Properties();

      try {
         InputStream in = ImporterConfigMember.class.getClassLoader().getResourceAsStream(propertiesFile);

         try {
            if (in == null) {
               throw new IllegalStateException("Resource not found: " + propertiesFile);
            }

            props.load(in);
         } catch (Throwable var9) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (in != null) {
            in.close();
         }
      } catch (IOException e) {
         throw new RuntimeException("Failed loading:" + propertiesFile, e);
      }

      String memberZipUrl = requireProp(props, "member.zip_url");
      String memberDownloadDir = requireProp(props, "member.download_dir");
      String memberExtractDir = requireProp(props, "member.extract_dir");
      String memberZipFilename = requireProp(props, "member.zip_filename");
      String memberXmlFilename = requireProp(props, "member.xml_filename");
      int memberLegislatureperiod = Integer.parseInt(requireProp(props, "member.legislature_period"));
      return new ImporterConfigMember(memberZipUrl, memberDownloadDir, memberExtractDir, memberZipFilename, memberXmlFilename, memberLegislatureperiod);
   }

   private static String requireProp(Properties props, String key) {
      String value = props.getProperty(key);
      if (value != null && !value.isBlank()) {
         return value.trim();
      } else {
         throw new IllegalStateException("Missing property:" + key);
      }
   }
}
