package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.NewGfaParserFactory;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.persistence.GraphLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;


/**
 * Represents a GFA file with its contents and metadata.
 */
public final class GfaFile {
    private static final Logger LOGGER = LogManager.getLogger(GfaFile.class);
    private static final int PROGRESS_TOTAL = 100;

    private final String fileName;
    private final NewGfaParser gfaParser;
    private final MetadataParser metadataParser;
    private @MonotonicNonNull Graph graph;


    /**
     * Constructs a new {@link GfaFile}.
     *
     * @param fileName the name of the GFA file
     */
    public GfaFile(final String fileName) {
        this.fileName = fileName;

        gfaParser = NewGfaParserFactory.createInstance();
        metadataParser = MetadataParserFactory.createInstance();
    }


    /**
     * Parses the GFA file into a {@link Graph}.
     *
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return a {@link Graph} based on the contents of the GFA file
     * @throws ParseException if the file content is not GFA-compliant
     */
    public Graph parse(final ProgressUpdater progressUpdater) throws ParseException {
        try (FileDatabase fileDatabase = new FileDatabase(fileName)) {
            final GraphLoader graphLoader = new GraphLoader(fileDatabase);

            if (graphLoader.hasGraph()) {
                graph = new Graph(graphLoader.restoreGraph(progressUpdater), this);
            } else {
                LOGGER.info("Start parsing");
                graph = gfaParser.parse(this, progressUpdater);
                LOGGER.info("Finished parsing");

                LOGGER.info("Start fafosp x");
                graph.fafosp().horizontal();

                LOGGER.info("Start fafosp y");
                graph.fafosp().vertical();

                LOGGER.info("GfaFile parse finished");

                progressUpdater.updateProgress(PROGRESS_TOTAL - 1, "Caching data for faster load next time...");

                LOGGER.info("Start dumping the graph to the database");
                graphLoader.dumpGraph(graph.getNodeArrays());
                LOGGER.info("Finished dumping the graph to the database");
            }
        } catch (final IOException | SQLException e) {
            LOGGER.error("Could not open file database to restore graph.", e);
            throw new ParseException("Could not open file database to restore graph.", e);
        }

        progressUpdater.updateProgress(PROGRESS_TOTAL, "Loading file completed");

        return graph;
    }

    /**
     * Parses a node's metadata to a {@link NodeMetadata} object.
     *
     * @param lineNumber  the line numbers where the nodes should be located, sorted from lowest to highest,
     *                    results will be given the same key as provided in this map
     * @return a map in the {@code provided key => node metadata} format
     * @throws ParseException if the node metadata cannot be parsed
     */
    public NodeMetadata parseNodeMetadata(final int lineNumber) throws ParseException {
        return metadataParser.parseNodeMetadata(this, lineNumber);
    }

    /**
     * Parses a node's metadata to a {@link NodeMetadata} object.
     *
     * @param lineNumbers line number of the node within the GFA file
     * @return a {@link NodeMetadata} object
     * @throws ParseException if the node metadata cannot be parsed
     */
    public Map<Integer, NodeMetadata> parseNodeMetadata(final Map<Integer, Integer> lineNumbers) throws ParseException {
        return metadataParser.parseNodeMetadata(this, lineNumbers);
    }

    /**
     * Parses an edge's metadata to a {@link EdgeMetadata} object.
     *
     * @param lineNumber Line number of the edge within the GFA file
     * @return a {@link EdgeMetadata} object
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public EdgeMetadata parseEdgeMetadata(final int lineNumber) throws ParseException {
        return metadataParser.parseEdgeMetadata(this, lineNumber);
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
     * @throws ParseException if the given file name cannot be read
     */
    public BufferedReader readFile() throws ParseException {
        try {
            return Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new ParseException("File '" + fileName + "' cannot be read. ", e);
        }
    }
}
