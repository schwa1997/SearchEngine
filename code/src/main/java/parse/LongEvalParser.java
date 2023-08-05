package parse;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Implements a stream document parser for the LongEval corpus (JSON version).
 *
 * @version 1.00
 * @since 1.00
 */
public class LongEvalParser extends DocumentParser {

    /**
     * The currently parsed document.
     */
    private ParsedDocument document = null;

    /**
     * The JSON reader to be used to parse document(s).
     * We will use this object (JsonReader) instead of Reader in.
     */
    private final JsonReader in_json;

    /**
     * Creates a new Longeval Corpus (JSON) stream document parser.
     *
     * @param in the reader of the document(s) to be parsed.
     * @throws NullPointerException     if {@code in} is {@code null}.
     * @throws IllegalArgumentException if any error initializing the JSON parser.
     */
    public LongEvalParser(final Reader in) {
        super(new BufferedReader(in));

        in_json = new JsonReader(this.in);
        try {
            in_json.beginArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse the JSON file: Error beginning array", e);
        }
    }

    @Override
    public boolean hasNext() {
        // JSON stream reading taken from: https://www.amitph.com/java-parse-large-json-files/
        try {
            if (in_json.hasNext()) {
                JsonDocument jsdoc = new Gson().fromJson(in_json, JsonDocument.class);
                document = new ParsedDocument(jsdoc.getId(), jsdoc.getContents());
                next = true;
            } else {
                in_json.endArray();
                in_json.close();
                next = false;
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to parse the document.", e);
        }

        return next;
    }

    @Override
    protected final ParsedDocument parse() {
        return document;
    }


    /**
     * Main method of the class. Just for testing purposes.
     *
     * @param args command line arguments.
     * @throws Exception if something goes wrong while indexing.
     */
    public static void main(String[] args) throws Exception {
        final String FILE_NAME_JMR = "C:\\longeval_train\\publish\\English\\Documents\\Json\\collector_kodicare_1.txt.json";
        Reader reader = new FileReader(
                FILE_NAME_JMR);

        LongEvalParser p = new LongEvalParser(reader);

        System.out.printf("Starting document parsing at %s...%n", FILE_NAME_JMR);
        int count = 0;
        for (ParsedDocument d : p) {
            System.out.printf("%n%n------------------------------------%n%s%n%n%n", d.toString());
            System.out.printf("Parsed document with id=%s %n", d.getIdentifier());
            count++;
        }
        System.out.printf("Number of parsed documents: %d", count);
    }
}
