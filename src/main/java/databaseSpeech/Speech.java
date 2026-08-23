package databaseSpeech;

import nlp.RedeCas;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.Objects;

/**
 * Immutable speech data required to create a UIMA CAS.
 */
public final class Speech {
    private final String speechId;
    private final String text;
    private final String speaker;
    private final String fraction;

    public Speech(String speechId, String text, String speaker, String fraction) {
        this.speechId = Objects.requireNonNullElse(speechId, "");
        this.text = Objects.requireNonNullElse(text, "");
        this.speaker = Objects.requireNonNullElse(speaker, "");
        this.fraction = Objects.requireNonNullElse(fraction, "");
    }

    public String getSpeechId() {
        return speechId;
    }

    public String getText() {
        return text;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getspeaker() {
        return getSpeaker();
    }

    public String getFraction() {
        return fraction;
    }

    public JCas toCAS() throws ResourceInitializationException, CASException {
        return RedeCas.CasForRede(text, speechId, speaker, fraction);
    }
}
