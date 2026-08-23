package InputDatabase;

import databaseMember.Abgeordnete;
import databaseMember.GetPicture;
import databaseMember.PictureMember;
import importer.memberdata.ImporterConfigMember;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadAbgeordnete {
   public List ReadAbgeordnete(File file) {
      List<Abgeordnete> abgeordneteList = new ArrayList();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      ImporterConfigMember config = ImporterConfigMember.fromResources("memberdate.properties");
      String wp = String.valueOf(config.getMemberLegislatureperiod());

      try {
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(file);
         doc.getDocumentElement().normalize();
         NodeList mdblist = doc.getElementsByTagName("MDB");

         for(int i = 0; i < mdblist.getLength(); ++i) {
            Abgeordnete currentAbgeordneter = null;
            Map<String, String> funktionenList = new LinkedHashMap();
            Element mdb = (Element)mdblist.item(i);
            String id = mdb.getElementsByTagName("ID").item(0).getTextContent();
            String vorname = mdb.getElementsByTagName("VORNAME").item(0).getTextContent();
            String nachname = mdb.getElementsByTagName("NACHNAME").item(0).getTextContent();
            String anrede = mdb.getElementsByTagName("ANREDE_TITEL").item(0).getTextContent();
            String geburtsdatum = mdb.getElementsByTagName("GEBURTSDATUM").item(0).getTextContent();
            String religion = mdb.getElementsByTagName("RELIGION").item(0).getTextContent();
            String beruf = mdb.getElementsByTagName("BERUF").item(0).getTextContent();
            String geschlecht = mdb.getElementsByTagName("GESCHLECHT").item(0).getTextContent();
            String partei = mdb.getElementsByTagName("PARTEI_KURZ").item(0).getTextContent();
            NodeList wahlperiodiodeList = mdb.getElementsByTagName("WAHLPERIODE");

            for(int j = 0; j < wahlperiodiodeList.getLength(); ++j) {
               Element wahlperiod = (Element)wahlperiodiodeList.item(j);
               if (wp.equals(wahlperiod.getElementsByTagName("WP").item(0).getTextContent())) {
                  NodeList institutionen = wahlperiod.getElementsByTagName("INSTITUTION");

                  for(int k = 0; k < institutionen.getLength(); ++k) {
                     Element institution = (Element)institutionen.item(k);
                     if (institution.getElementsByTagName("INSART_LANG").item(0).getTextContent().equals("Fraktion/Gruppe")) {
                        String fraktionName = institution.getElementsByTagName("INS_LANG").item(0).getTextContent();
                        String funktion = institution.getElementsByTagName("FKT_LANG").item(0).getTextContent();
                        funktionenList.put(fraktionName, funktion);
                     }
                  }

                  PictureMember pic = GetPicture.getPicture(nachname, vorname);
                  currentAbgeordneter = new Abgeordnete(id, vorname, nachname, anrede, geburtsdatum, geschlecht, religion, beruf, funktionenList, partei, pic);
               }
            }

            if (currentAbgeordneter != null) {
               abgeordneteList.add(currentAbgeordneter);
            }
         }

         return abgeordneteList;
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (SAXException e) {
         throw new RuntimeException(e);
      }
   }
}
