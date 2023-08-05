package analyze;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.wordnet.SynonymMap;
import org.apache.lucene.wordnet.SynonymTokenFilter;
import parse.LongEvalParser;
import parse.ParsedDocument;

import java.io.*;
import java.util.regex.Pattern;

import static analyze.AnalyzerUtil.consumeTokenStream;
import static analyze.AnalyzerUtil.loadStopList;

/**
 * Lucene custom {@link Analyzer} created for the English version of the LongEval documents.
 *
 * @version 1.0
 * @since 1.0
 */
public class EnglishAnalyzer extends Analyzer
{
    /**
     * The class loader of this class. Needed for reading files from the {@code resource} directory.
     */
    private static final ClassLoader CL = AnalyzerUtil.class.getClassLoader();

    /**
     * Creates a new instance of the analyzer.
     */
    public EnglishAnalyzer() {
        super();
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {

        // Synonym map to use in SynonymTokenFilter
        SynonymMap synMap;
        try {
            // Load wordnet synonym map
            // Load file wn_s.pl (in resources/) (Prolog version from: https://wordnet.princeton.edu/download/current-version)
            synMap = new SynonymMap(CL.getResourceAsStream("prolog\\wn_s.pl"));
        } catch (IOException  e) {
            throw new IllegalArgumentException(
                    String.format("Unable to find synonym file: %s.", e.getMessage()), e);
        }

        // Whitespace tokenizer
        final Tokenizer source = new WhitespaceTokenizer();

        // Delete some strange symbols found in documents
        TokenStream tokens = new PatternReplaceFilter(source, Pattern.compile(AnalyzerUtil.STRANGE_SYMBOLS_REGEX), "",
                true);

        // Delete punctuation marks at the beginning of words (text)
        tokens = new PatternReplaceFilter(tokens, Pattern.compile("^[\\p{Punct}]+"), "", true);

        // Delete punctuation marks at the end of words (text)
        tokens = new PatternReplaceFilter(tokens, Pattern.compile("[\\p{Punct}]+$"), "", true);

        // Apply WordDelimiterGraphFilter with the following options
        tokens = new WordDelimiterGraphFilter(tokens,
                WordDelimiterGraphFilter.GENERATE_WORD_PARTS // Ex: "PowerShot" => "Power" "Shot"
                        | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS // Ex: "500-42" => "500" "42"
                        | WordDelimiterGraphFilter.CATENATE_NUMBERS // Ex: "500-42" => "50042"
                        | WordDelimiterGraphFilter.PRESERVE_ORIGINAL // Ex: "500-42" => "500" "42" "500-42"
                        | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE // Causes lowercase -> uppercase transition to start a new subword.
                        // | WordDelimiterGraphFilter.SPLIT_ON_NUMERICS // If not set, causes numeric changes to be ignored (subwords will only be generated given SUBWORD_DELIM tokens).
                        | WordDelimiterGraphFilter.STEM_ENGLISH_POSSESSIVE, // "O'Neil's" => "O", "Neil"
                null);

        // Lowercase
        tokens = new LowerCaseFilter(tokens);

        // Apply TERRIER stopword list
        tokens = new StopFilter(tokens, loadStopList("terrier.txt"));

        // Apply query expansion with synonyms
        tokens = new SynonymTokenFilter(tokens, synMap, 10);

        // Apply English Minimal Stem Filter
        tokens = new EnglishMinimalStemFilter(tokens);

        // Remove tokens with empty text
        tokens = new EmptyTokenFilter(tokens);

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
        final String FILE_NAME_JMR = "C:\\longeval_train\\publish\\English\\Documents\\Json\\collector_kodicare_1.txt.json";
        final String FILE_NAME_NS = "C:\\Users\\Lenovo\\Desktop\\seupd2223-jihuming\\code\\collector_kodicare_1.txt.json";

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
            consumeTokenStream(new EnglishAnalyzer(), pdExample.getBody(), false);
        } else {
            System.out.println("Error, could not retrieve document to apply analyzer");
        }
    }
}