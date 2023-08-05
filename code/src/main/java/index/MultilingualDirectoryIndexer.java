package index;

import analyze.FrenchAnalyzer;
import analyze.NERAnalyzer;
import analyze.NGramAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import parse.DocumentParser;
import parse.LongEvalParser;
import parse.ParsedDocument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * Indexes two versions of the same documents (in English and in French) processing a whole directory tree.
 * It will create documents with their ID (field), their ENGLISH BODY (field) and their FRENCH BODY (field).
 *
 * @version 1.00
 * @since 1.00
 */
public class MultilingualDirectoryIndexer {

    // One megabyte
    private static final int MBYTE = 1024 * 1024;

    // The index writer
    private final IndexWriter writer;

    // The class of the {@code DocumentParser} to be used.
    private final Class<? extends DocumentParser> dpCls;

    // The directory where the index is stored.
    private final Path indexDir;

    // The directory (and sub-directories) where English documents are stored.
    private final Path enDocsDir;

    // The directory (and sub-directories) where French documents are stored.
    private final Path frDocsDir;

    // The extension of the files to be indexed.
    private final String extension;

    // The charset used for encoding documents.
    private final Charset cs;

    /**
     * The total number of documents expected to be indexed. For every document there will be two versions (English and
     * French), the count is 1, not 2.
     */
    private final long expectedDocs;

    // The start instant of the indexing.
    private final long start;

    // The total number of indexed files.
    private long filesCount;

    // The total number of indexed documents.
    private long docsCount;

    // The total number of indexed bytes
    private long bytesCount;

    /**
     * Creates a new indexer.
     *
     * @param enAnalyzer      the {@code Analyzer} to be used for the English documents.
     * @param frAnalyzer      the {@code Analyzer} to be used for the French documents.
     * @param ngramAnalyzer   the {@code Analyzer} to be used for N-Gram field of documents.
     * @param nerAnalyzer     the {@code Analyzer} to be used for NER extracted information from documents.
     * @param similarity      the {@code Similarity} to be used.
     * @param ramBufferSizeMB the size in megabytes of the RAM buffer for indexing documents.
     * @param indexPath       the directory where to store the index.
     * @param enDocsPath      the directory from which English documents have to be read.
     * @param frDocsPath      the directory from which French documents have to be read.
     * @param extension       the extension of the files to be indexed.
     * @param charsetName     the name of the charset used for encoding documents.
     * @param expectedDocs    the total number of documents expected to be indexed
     * @param dpCls           the class of the {@code DocumentParser} to be used.
     * @throws NullPointerException     if any of the parameters is {@code null}.
     * @throws IllegalArgumentException if any of the parameters assumes invalid values.
     */
    public MultilingualDirectoryIndexer(final Analyzer enAnalyzer, final Analyzer frAnalyzer,
                                        final Analyzer ngramAnalyzer, final Analyzer nerAnalyzer, final Similarity similarity,
                                        final int ramBufferSizeMB, final String indexPath, final String enDocsPath,
                                        final String frDocsPath, final String extension, final String charsetName,
                                        final long expectedDocs, final Class<? extends DocumentParser> dpCls) {
        // dpCls
        if (dpCls == null) {
            throw new NullPointerException("Document parser class cannot be null.");
        }
        this.dpCls = dpCls;

        // enAnalyzer
        if (enAnalyzer == null) {
            throw new NullPointerException("English analyzer cannot be null.");
        }

        // frAnalyzer
        if (frAnalyzer == null) {
            throw new NullPointerException("French analyzer cannot be null.");
        }

        // ngramAnalyzer
        if (ngramAnalyzer == null) {
            throw new NullPointerException("N-Gram analyzer cannot be null.");
        }

        // nerAnalyzer
        if (nerAnalyzer == null) {
            throw new NullPointerException("NER analyzer cannot be null.");
        }

        // similarity
        if (similarity == null) {
            throw new NullPointerException("Similarity cannot be null.");
        }

        // ramBufferSizeMB
        if (ramBufferSizeMB <= 0) {
            throw new IllegalArgumentException("RAM buffer size cannot be less than or equal to zero.");
        }

        // To apply different analyzers to different fields of documents
        // Taken from: https://www.baeldung.com/lucene-analyzers
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put(ParsedDocument.FIELDS.ENGLISH_BODY, enAnalyzer);
        analyzerMap.put(ParsedDocument.FIELDS.FRENCH_BODY, frAnalyzer);
        analyzerMap.put(ParsedDocument.FIELDS.N_GRAM, ngramAnalyzer);
        analyzerMap.put(ParsedDocument.FIELDS.NER, nerAnalyzer);
        PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);

        final IndexWriterConfig indexConfig = new IndexWriterConfig(wrapper);
        indexConfig.setSimilarity(similarity);
        indexConfig.setRAMBufferSizeMB(ramBufferSizeMB);
        indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexConfig.setCommitOnClose(true);
        indexConfig.setUseCompoundFile(true);

        // indexPath
        if (indexPath == null) {
            throw new NullPointerException("Index path cannot be null.");
        }
        if (indexPath.isEmpty()) {
            throw new IllegalArgumentException("Index path cannot be empty.");
        }

        final Path indexDir = Paths.get(indexPath);
        // if the directory does not already exist, create it
        if (Files.notExists(indexDir)) {
            try {
                Files.createDirectory(indexDir);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Unable to create directory %s: %s. (Try to create the directory manually)",
                                indexDir.toAbsolutePath(), e.getMessage()), e);
            }
        }
        if (!Files.isWritable(indexDir)) {
            throw new IllegalArgumentException(
                    String.format("Index directory %s cannot be written.", indexDir.toAbsolutePath()));
        }
        if (!Files.isDirectory(indexDir)) {
            throw new IllegalArgumentException(String.format("%s expected to be a directory where to write the index.",
                    indexDir.toAbsolutePath()));
        }
        this.indexDir = indexDir;

        // enDocsPath
        if (enDocsPath == null) {
            throw new NullPointerException("English documents path cannot be null.");
        }
        if (enDocsPath.isEmpty()) {
            throw new IllegalArgumentException("English documents path cannot be empty.");
        }
        final Path enDocsDir = Paths.get(enDocsPath);
        if (!Files.isReadable(enDocsDir)) {
            throw new IllegalArgumentException(
                    String.format("English documents directory %s cannot be read.", enDocsDir.toAbsolutePath()));
        }
        if (!Files.isDirectory(enDocsDir)) {
            throw new IllegalArgumentException(
                    String.format("%s expected to be a directory of documents.", enDocsDir.toAbsolutePath()));
        }
        this.enDocsDir = enDocsDir;

        // frDocsPath
        if (frDocsPath == null) {
            throw new NullPointerException("French documents path cannot be null.");
        }
        if (frDocsPath.isEmpty()) {
            throw new IllegalArgumentException("French documents path cannot be empty.");
        }
        final Path frDocsDir = Paths.get(frDocsPath);
        if (!Files.isReadable(frDocsDir)) {
            throw new IllegalArgumentException(
                    String.format("French documents directory %s cannot be read.", frDocsDir.toAbsolutePath()));
        }
        if (!Files.isDirectory(frDocsDir)) {
            throw new IllegalArgumentException(
                    String.format("%s expected to be a directory of documents.", frDocsDir.toAbsolutePath()));
        }
        this.frDocsDir = frDocsDir;

        // extension
        if (extension == null) {
            throw new NullPointerException("File extension cannot be null.");
        }
        if (extension.isEmpty()) {
            throw new IllegalArgumentException("File extension cannot be empty.");
        }
        this.extension = extension;

        // charsetName
        if (charsetName == null) {
            throw new NullPointerException("Charset name cannot be null.");
        }
        if (charsetName.isEmpty()) {
            throw new IllegalArgumentException("Charset name cannot be empty.");
        }

        try {
            cs = Charset.forName(charsetName);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Unable to create the charset %s: %s.", charsetName, e.getMessage()), e);
        }

        // expectedDocs
        if (expectedDocs <= 0) {
            throw new IllegalArgumentException(
                    "The expected number of documents to be indexed cannot be less than or equal to zero.");
        }
        this.expectedDocs = expectedDocs;

        // Set all the counts to 0
        this.docsCount = 0;
        this.bytesCount = 0;
        this.filesCount = 0;

        // Create the IndexWritter object
        try {
            writer = new IndexWriter(FSDirectory.open(indexDir), indexConfig);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Unable to create the index writer in directory %s: %s.",
                    indexDir.toAbsolutePath(), e.getMessage()), e);
        }

        this.start = System.currentTimeMillis();
    }

    /**
     * Indexes the documents.
     *
     * @throws IOException if something goes wrong while indexing.
     */
    public void index() throws IOException {

        System.out.printf("%n#### Start indexing ####%n");

        // Create an iterator to access every file in the English directory (enFileIterator)
        File[] enFiles = enDocsDir.toFile().listFiles();
        Iterator<File> enFileIterator;
        if (enFiles != null) {
            enFiles = deleteWithoutExtension(enFiles, extension);
            enFileIterator = Arrays.stream(enFiles).iterator();
        } else {
            throw new RuntimeException("List of files in English documents directory is null");
        }

        // Create an iterator to access every file in the French directory (frFileIterator)
        File[] frFiles = frDocsDir.toFile().listFiles();
        Iterator<File> frFileIterator;
        if (frFiles != null) {
            frFiles = deleteWithoutExtension(frFiles, extension);
            frFileIterator = Arrays.stream(frFiles).iterator();
        } else {
            throw new RuntimeException("List of files in French documents directory is null");
        }

        long elapsedTime;

        while (enFileIterator.hasNext() && frFileIterator.hasNext()) {
            Path enFile = enFileIterator.next().toPath();
            Path frFile = frFileIterator.next().toPath();

            bytesCount += Files.size(enFile) + Files.size(frFile);
            filesCount += 2;

            // Create a document parser for English documents
            DocumentParser enDp = DocumentParser.create(dpCls, Files.newBufferedReader(enFile, cs));

            // Create a document parser for French documents
            DocumentParser frDp = DocumentParser.create(dpCls, Files.newBufferedReader(frFile, cs));

            // Create an iterator for the English documents
            Iterator<ParsedDocument> enParDocIterator = enDp.iterator();

            // Create an iterator for the French documents
            Iterator<ParsedDocument> frParDocIterator = frDp.iterator();

            while (enParDocIterator.hasNext() && frParDocIterator.hasNext()) {
                ParsedDocument enParDoc = enParDocIterator.next();
                ParsedDocument frParDoc = frParDocIterator.next();

                if (!enParDoc.getIdentifier().equals(frParDoc.getIdentifier())) {
                    throw new RuntimeException("English and French versions of a document don't have the same ID");
                }

                Document doc = new Document();

                // add the document identifier
                doc.add(new StringField(ParsedDocument.FIELDS.ID, enParDoc.getIdentifier(), Field.Store.YES));

                // add the English document body
                doc.add(new EnglishBodyField(enParDoc.getBody()));

                // add the French document body
                doc.add(new FrenchBodyField(frParDoc.getBody()));

                // add the English body concatenated to the French body to generate the N-Gram
                // note that the N-Gram will be generated by the class NGramAnalyzer using this field content
                doc.add(new NGramField(enParDoc.getBody() + " " +frParDoc.getBody()));

                // add the French body to extract NER information
                // note that the NER information will be generated by the class NERAnalyzer using this field content
                // we take the French version of the documents, because it is the original, the English one is a translation
                doc.add(new NERBodyField(frParDoc.getBody()));

                writer.addDocument(doc);

                docsCount++;

                // print progress every 1000 indexed documents
                if (docsCount % 1000 == 0) {
                    elapsedTime = (System.currentTimeMillis() - start) / 1000;

                    System.out.printf("%d document(s) in both languages (%d files, %d Mbytes) indexed in %d seconds.%n",
                            docsCount, filesCount, bytesCount / MBYTE,
                            elapsedTime);

                    System.out.printf("\tEstimated remaining time (%d/%d processed): %d second(s).%n",
                            docsCount, expectedDocs,
                            (long)((double)expectedDocs * elapsedTime / (double)docsCount) - elapsedTime);
                }
            }
        }

        writer.commit();
        writer.close();

        if (docsCount != expectedDocs) {
            System.out.printf("Expected to index %d documents (in both languages); %d indexed instead.%n", expectedDocs, docsCount);
        }

        System.out.printf("%d document(s) in both languages (%d files, %d Mbytes) indexed in %d seconds.%n", docsCount, filesCount,
                bytesCount / MBYTE, (System.currentTimeMillis() - start) / 1000);

        System.out.printf("#### Indexing complete ####%n");
    }

    /**
     * Given an array of files, delete those files not having the specified extension.
     *
     * @param files array of files from which files without the extension will be deleted.
     * @param extension extension of files of the resulting array.
     * @return array of files with the specified extension.
     */
    private File[] deleteWithoutExtension(File[] files, String extension) {
        List<File> result = new ArrayList<>();
        for (File f : files) {
            if (f.toPath().getFileName().toString().endsWith(extension))
                result.add(f);
        }
        return result.toArray(new File[0]);
    }

    /**
     * Prints statistics about the vocabulary to the console. Statistics from English and French versions of the
     * documents will be printed independently.
     *
     * @param maxVocabularyPrint maximum number of words to be printed in the vocabulary part of the statistics.
     * @throws IOException if something goes wrong while accessing the index.
     */
    public void printVocabularyStatistics(int maxVocabularyPrint) throws IOException {

        System.out.printf("%n------------- PRINTING VOCABULARY STATISTICS -------------%n");

        // Open the directory in Lucene
        final Directory dir = FSDirectory.open(indexDir);

        // Open the index - we need to get a LeafReader to be able to directly access terms
        final LeafReader index = DirectoryReader.open(dir).leaves().get(0).reader();

        // Total number of documents in the collection
        System.out.printf("+ Total number of documents (both languages): %d%n", index.numDocs());

        List<Terms> vocs = new ArrayList<>();
        vocs.add(index.terms(ParsedDocument.FIELDS.ENGLISH_BODY));
        vocs.add(index.terms(ParsedDocument.FIELDS.FRENCH_BODY));

        for (int i = 0; i < vocs.size(); i++) {
            if (i == 0)
                System.out.printf("%n------------- ENGLISH -------------%n");
            else
                System.out.printf("%n------------- FRENCH -------------%n");

            // Get the vocabulary of the index.
            final Terms voc = vocs.get(i);

            // Total number of unique terms in the collection
            System.out.printf("+ Total number of unique terms: %d%n", voc.size());

            // Total number of terms in the collection
            System.out.printf("+ Total number of terms: %d%n", voc.getSumTotalTermFreq());

            // Get an iterator over the vector of terms in the vocabulary
            final TermsEnum termsEnum = voc.iterator();

            // Iterate until there are terms
            System.out.printf("+ Vocabulary (printing only %d):%n", maxVocabularyPrint);
            System.out.printf("  - %-20s%-5s%-5s%n", "TERM", "DF", "FREQ");
            int count = 0;
            for (BytesRef term = termsEnum.next(); term != null; term = termsEnum.next()) {
                count++;
                // Get the text string of the term
                String termstr = term.utf8ToString();

                // Get the document frequency (DF) of the term
                int df = termsEnum.docFreq();

                // Get the total frequency of the term
                long freq = termsEnum.totalTermFreq();
                System.out.printf("  - %-30s%-5d%-5d%n", termstr, df, freq);

                if (count == maxVocabularyPrint)
                    break;
            }
        }

        // close the index and the directory
        index.close();
        dir.close();

        System.out.printf("----------------------------------------------------------%n");
    }

    /**
     * Main method of the class. Just for testing purposes.
     *
     * @param args command line arguments.
     * @throws Exception if something goes wrong while indexing.
     */
    public static void main(String[] args) throws Exception {
        final int ramBuffer = 256;
        // FILL: English documents path
        final String enDocsPath = "D:\\longeval_test\\test-collection\\B-Long-September\\English\\Documents\\Json";
        // FILL: French documents path
        final String frDocsPath = "D:\\longeval_test\\test-collection\\B-Long-September\\French\\Documents\\Json";
        String indexPath;
        // FILL: Created index path
        indexPath = "D:\\created_indexes\\test_long\\2023_05_13_multilingual_4gram_synonym_ner";

        // FILL: extension of the files containing documents
        final String extension = "json";
        // FILE: number of expected documents
        final int expectedDocs = 1081334;
        final String charsetName = "ISO-8859-1";

        final EnglishAnalyzer enAn = new EnglishAnalyzer();
        final FrenchAnalyzer frAn = new FrenchAnalyzer();
        NGramAnalyzer ngramAn;
        ngramAn = new NGramAnalyzer(4);
        final NERAnalyzer nerAnalyzer = new NERAnalyzer();

        MultilingualDirectoryIndexer i;

        i = new MultilingualDirectoryIndexer(enAn, frAn, ngramAn, nerAnalyzer, new BM25Similarity(),
                ramBuffer, indexPath, enDocsPath, frDocsPath, extension, charsetName, expectedDocs,
                LongEvalParser.class);
        i.index();
    }
}