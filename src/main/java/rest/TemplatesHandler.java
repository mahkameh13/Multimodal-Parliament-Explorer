package rest;

import freemarker.template.Configuration;
import java.io.File;
import java.io.IOException;

public class TemplatesHandler {
   private Configuration cfg = null;
   private RestHandler restHandler = null;

   public TemplatesHandler(RestHandler rest, String path) throws IOException {
      this.cfg = new Configuration(Configuration.VERSION_2_3_23);
      this.cfg.setDirectoryForTemplateLoading(new File(path));
      this.restHandler = rest;
   }

   public Configuration getConfiguration() {
      return this.cfg;
   }
}
