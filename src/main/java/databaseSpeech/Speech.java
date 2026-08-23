package databaseSpeech;

//import nlp.RedeCas;
import nlp.RedeCas;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class Speech {

    private String speechId;
    private String text;
    private String speaker;
    private String fraction;

    //Konstruktor
    public Speech(String speechId, String text, String speaker, String fraction){
        this.speechId = speechId;
        this.text = text;
        this.speaker = speaker;
        this.fraction = fraction;
    }

    public String getSpeechId(){
        return this.speechId;
    }

    public String getText(){
        return this.text;
    }

    public String getspeaker(){
        return this.speaker;
    }

    public String getFraction(){
        return this.fraction;
    }


    //Übernommen und modifiziert aus alter Übung

    /**
     * @author: Hadia Syeda
     * ToCas Methode
     * @return
     * @throws ResourceInitializationException
     * @throws CASException
     */
    public JCas toCAS() throws ResourceInitializationException, CASException {
        return RedeCas.CasForRede(this.text
        ,this.speechId, this.speaker, this.fraction);}

}
