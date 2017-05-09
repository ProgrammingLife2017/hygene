package org.dnacronym.hygene.parser;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.parser.factories.SequenceAlignmentGraphParserFactory;
import org.dnacronym.hygene.parser.factories.GfaParserFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Represents a GFA file with its contents and metadata.
 */
public class GfaFile {
    private String fileName;
    private @MonotonicNonNull SequenceGraph graph;
    private GfaParser gfaParser;
    private SequenceAlignmentGraphParser sagParser;


    /**
     * Constructs a new {@code GfaFile}.
     *
     * @param fileName the name of the GFA file
     */
    public GfaFile(final String fileName) {
        this.fileName = fileName;

        gfaParser = GfaParserFactory.getInstance();
        sagParser = SequenceAlignmentGraphParserFactory.getInstance();
    }


    /**
     * Parses the GFA file into a {@code SequenceGraph}.
     *
     * @return a {@code SequenceGraph} based on the contents of the GFA file
     * @throws ParseException if the file content is not GFA-compliant
     * @throws IOException if the file cannot be read
     */
    public final SequenceGraph parse() throws ParseException {
        graph = sagParser.parse(gfaParser.parse(readFile(fileName)));
        return graph;
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
    public final SequenceGraph getGraph() throws ParseException {
        if (graph == null) {
            throw new ParseException("Cannot get the graph before parsing the file");
        }
        return graph;
    }

    /**
     * Reads a GFA file into memory and gives its contents as a {@code String}.
     *
     *  @param fileName the name of the GFA file
     * @return contents of the GFA file.
     * @throws ParseException if the given file name cannot be read
     */
    private String readFile(final String fileName) throws ParseException {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ParseException("File '" + fileName + "' cannot be read. ", e);
        }
    }
}
