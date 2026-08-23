package nlp;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.type.GerVaderSentiment;
import org.json.JSONArray;
import org.json.JSONObject;

public class FeatureSentences implements LanguageFeatureExtract {
   public JSONObject extractFeature(JCas jcas) {
      JCas sentenceView;
      try {
         sentenceView = jcas.getView("_InitialView");
      } catch (CASException e) {
         throw new RuntimeException(e);
      }

      JSONArray sentencesArray = new JSONArray();

      for(Sentence sentence : JCasUtil.select(sentenceView, Sentence.class)) {
         JSONObject sentenceObject = new JSONObject();
         sentenceObject.put("begin", sentence.getBegin());
         sentenceObject.put("end", sentence.getEnd());
         sentenceObject.put("text", safe(sentence.getCoveredText()));
         double sum = (double)0.0F;
         int k = 0;

         for(GerVaderSentiment sentiment : JCasUtil.selectOverlapping(sentenceView, GerVaderSentiment.class, sentence)) {
            try {
               sum += sentiment.getSentiment();
               ++k;
            } catch (Throwable var18) {
            }
         }

         sentenceObject.put("sentiment", k == 0 ? (double)0.0F : sum / (double)k);
         double sumForSarcasm = (double)0.0F;

         for(Annotation annotation : JCasUtil.selectOverlapping(sentenceView, Annotation.class, sentence)) {
            String type = annotation.getType().getName();
            if (type.toLowerCase().contains("sarcasm")) {
               System.out.println("Sarcasm-Typ: " + annotation.getType().getName());
               if (annotation.getType().getFeatureByBaseName("Sarcasm") != null) {
                  String sarcasmValue = annotation.getFeatureValueAsString(annotation.getType().getFeatureByBaseName("Sarcasm"));
                  if (sarcasmValue != null) {
                     try {
                        double value = Double.parseDouble(sarcasmValue);
                        if (value > sumForSarcasm) {
                           sumForSarcasm = value;
                        }
                     } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                     }
                  }
               }
            }
         }

         sentenceObject.put("sarcasm", sumForSarcasm);
         JSONArray entities = new JSONArray();

         for(NamedEntity namedEntity : JCasUtil.selectOverlapping(sentenceView, NamedEntity.class, sentence)) {
            JSONObject entityObject = new JSONObject();
            entityObject.put("begin", namedEntity.getBegin());
            entityObject.put("end", namedEntity.getEnd());
            entityObject.put("text", safe(namedEntity.getCoveredText()));
            entityObject.put("type", safe(namedEntity.getValue()));
            entities.put(entityObject);
         }

         sentenceObject.put("entities", entities);
         sentencesArray.put(sentenceObject);
      }

      JSONObject outObject = new JSONObject();
      outObject.put("sentences", sentencesArray);
      return outObject;
   }

   private static String safe(String string) {
      return string == null ? "" : string;
   }
}
