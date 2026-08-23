package nlp;

import nlp.types.PlenarSpeech;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Diese Klasse stellt sicher, dass der Text korrekt umgeschrieben wird.
 * Hier werden JCas Objekte hergestellt.
 */

public class RedeCas {
    /**
     * JCas Objekt wird für Rede erstellt. Wird von to-Cas aufgegriffen.
     *
     * @param text
     * @param speechId
     * @param speakerName
     * @param fraction
     * @return JCas Objekt für NLP
     * @throws ResourceInitializationException
     * @throws CASException
     */

    public static JCas CasForRede(String text, String speechId, String speakerName, String fraction) throws ResourceInitializationException, CASException {
        JCas jcas = JCasFactory.createJCas();
        jcas.setDocumentLanguage("de");
        //Der Text wird in die Cas geschrieben
        jcas.setDocumentText(text);


        //Neu für diese Abgabe:
        //Annotation TypSystem
        PlenarSpeech plenarSpeech = new PlenarSpeech(jcas);
        plenarSpeech.setBegin(0);
        plenarSpeech.setEnd(text.length());

        //Eigenschaften:
        plenarSpeech.setSpeechId(speechId == null? "": speechId);
        plenarSpeech.setSpeakerName(speakerName == null? "": speakerName);
        plenarSpeech.setFraction(fraction == null? "": fraction);
        plenarSpeech.addToIndexes();

        return jcas;
    }
}

//
