package org.dnacronym.hygene.parser;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.NewGfaParserFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Represents a GFA file with its contents and metadata.
 */
public class NewGfaFile {
    private String fileName;
    private @MonotonicNonNull Graph graph;
    private NewGfaParser gfaParser;
    private MetadataParser metadataParser;


    /**
     * Constructs a new {@code GfaFile}.
     *
     * @param fileName the name of the GFA file
     */
    public NewGfaFile(final String fileName) {
        this.fileName = fileName;

        gfaParser = NewGfaParserFactory.createInstance();
        metadataParser = MetadataParserFactory.createInstance();
    }


    /**
     * Parses the GFA file into a {@code SequenceGraph}.
     *
     * @return a {@code SequenceGraph} based on the contents of the GFA file
     * @throws ParseException if the file content is not GFA-compliant
     * @throws IOException if the file cannot be read
     */
    public final Graph parse() throws ParseException {
        graph = gfaParser.parse(this);
        return graph;
    }

    /**
     * Parses a {@code Node}'s metadata to a {@code NodeMetadata} object.
     *
     * @param lineNumber line number of the node within the GFA file
     * @return a {@code NodeMetadata} object.
     * @throws ParseException if the node metadata cannot be parsed
     */
    public final NodeMetadata parseNodeMetadata(final int lineNumber) throws ParseException {
        return metadataParser.parseNodeMetadata(readFile(), lineNumber);
    }

    /**
     * Parses an {@code Edge}'s metadata to a {@code EdgeMetaData} object.
     *
     * @param lineNumber Line number of the edge within the GFA file
     * @return a {@code EdgeMetaData} object.
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public final EdgeMetadata parseEdgeMetadata(final int lineNumber) throws ParseException {
        return metadataParser.parseEdgeMetadata(readFile(), lineNumber);
    }

    /**
     * Get the name of the GFA file.
     *
     * @return the name of the GFA file.
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * Get the contents of the GFA file.
     *
     * @return the contents of the GFA file.
     * @throws ParseException if the file is not yet parsed to a graph
     */
    public final Graph getGraph() throws ParseException {
        if (graph == null) {
            throw new ParseException("Cannot get the graph before parsing the file");
        }
        return graph;
    }

    /**
     * Reads a GFA file into memory and gives its contents as a {@code String}.
     *
     * @return contents of the GFA file.
     * @throws ParseException if the given file name cannot be read
     */
    public final String readFile() throws ParseException {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ParseException("File '" + fileName + "' cannot be read. ", e);
        }
    }
}
