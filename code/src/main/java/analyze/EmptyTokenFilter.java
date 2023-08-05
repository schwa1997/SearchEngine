package analyze;

import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Lucene custom {@link org.apache.lucene.analysis.TokenFilter} to remove tokens with empty text, this is, with length
 * equals to 0.
 *
 * @version 1.0
 * @since 1.0
 */
public class EmptyTokenFilter extends FilteringTokenFilter {

    private final CharTermAttribute termAtt =
            addAttribute(CharTermAttribute.class);

    /**
     * Creates an instance of this token filter
     *
     * @param input input token stream
     */
    protected EmptyTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    protected boolean accept() throws IOException {
        final int length = termAtt.length();
        // length == 0 -> false; length != 0 -> true
        return length != 0;
    }
}