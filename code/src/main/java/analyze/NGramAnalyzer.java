package analyze;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import parse.LongEvalParser;
import parse.ParsedDocument;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import static analyze.AnalyzerUtil.consumeTokenStream;

/**
 * Lucene custom {@link Analyzer} generating character N-Grams for the English and French versions of the documents.
 *
 * @version 1.0
 * @since 1.0
 */
public class NGramAnalyzer extends Analyzer
{
    /**
     * N parameter of the character N-Grams.
     */
    private final Integer N;

    /**
     * Creates a new instance of the analyzer.
     *
     * @param N N parameter of character N-Grams.
     */
    public NGramAnalyzer(Integer N) {
        super();
        this.N = N;
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {

        // Whitespace tokenizer
        final Tokenizer source = new WhitespaceTokenizer();

        // Lowercase
        TokenStream tokens = new LowerCaseFilter(source);

        // Delete everything but letters (also maintain French accent characters)
        tokens = new PatternReplaceFilter(tokens, Pattern.compile("[^a-zéàèùçâêîôûëïü]+"), "", true);

        // Delete empty tokens
        tokens = new EmptyTokenFilter(tokens);

        // Create N-Gram
        tokens = new NGramTokenFilter(tokens, N);

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
        final String FILE_NAME_NS = "";

        Reader reader = new FileReader(
                FILE_NAME_NS);
        LongEvalParser parser = new LongEvalParser(reader);
        ParsedDocument pdExample = null;
        if (parser.hasNext())
            pdExample = parser.next();

        if (pdExample != null) {
            System.out.println("--------- PARSED DOCUMENT ---------");
            System.out.println(pdExample.getBody());
            System.out.println("--------- ANALYZER RESULT ---------");
            // use the analyzer to process the text and print diagnostic information about each token
            consumeTokenStream(new NGramAnalyzer(4), pdExample.getBody(), false);
        } else {
            System.out.println("Error, could not retrieve document to apply analyzer");
        }
    }
}