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

/**
 * Runs the configured DUUI text and transcription pipelines.
 */
public final class PipelineDuui implements NLPline {
    private static final String CONFIG_FILE = "nlp.properties";
    private static final String SENTENCE_TYPE = Sentence.class.getName();

    private final DUUIComposer textPipeline;
    private final DUUIComposer transcriptionPipeline;

    public PipelineDuui(int workers)
            throws IOException, URISyntaxException, CompressorException, InvalidXMLException, SAXException {
        int scale = Math.max(1, workers);
        Properties config = loadProperties();
        DUUILuaContext luaContext = new DUUILuaContext().withJsonLibrary();

        textPipeline = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(luaContext)
                .withWorkers(scale);
        textPipeline.addDriver(new DUUIUIMADriver(), new DUUIRemoteDriver());
        textPipeline.add(new DUUIRemoteDriver.Component(required(config, "spacy.url"))
                .withScale(scale)
                .build());
        textPipeline.add(new DUUIRemoteDriver.Component(required(config, "sarcasmDetector.url"))
                .withScale(scale)
                .withParameter("selection", SENTENCE_TYPE)
                .build());
        textPipeline.add(new DUUIRemoteDriver.Component(required(config, "gervader.url"))
                .withScale(scale)
                .withParameter("selection", SENTENCE_TYPE)
                .build());
        textPipeline.add(new DUUIRemoteDriver.Component(required(config, "parlbert.url"))
                .withScale(scale)
                .withParameter("selection", SENTENCE_TYPE)
                .build());

        transcriptionPipeline = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(luaContext)
                .withWorkers(scale);
        transcriptionPipeline.addDriver(new DUUIUIMADriver(), new DUUIRemoteDriver());
        transcriptionPipeline.add(new DUUIRemoteDriver.Component(required(config, "whisper.url"))
                .withScale(scale)
                .withSourceView("video")
                .withTargetView("transcript")
                .withIgnoring200Error(true)
                .build()
                .withTimeout(3600));
    }

    @Override
    public synchronized void run(JCas jCas) throws Exception {
        textPipeline.run(jCas);
    }

    public synchronized void runWhisper(JCas jCas) throws Exception {
        transcriptionPipeline.run(jCas);
    }

    private static Properties loadProperties() throws IOException {
        try (InputStream input = PipelineDuui.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Missing classpath resource: " + CONFIG_FILE);
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing NLP configuration: " + key);
        }
        return value.trim();
    }
}
