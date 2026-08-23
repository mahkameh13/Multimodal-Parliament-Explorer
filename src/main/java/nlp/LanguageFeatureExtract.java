package nlp;

import org.apache.uima.jcas.JCas;
import org.json.JSONObject;

public interface LanguageFeatureExtract {
   JSONObject extractFeature(JCas var1);
}
