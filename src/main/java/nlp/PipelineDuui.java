package nlp;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.InvalidXMLException;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIRemoteDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIUIMADriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.DUUILuaContext;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

//Übernommen aus der Übung 4 und angepasst für Übung 5.

/**
 * In dieser Klasse werden die Pipelines für DUUI aufgebaut.
 * Eine Pipeline für Whisperx und eine für die Texte der Reden
 * @author Hadia Syeda
 */
public class PipelineDuui implements NLPline {

    private final String spacyUrl;
    private final String gervaderUrl;
    private final String parlbertUrl;
    private final String whisperUrl;
    //Neu für Aufgabe 2.1
    private final String sarcasmDetectorUrl;
    private static final String sentenceType = Sentence.class.getName();

    private final DUUIComposer duuiComposer;
    private final DUUIComposer whisperComposer;
    private final int workerCompose;
    private final Properties properties;

    /*
    Neu für Aufgabe 2.1
    private final DUUIComposer sarcasmDetectorComposer;*/

    /**
     * Die Composer werden initialisiert.
     */
    public PipelineDuui(int workerCompose) throws IOException, URISyntaxException, CompressorException, InvalidXMLException, SAXException {
        this.workerCompose = Math.max(1, workerCompose);


        this.properties = loadProperties("nlp.properties");
        this.spacyUrl = properties.getProperty("spacy.url");
        this.gervaderUrl = properties.getProperty("gervader.url");
        this.parlbertUrl = properties.getProperty("parlbert.url");
        this.whisperUrl = properties.getProperty("whisper.url");
        this.sarcasmDetectorUrl = properties.getProperty("sarcasmDetector.url");

        System.out.println("URLs:");
        System.out.println("Spacy: " + spacyUrl);
        System.out.println("GerVader: " + gervaderUrl);
        System.out.println("ParlBert: " + parlbertUrl);
        System.out.println("Whisper: " + whisperUrl);
        //Neu für Aufgabe 2.1
        System.out.println("sarcasmDetector: " + sarcasmDetectorUrl);

        DUUILuaContext context = new DUUILuaContext().withJsonLibrary();

        this.duuiComposer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(context)
                .withWorkers(this.workerCompose);

        /*
        this.sarcasmDetectorComposer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(context)
                .withWorkers(this.workerCompose);*/

        this.whisperComposer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(context)
                .withWorkers(this.workerCompose);

        this.duuiComposer.addDriver(new DUUIUIMADriver(), new DUUIRemoteDriver());
        this.whisperComposer.addDriver(new DUUIUIMADriver(), new DUUIRemoteDriver());

        // Spacy Component
        this.duuiComposer.add(new DUUIRemoteDriver.Component(spacyUrl)
                .withScale(workerCompose)
                .build());

        // sarcasm Component
        this.duuiComposer.add(new DUUIRemoteDriver.Component(sarcasmDetectorUrl)
                .withScale(workerCompose)
                .withParameter("selection", sentenceType)
                .build());

        //Neu für Aufgabe 2.1
        // Whisper Component
        this.whisperComposer.add(new DUUIRemoteDriver.Component(whisperUrl)
                .withScale(workerCompose)
                .withSourceView("video")
                .withTargetView("transcript")
                .withIgnoring200Error(true)
                .build().withTimeout(3600));

        // GerVader Component
        this.duuiComposer.add(new DUUIRemoteDriver.Component(gervaderUrl)
                .withScale(workerCompose)
                .withParameter("selection", sentenceType)
                .build());

        // ParlBert Component
        this.duuiComposer.add(new DUUIRemoteDriver.Component(parlbertUrl)
                .withScale(workerCompose)
                .withParameter("selection", sentenceType)
                .build());

    }

    /**
     * Pipeline wird auf einem cas ausgeführt
     */
    public synchronized void run(JCas jcas) throws Exception {
        System.out.println("Starting DUUI pipeline execution...");
        System.out.println("Document text length: " + jcas.getDocumentText().length());

        try {
            duuiComposer.run(jcas);
        } finally {
            duuiComposer.resetPipeline();
        }
    }

    /**
     * Hier wird das gleiche gemacht, aber für Whisper
     */
    public synchronized void runWhisper(JCas jcas) throws Exception {
        System.out.println("Starting Whisper pipeline execution...");

        try {
            whisperComposer.run(jcas);
        } finally {
            whisperComposer.resetPipeline();
        }
    }

    /**
     * Laden properties aus Ressourcen Ordner
     */
    private static Properties loadProperties(String propertiesFile) {
        Properties properties1 = new Properties();
        try(InputStream inputStream= PipelineDuui.class.getClassLoader().getResourceAsStream(propertiesFile)){
            if (inputStream != null){
                properties1.load(inputStream);
                System.out.println("Properties file loaded: " + propertiesFile);
            } else {
                System.err.println("ERROR: Properties file not found: " + propertiesFile);
                System.err.println("Looking in classpath for: " + propertiesFile);

                // Try to list available resources
                ClassLoader cl = PipelineDuui.class.getClassLoader();
                java.net.URL resource = cl.getResource(propertiesFile);
                System.err.println("Resource URL: " + resource);

                throw new RuntimeException("Properties file not found: " + propertiesFile);
            }
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());
            throw new RuntimeException("Failed to load properties", e);
        }
        return properties1;
    }
}

//