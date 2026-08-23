package nlp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class Serialize {
   public static String serializeCasObj(JCas jcas) {
      ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
      XmiCasSerializer casSerializer = new XmiCasSerializer(jcas.getTypeSystem());
      XMLSerializer xmlSerializer = new XMLSerializer(arrayOutputStream, true);
      ContentHandler contentHandler = xmlSerializer.getContentHandler();

      try {
         casSerializer.serialize(jcas.getCas(), contentHandler);
      } catch (SAXException e) {
         throw new RuntimeException(e);
      }

      return arrayOutputStream.toString(StandardCharsets.UTF_8);
   }

   public static void deserializeCas(JCas resultCas, String xmi) {
      ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8));

      try {
         XmiCasDeserializer.deserialize(arrayInputStream, resultCas.getCas());
      } catch (SAXException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
