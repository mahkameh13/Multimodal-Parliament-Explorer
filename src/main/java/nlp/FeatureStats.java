package nlp;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import java.util.HashMap;
import java.util.Map;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.json.JSONObject;

public class FeatureStats implements LanguageFeatureExtract {
   public JSONObject extractFeature(JCas jcas) {
      Map<String, Integer> posToken = new HashMap();

      for(Token token : JCasUtil.select(jcas, Token.class)) {
         if (token.getPos() != null && token.getPos().getPosValue() != null) {
            posToken.merge(token.getPos().getPosValue(), 1, Integer::sum);
         }
      }

      Map<String, Integer> dependencyCount = new HashMap();

      for(Dependency dependency : JCasUtil.select(jcas, Dependency.class)) {
         String dependencyType = dependency.getDependencyType();
         if (dependencyType != null && !dependencyType.isBlank()) {
            dependencyCount.merge(dependencyType, 1, Integer::sum);
         }
      }

      Map<String, Integer> neCount = new HashMap();

      for(NamedEntity namedEntity : JCasUtil.select(jcas, NamedEntity.class)) {
         String type = namedEntity.getValue();
         if (type != null && !type.isBlank()) {
            neCount.merge(type, 1, Integer::sum);
         }
      }

      JSONObject statistic = new JSONObject();
      statistic.put("posCounts", new JSONObject(posToken));
      statistic.put("neCounts", new JSONObject(neCount));
      statistic.put("depCounts", new JSONObject(dependencyCount));
      return statistic;
   }
}
