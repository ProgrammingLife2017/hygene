package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.graph.layout.Fafosp;
import org.dnacronym.hygene.graph.metadata.EdgeMetadata;
import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.GfaParserFactory;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.persistence.UnexpectedDatabaseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;


/**
 * Represents a GFA file with its contents and metadata.
 */
@SuppressWarnings("squid:S1192") // No need to define a constant for the string literal "File"
public final class GfaFile {
    private static final Logger LOGGER = LogManager.getLogger(GfaFile.class);
    private static final int PROGRESS_TOTAL = 100;
    private static final String RANDOM_ACCESS_FILE_MODE = "r";

    private final String fileName;
    private final GfaParser gfaParser;
    private final MetadataParser metadataParser;
    private @MonotonicNonNull Graph graph;
    private @MonotonicNonNull RandomAccessFile randomAccessFile;


    /**
     * Constructs a new {@link GfaFile}.
     *
     * @param fileName the name of the GFA file
     */
    public GfaFile(final String fileName) {
        this.fileName = fileName;

        gfaParser = GfaParserFactory.createInstance();
        metadataParser = MetadataParserFactory.createInstance();
    }


    /**
     * Parses the GFA file into a {@link Graph}.
     *
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return a {@link Graph} based on the contents of the GFA file
     * @throws GfaParseException if the file content is not GFA-compliant
     */
    public Graph parse(final ProgressUpdater progressUpdater) throws GfaParseException {
        try (FileDatabase fileDatabase = new FileDatabase(fileName)) {
            final GraphLoader graphLoader = new GraphLoader(fileName);

            if (graphLoader.hasGraph()) {
                final Map<String, String> genomeMapping = fileDatabase.getFileGenomeMapping().getMappings();
                graph = new Graph(graphLoader.restoreGraph(progressUpdater), genomeMapping, this);
            } else {
                LOGGER.info("Start parsing");
                graph = gfaParser.parse(this, progressUpdater);
                LOGGER.info("Finished parsing");

                LOGGER.info("Start fafosp x");
                new Fafosp(graph).horizontal();

                LOGGER.info("GfaFile parse finished");

                progressUpdater.updateProgress(PROGRESS_TOTAL - 1, "Caching data for faster load next time...");

                LOGGER.info("Start dumping the graph to the database");
                graphLoader.dumpGraph(graph.getNodeArrays());
                fileDatabase.getFileGenomeMapping().addMapping(graph.getGenomeMapping());
                LOGGER.info("Finished dumping the graph to the database");
            }
        } catch (final UnexpectedDatabaseException | IOException | SQLException e) {
            LOGGER.error("Could not open file database to restore graph.", e);
            throw new GfaParseException("Could not open file database to restore graph.", e);
        }

        progressUpdater.updateProgress(PROGRESS_TOTAL, "Loading file completed");

        return graph;
    }

    /**
     * Parses a node's metadata to a {@link NodeMetadata} object.
     *
     * @param byteOffset the byte offset of the node within the GFA file
     * @return a map in the {@code provided key => node metadata} format
     * @throws MetadataParseException if the node metadata cannot be parsed
     */
    public NodeMetadata parseNodeMetadata(final long byteOffset) throws MetadataParseException {
        return metadataParser.parseNodeMetadata(this, byteOffset);
    }

    /**
     * Parses a node's metadata to a {@link NodeMetadata} object.
     *
     * @param byteOffsets the byte offsets where the nodes should be located, sorted from lowest to highest,
     *                    results will be given the same key as provided in this map
     * @return a {@link NodeMetadata} object
     * @throws MetadataParseException if the node metadata cannot be parsed
     */
    public Map<Integer, NodeMetadata> parseNodeMetadata(final Map<Integer, Long> byteOffsets)
            throws MetadataParseException {
        return metadataParser.parseNodeMetadata(this, byteOffsets);
    }

    /**
     * Parses an edge's metadata to a {@link EdgeMetadata} object.
     *
     * @param byteOffset byte offset of the edge within the GFA file
     * @return a {@link EdgeMetadata} object
     * @throws MetadataParseException if the edge metadata cannot be parsed
     */
    public EdgeMetadata parseEdgeMetadata(final long byteOffset) throws MetadataParseException {
        return metadataParser.parseEdgeMetadata(this, byteOffset);
    }

    /**
     * Get the name of the GFA file.
     *
     * @return the name of the GFA file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the contents of the GFA file.
     *
     * @return the contents of the GFA file
     * @throws IllegalStateException if the file is not yet parsed to a graph
     */
    public Graph getGraph() {
        if (graph == null) {
            throw new IllegalStateException("Cannot get the graph before parsing the file");
        }
        return graph;
    }

    /**
     * Reads a GFA file into memory and gives its contents as a {@link String}.
     *
     * @return contents of the GFA file
     * @throws GfaParseException if the given file name cannot be read
     */
    public BufferedReader readFile() throws GfaParseException {
        try {
            return Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new GfaParseException("File '" + fileName + "' cannot be read. ", e);
        }
    }

    /**
     * Returns an input stream to read the GFA file.
     *
     * @return an input stream to read the GFA file
     * @throws GfaParseException if file could not be found
     */
    public InputStream getInputStream() throws GfaParseException {
        try {
            return new FileInputStream(fileName);
        } catch (final FileNotFoundException e) {
            throw new GfaParseException("File '" + fileName + "' could not be found. ", e);
        }
    }

    /**
     * Returns a random access file for the GFA file.
     *
     * @return a random access file for the GFA file
     * @throws MetadataParseException if file could not be found
     */
    public RandomAccessFile getRandomAccessFile() throws MetadataParseException {
        try {
            if (randomAccessFile == null) {
                randomAccessFile = new RandomAccessFile(fileName, RANDOM_ACCESS_FILE_MODE);
            }
            return randomAccessFile;
        } catch (final FileNotFoundException e) {
            throw new MetadataParseException("File '" + fileName + "' could not be found. ", e);
        }
    }
}
