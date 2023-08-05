package analyze;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import parse.LongEvalParser;
import parse.ParsedDocument;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static analyze.AnalyzerUtil.*;

/**
 * Lucene custom {@link Analyzer} created for applying NER (Named Entity Recognition) to the French documents.
 *
 * @version 1.0
 * @since 1.0
 */
public class NERAnalyzer extends Analyzer
{
    /**
     * Creates a new instance of the analyzer.
     */
    public NERAnalyzer() {
        super();
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {

        // Apply a standard tokenizer
        final Tokenizer source = new StandardTokenizer();

        // Apply NER (locations)
        TokenStream tokens = new OpenNLPNERFilter(source, loadLNerTaggerModel("en-ner-location.bin"));

        // Apply NER (persons)
        tokens = new OpenNLPNERFilter(tokens, loadLNerTaggerModel("en-ner-person.bin"));

        // Apply NER (organizations)
        tokens = new OpenNLPNERFilter(tokens, loadLNerTaggerModel("en-ner-organization.bin"));

        // tokens = new OpenNLPNERFilter(tokens, loadLNerTaggerModel("en-ner-money.bin"));
        // tokens = new OpenNLPNERFilter(tokens, loadLNerTaggerModel("en-ner-date.bin"));
        // tokens = new OpenNLPNERFilter(tokens, loadLNerTaggerModel("en-ner-time.bin"));

        return new TokenStreamComponents(source, tokens);
    }

    @Override
    protected Reader initReader(String fieldName, Reader reader) {
        return super.initReader(fieldName, reader);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }

    /**
     * Main method of the class.
     *
     * @param args command line arguments.
     *
     * @throws IOException if something goes wrong while processing the text.
     */
    public static void main(String[] args) throws IOException {
        // Take one example (parsed) (English) document from the training set (pdExample)
        final String FILE_NAME_JMR = "C:\\longeval_train\\publish\\French\\Documents\\Json\\collector_kodicare_1.txt.json";
        final String FILE_NAME_NS = "";

        Reader reader = new FileReader(
                FILE_NAME_JMR);
        LongEvalParser parser = new LongEvalParser(reader);
        ParsedDocument pdExample = null;
        if (parser.hasNext())
            pdExample = parser.next();

        if (pdExample != null) {
            System.out.println("--------- PARSED DOCUMENT ---------");
            System.out.println(pdExample.getBody());
            System.out.println("--------- ANALYZER RESULT ---------");
            // use the analyzer to process the text and print diagnostic information about each token
            consumeTokenStream(new NERAnalyzer(), pdExample.getBody(), false);
        } else {
            System.out.println("Error, could not retrieve document to apply analyzer");
        }
    }
}
