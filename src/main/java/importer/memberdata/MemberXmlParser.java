package importer.memberdata;

import databaseMember.Member;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MemberXmlParser {
   public List parse(Path xmlPath, int allowedWps) {
      List<Member> members = new ArrayList();

      try {
         Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlPath.toFile());
         document.getDocumentElement().normalize();
         NodeList mdbNodes = document.getElementsByTagName("MDB");
         System.out.println("MDB count: " + mdbNodes.getLength());

         for(int i = 0; i < mdbNodes.getLength(); ++i) {
            Node node = mdbNodes.item(i);
            if (node.getNodeType() == 1) {
               Element mdbElement = (Element)node;
               Member member = new Member();
               member.setId(this.getText(mdbElement, "ID"));
               member.setAnrede(this.getText(mdbElement, "ANREDE_TITEL"));
               member.setGeburtsdatum(this.getText(mdbElement, "GEBURTSDATUM"));
               member.setReligion(this.getText(mdbElement, "RELIGION"));
               member.setBeruf(this.getText(mdbElement, "BERUF"));
               member.setGeschlecht(this.getText(mdbElement, "GESCHLECHT"));
               Element namen = this.getFirstElement(mdbElement, "NAMEN");
               if (namen != null) {
                  Element name = this.getFirstElement(namen, "NAME");
                  if (name != null) {
                     member.setVorname(this.getText(name, "VORNAME"));
                     member.setNachname(this.getText(name, "NACHNAME"));
                  }
               }

               String partei = this.getText(mdbElement, "PARTEI_KURZ");
               if (partei == null) {
                  partei = this.getText(mdbElement, "PARTEI");
               }

               member.setPartei(partei);
               Map<String, String> fraktionen = new LinkedHashMap();
               boolean hasAllowedWp = false;
               NodeList institutionen = mdbElement.getElementsByTagName("INSTITUTIONEN");

               for(int j = 0; j < institutionen.getLength(); ++j) {
                  Node instNode = institutionen.item(j);
                  if (instNode.getNodeType() == 1) {
                     Element instElement = (Element)instNode;
                     String wpText = this.getText(instElement, "WP");
                     String art = this.getText(instElement, "INSART_LANG");
                     String name = this.getText(instElement, "INS_LANG");
                     String funktion = this.getText(instElement, "FKT_LANG");
                     Integer wp = this.parseInteger(wpText);
                     if (wp != null && wp == allowedWps) {
                        hasAllowedWp = true;
                        if (art != null && art.toLowerCase().contains("fraktion") && name != null) {
                           if (funktion == null) {
                              funktion = "Mitglied";
                           }

                           fraktionen.put(name.trim(), funktion.trim());
                        }
                     }
                  }
               }

               if (hasAllowedWp) {
                  member.setFraktionList(fraktionen);
                  if (!fraktionen.isEmpty()) {
                     String lastFraktion = (String)(new ArrayList(fraktionen.keySet())).get(fraktionen.size() - 1);
                     member.setCurrentFraktion(lastFraktion);
                     member.setCurrentFunktion((String)fraktionen.get(lastFraktion));
                  }

                  members.add(member);
               }
            }
         }

         return members;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private Element getFirstElement(Element parent, String tag) {
      NodeList list = parent.getElementsByTagName(tag);
      return list.getLength() == 0 ? null : (Element)list.item(0);
   }

   private String getText(Element parent, String tag) {
      NodeList list = parent.getElementsByTagName(tag);
      if (list.getLength() == 0) {
         return null;
      } else {
         Node node = list.item(0);
         if (node == null) {
            return null;
         } else {
            String text = node.getTextContent();
            if (text == null) {
               return null;
            } else {
               text = text.trim();
               return text.isEmpty() ? null : text;
            }
         }
      }
   }

   private Integer parseInteger(String text) {
      if (text != null && !text.isBlank()) {
         try {
            return Integer.parseInt(text.trim());
         } catch (NumberFormatException var3) {
            return null;
         }
      } else {
         return null;
      }
   }
}
