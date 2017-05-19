package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.Edge;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
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


/**
 * Represents a GFA file with its contents and metadata.
 */
public class GfaFile {
    private static final Logger LOGGER = LogManager.getLogger(GfaFile.class);

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
     * @return a {@link Graph} based on the contents of the GFA file
     * @throws ParseException if the file content is not GFA-compliant
     */
    public final Graph parse() throws ParseException {
        try (final FileDatabase fileDatabase = new FileDatabase(fileName)) {
            final GraphLoader graphLoader = new GraphLoader(fileDatabase);

            if (graphLoader.hasGraph()) {
                graph = new Graph(graphLoader.restoreGraph(), this);
            } else {
                graph = gfaParser.parse(this);
                graph.fafosp().horizontal();
                graph.fafosp().vertical();

                graphLoader.dumpGraph(graph.getNodeArrays());
            }
        } catch (final IOException | SQLException e) {
            LOGGER.error("Could not open file database to restore graph.", e);
            throw new ParseException("Could not open file database to restore graph.", e);
        }

        return graph;
    }

    /**
     * Parses a {@link Node}'s metadata to a {@link NodeMetadata} object.
     *
     * @param lineNumber line number of the node within the GFA file
     * @return a {@link NodeMetadata} object
     * @throws ParseException if the node metadata cannot be parsed
     */
    public final NodeMetadata parseNodeMetadata(final int lineNumber) throws ParseException {
        return metadataParser.parseNodeMetadata(this, lineNumber);
    }

    /**
     * Parses an {@link Edge}'s metadata to a {@link EdgeMetadata} object.
     *
     * @param lineNumber Line number of the edge within the GFA file
     * @return a {@link EdgeMetadata} object
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public final EdgeMetadata parseEdgeMetadata(final int lineNumber) throws ParseException {
        return metadataParser.parseEdgeMetadata(this, lineNumber);
    }

    /**
     * Get the name of the GFA file.
     *
     * @return the name of the GFA file
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * Get the contents of the GFA file.
     *
     * @return the contents of the GFA file
     * @throws ParseException if the file is not yet parsed to a graph
     */
    public final Graph getGraph() throws ParseException {
        if (graph == null) {
            throw new ParseException("Cannot get the graph before parsing the file");
        }
        return graph;
    }

    /**
     * Reads a GFA file into memory and gives its contents as a {@link String}.
     *
     * @return contents of the GFA file
     * @throws ParseException if the given file name cannot be read
     */
    public final BufferedReader readFile() throws ParseException {
        try {
            return Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new ParseException("File '" + fileName + "' cannot be read. ", e);
        }
    }
}
