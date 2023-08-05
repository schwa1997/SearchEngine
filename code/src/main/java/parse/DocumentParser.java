package parse;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a document parser.
 *
 * @author Nicola Ferro (ferro@dei.unipd.it)
 * @version 1.00
 * @since 1.00
 */
public abstract class DocumentParser
        implements Iterator<ParsedDocument>, Iterable<ParsedDocument> {
    //Iterable - Represents a collection that can be iterated over using a for-each loop
    //Iterator - Represents an interface that can be used to iterate over a collection
    /**
     * Indicates whether there is another {@code ParsedDocument} to return.
     */
    protected boolean next = true;

    /**
     * The reader to be used to parse document(s).
     */
    protected final Reader in;

    /**
     * Creates a new document parser.
     *
     * @param in the reader to the document(s) to be parsed.
     * @throws NullPointerException if {@code in} is {@code null}.
     */
    protected DocumentParser(final Reader in) {

        if (in == null) {
            throw new NullPointerException("Reader cannot be null.");
        }

        this.in = in;
    }


    @Override
    public final Iterator<ParsedDocument> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return next;
    }

    @Override
    public final ParsedDocument next() {

        if (!next) {
            throw new NoSuchElementException("No more documents to parse.");
        }

        try {
            return parse();
        } finally {
            try {
                // we reached the end of the file
                if (!next) {
                    in.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to close the reader.", e);
            }
        }

    }

    /**
     * Creates a new {@code DocumentParser}.
     *
     * It assumes the {@code DocumentParser} has a single-parameter constructor which takes a {@code Reader} as input.
     *
     * @param cls the class of the document parser to be instantiated.
     * @param in  the reader to the document(s) to be parsed.
     * @return a new instance of {@code DocumentParser} for the given class.
     * @throws NullPointerException  if {@code cls} and/or {@code in} are {@code null}.
     * @throws IllegalStateException if something goes wrong in instantiating the class.
     */
    public static DocumentParser create(Class<? extends DocumentParser> cls, Reader in) {

        if (cls == null) {
            throw new NullPointerException("Document parser class cannot be null.");
        }

        if (in == null) {
            throw new NullPointerException("Reader cannot be null.");
        }
        try {
            return cls.getConstructor(Reader.class).newInstance(in);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Unable to instantiate document parser %s.", cls.getName()),
                    e);
        }

    }

    /**
     * Creates a new {@code DocumentParser}.
     *
     * It assumes the {@code DocumentParser} has a single-parameter constructor which takes a {@code Reader} as input.
     *
     * @param cls the fully qualified name of class of the document parser to be instantiated.
     * @param in  the reader to the document(s) to be parsed.
     * @return a new instance of {@code DocumentParser} for the given class.
     * @throws NullPointerException  if {@code cls} and/or {@code in} are {@code null}.
     * @throws IllegalStateException if something goes wrong in instantiating the class.
     */
    public static DocumentParser create(String cls, Reader in) {

        if (cls == null || cls.isBlank()) {
            throw new NullPointerException("Document parser class cannot be null or empty.");
        }

        if (in == null) {
            throw new NullPointerException("Reader cannot be null.");
        }

        try {
            return DocumentParser.create( (Class<? extends DocumentParser>) Class.forName(cls), in);
        } catch(ClassNotFoundException e) {
            throw new IllegalStateException(String.format("Unable to find the class of the document parser %s.", cls),
                    e);
        }
    }

    /**
     * Performs the actual parsing of the document.
     *
     * @return the parsed document.
     */
    protected abstract ParsedDocument parse();


}
