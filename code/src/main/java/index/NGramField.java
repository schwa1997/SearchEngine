package index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import parse.ParsedDocument;

import java.io.Reader;

/**
 * Represents a {@link Field} containing the character N-Gram of the English and French versions of the documents
 * together. Note that the parameter N of the N-gram must be chosen in {@link analyze.NGramAnalyzer} as it is the analyzer
 * processing this field.
 *
 * It is a tokenized field, not stored, keeping only document ids and term frequencies (see {@link
 * IndexOptions#DOCS_AND_FREQS} in order to minimize the space occupation.
 *
 *
 *
 * @version 1.00
 * @since 1.00
 */
public class NGramField extends Field
{
    /**
     * The type of the document body field
     */
    private static final FieldType N_GRAM_TYPE = new FieldType();

    static {
        N_GRAM_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        N_GRAM_TYPE.setTokenized(true);
        N_GRAM_TYPE.setStored(false);
    }

    /**
     * Create a new field for the character N-Gram of the English and French versions of the documents.
     *
     * @param value the contents of the French body of a document.
     */
    public NGramField(final Reader value) {
        super(ParsedDocument.FIELDS.N_GRAM, value, N_GRAM_TYPE);
    }

    /**
     * Create a new field for the character N-Gram of the English and French versions of the documents.
     *
     * @param value the contents of the French body of a document.
     */
    public NGramField(final String value) {
        super(ParsedDocument.FIELDS.N_GRAM, value, N_GRAM_TYPE);
    }
}
