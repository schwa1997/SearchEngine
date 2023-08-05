package index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import parse.ParsedDocument;

import java.io.Reader;

/**
 * Represents a {@link Field} for containing the body text (English version) of a document.
 *
 * It is a tokenized field, not stored, keeping only document ids and term frequencies (see {@link
 * IndexOptions#DOCS_AND_FREQS} in order to minimize the space occupation.
 *
 * @version 1.00
 * @since 1.00
 */
public class EnglishBodyField extends Field
{
    /**
     * The type of the document body field
     */
    private static final FieldType ENGLISH_BODY_TYPE = new FieldType();

    static {
        ENGLISH_BODY_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        ENGLISH_BODY_TYPE.setTokenized(true);
        ENGLISH_BODY_TYPE.setStored(false);
    }

    /**
     * Create a new field for the English body of a document.
     *
     * @param value the contents of the English body of a document.
     */
    public EnglishBodyField(final Reader value) {
        super(ParsedDocument.FIELDS.ENGLISH_BODY, value, ENGLISH_BODY_TYPE);
    }

    /**
     * Create a new field for the English body of a document.
     *
     * @param value the contents of the English body of a document.
     */
    public EnglishBodyField(final String value) {
        super(ParsedDocument.FIELDS.ENGLISH_BODY, value, ENGLISH_BODY_TYPE);
    }
}
