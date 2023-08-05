package topic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for the topics in the LongEval provided data.
 *
 * @version 1.00
 * @since 1.00
 */
public class LongEvalTopicReader {

    /**
     * List of read topics.
     */
    private List<LongEvalTopic> topics;

    /**
     * Creates a new topic reader.
     *
     * @param topicsFile path of the topics file.
     * @throws NullPointerException if {@code topicsFile} is {@code null}.
     * @throws IOException if {@code topicsFile} cannot be read as a String.
     * @throws RuntimeException if there are errors parsing (as XML) the topics file.
     */
    public LongEvalTopicReader (Path topicsFile) throws IOException {

        if (topicsFile == null) {
            throw new NullPointerException("Topics file cannot be null.");
        }

        // The list of topics we will return
        this.topics = new ArrayList<>();

        //Code ideas taken from: https://howtodoinjava.com/java/xml/parse-string-to-xml-dom/
        String input = Files.readString(topicsFile);

        //Adding keys at the top and at the end to parse file as xml
        String xmlDoc = "<in>"+input+"</in>";

        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlDoc)));

            //Gets the list of num elements
            NodeList nodeList = doc.getElementsByTagName("top");

            //System.out.println("Number of queries in trec file: "+nodeList.getLength());

            //Foreach node of xml file it tales and add to lists values
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                //System.out.println(node);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;

                    LongEvalTopic newTopic = new LongEvalTopic();
                    newTopic.setNum(eElement.getElementsByTagName("num").item(0).getTextContent());
                    newTopic.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());

                    this.topics.add(newTopic);
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Error parsing topics file %s%n", e);
        }
    }

    /**
     * Returns all the topics read at the path provided.
     *
     * @return list of topics read as {@link LongEvalTopic} objects.
     */
    public List<LongEvalTopic> read() {
        return topics;
    }

    /**
     * Main method of the class. Just for testing purposes.
     *
     * @param args command line arguments.
     * @throws Exception if something goes wrong while indexing.
     */
    public static void main(String[] args) throws Exception {

        final String FILE_NAME = "C:\\longeval_train\\publish\\English\\Queries\\heldout.trec";
        Path file = Paths.get(FILE_NAME);

        LongEvalTopicReader topicReader = new LongEvalTopicReader(file);

        System.out.printf("Starting topic reading at %s %n", FILE_NAME);
        List<LongEvalTopic> topics = topicReader.read();
        for (LongEvalTopic t : topics) {
            System.out.printf("Topic read: %s %n", t);
        }
        System.out.printf("Number of read topics: %d", topics.size());
    }
}
