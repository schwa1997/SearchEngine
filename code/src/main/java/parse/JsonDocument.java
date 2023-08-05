package parse;

/**
 * A java POJO needed to deserialize JSON documents into Java objects.
 *
 * @version 1.00
 * @since 1.00
 */
public class JsonDocument
{
    private String id;
    private String contents;

    /**
     * Get the id of the document
     *
     * @return id of the document
     */
    public String getId() {
        return id;
    }

    /**
     * Get the contents of the document.
     *
     * @return contents of the documents.
     */
    public String getContents() {
        return contents;
    }

    /**
     * Set the id of the document.
     *
     * @param id new id of the document.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the contents of the documents.
     *
     * @param contents new contents of the document
     */
    public void setContents(String contents) {
        this.contents = contents;
    }
}
