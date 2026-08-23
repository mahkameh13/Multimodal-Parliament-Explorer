package nlp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.json.JSONArray;
import org.json.JSONObject;

public class FeatureTopics implements LanguageFeatureExtract {
   public JSONObject extractFeature(JCas jcas) {
      JSONArray topicsComplete = new JSONArray();

      for(CategoryCoveredTagged tag : JCasUtil.select(jcas, CategoryCoveredTagged.class)) {
         JSONObject tagObject = new JSONObject();
         tagObject.put("begin", tag.getBegin());
         tagObject.put("end", tag.getEnd());
         tagObject.put("text", safe(tag.getCoveredText()));
         tagObject.put("category", safe(tag.getValue()));
         Double score = doubleScore(tag, "getScore");
         tagObject.put("score", score == null ? (double)0.0F : score);
         topicsComplete.put(tagObject);
      }

      Map<String, List<Double>> topScores = new HashMap();

      for(int j = 0; j < topicsComplete.length(); ++j) {
         JSONObject object = topicsComplete.getJSONObject(j);
         String category = object.optString("category", "");
         double score = object.optDouble("score", (double)0.0F);
         if (!category.equals("")) {
            if (!topScores.containsKey(category)) {
               topScores.put(category, new ArrayList());
            }

            ((List)topScores.get(category)).add(score);
         }
      }

      List<JSONObject> summedCategoryList = new ArrayList();

      for(String category : topScores.keySet()) {
         List<Double> scores = (List)topScores.get(category);
         double sum = (double)0.0F;

         for(double s : scores) {
            sum += s;
         }

         double average = (double)0.0F;
         if (scores.size() > 0) {
            average = sum / (double)scores.size();
         }

         JSONObject categoryObject = new JSONObject();
         categoryObject.put("category", category);
         categoryObject.put("score", average);
         categoryObject.put("count", scores.size());
         summedCategoryList.add(categoryObject);
      }

      summedCategoryList.sort((x, y) -> Double.compare(y.optDouble("score", (double)0.0F), x.optDouble("score", (double)0.0F)));
      JSONArray topicsTop5 = new JSONArray();

      for(int j = 0; j < Math.min(5, summedCategoryList.size()); ++j) {
         topicsTop5.put(summedCategoryList.get(j));
      }

      JSONObject output = new JSONObject();
      output.put("topicsAll", topicsComplete);
      output.put("topicsTop5", topicsTop5);
      return output;
   }

   private static String safe(String string) {
      return string == null ? "" : string;
   }

   private static Double doubleScore(Object object, String doubleMethod) {
      try {
         Method method = object.getClass().getMethod(doubleMethod);
         Object value = method.invoke(object);
         if (value instanceof Number number) {
            return number.doubleValue();
         } else {
            return null;
         }
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }
}
