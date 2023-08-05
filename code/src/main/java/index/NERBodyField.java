package index;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import parse.ParsedDocument;

import java.io.Reader;

/**
 * Represents a {@link Field} for containing the NER information about the body text of a document.
 *
 * It is a tokenized field, not stored, keeping only document ids and term frequencies (see {@link
 * IndexOptions#DOCS_AND_FREQS} in order to minimize the space occupation.
 *
 * @version 1.00
 * @since 1.00
 */
public class NERBodyField extends Field
{
    /**
     * The type of the document body field
     */
    private static final FieldType NER_TYPE = new FieldType();

    static {
        NER_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        NER_TYPE.setTokenized(true);
        NER_TYPE.setStored(false);
    }

    /**
     * Create a new field for the NER information of a document.
     *
     * @param value the contents of the NER information of a document.
     */
    public NERBodyField(final Reader value) {
        super(ParsedDocument.FIELDS.NER, value, NER_TYPE);
    }

    /**
     * Create a new field for the NER information of a document.
     *
     * @param value the contents of the NER information of a document.
     */
    public NERBodyField(final String value) {
        super(ParsedDocument.FIELDS.NER, value, NER_TYPE);
    }
}
