package nlp;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

public class FeatureLemma implements LanguageFeatureExtract {
   public JSONObject extractFeature(JCas jcas) {
      JSONArray tokenArray = new JSONArray();

      for(Token token : JCasUtil.select(jcas, Token.class)) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("begin", token.getBegin());
         jsonObject.put("end", token.getEnd());
         jsonObject.put("text", safe(token.getCoveredText()));
         POS pos = token.getPos();
         jsonObject.put("POS", safe(pos != null ? pos.getPosValue() : null));
         jsonObject.put("PosTag", safe(pos != null ? pos.getType().getShortName() : null));
         jsonObject.put("Lemma", safe(token.getLemma() != null ? token.getLemma().getValue() : null));
         tokenArray.put(jsonObject);
      }

      JSONObject output = new JSONObject();
      output.put("tokens", tokenArray);
      return output;
   }

   private static String safe(String string) {
      return string == null ? "" : string;
   }
}
