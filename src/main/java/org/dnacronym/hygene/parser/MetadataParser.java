package org.dnacronym.hygene.parser;

import com.google.common.collect.ImmutableMap;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;


/**
 * This class handles parsing of metadata of nodes and edges contained in GFA Files.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class MetadataParser {
    public static final String GENOME_LIST_HEADER_PREFIX = "ORI:Z:";

    private int readerPosition;


    /**
     * Parses the metadata of a segment (node) to a {@link NodeMetadata} object.
     *
     * @param gfa        a reference to the current {@link GfaFile}
     * @param byteOffset the byte offset where the node should be located
     * @return a {@link NodeMetadata} object containing a segment's metadata
     * @throws ParseException if the GFA file or given line is invalid
     */
    public NodeMetadata parseNodeMetadata(final GfaFile gfa, final long byteOffset) throws ParseException {
        final NodeMetadata metadata = parseNodeMetadata(gfa, ImmutableMap.of(0, byteOffset)).get(0);

        return Optional.ofNullable(metadata).orElseThrow(
                () -> new ParseException("The node at position " + byteOffset + " could not be found")
        );
    }

    /**
     * Parses the metadata of multiple nodes with limited file IO by reading the file only once.
     *
     * @param gfa         a reference to the current {@link GfaFile}
     * @param byteOffsets the byte offsets where the nodes should be located, sorted from lowest to highest, results
     *                    will be given the same key as provided in this map
     * @return a map in the {@code provided key => node metadata} format
     * @throws ParseException if the GFA file or given line is invalid
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // The whole purpose of the loop is to instantiate objects
    public Map<Integer, NodeMetadata> parseNodeMetadata(final GfaFile gfa, final Map<Integer, Long> byteOffsets)
            throws ParseException {

        final Map<Integer, NodeMetadata> result = new HashMap<>(byteOffsets.size());

        final RandomAccessFile gfaRandomAccessFile = gfa.getRandomAccessFile();

        for (final Map.Entry<Integer, Long> entry : byteOffsets.entrySet()) {
            final long byteOffset = entry.getValue();

            final String line = getLine(gfaRandomAccessFile, byteOffset);

            validateLine(line, "S", byteOffset);

            final StringTokenizer st = initializeStringTokenizer(line, byteOffset);

            try {
                st.nextToken();
                final String name = st.nextToken();
                final String sequence = st.nextToken();
                st.nextToken(); // Ignore asterisk
                final List<String> genomes = parseGenomes(st.nextToken(), byteOffset);

                result.put(entry.getKey(), new NodeMetadata(name, sequence, genomes));
            } catch (final NoSuchElementException e) {
                throw new ParseException("Not enough parameters for segment at position " + byteOffset + ".", e);
            }
        }

        return result;
    }

    /**
     * Parses the metadata of a link (edge) to an {@link EdgeMetadata} object.
     *
     * @param gfa        string containing the contents of the GFA file
     * @param lineNumber the line number where the edge should be located
     * @return an {@link EdgeMetadata} object containing a link's metadata
     * @throws ParseException if the GFA file or given line is invalid
     */
    public EdgeMetadata parseEdgeMetadata(final GfaFile gfa, final int lineNumber) throws ParseException {
        final String line = getLine(createReader(gfa), lineNumber);

        validateLine(line, "L", lineNumber);

        final StringTokenizer st = initializeStringTokenizer(line, lineNumber);

        try {
            st.nextToken();
            st.nextToken();
            final String fromOrient = st.nextToken();
            st.nextToken();
            final String toOrient = st.nextToken();
            final String overlap = st.nextToken();

            return new EdgeMetadata(fromOrient, toOrient, overlap);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for link on line " + lineNumber, e);
        }
    }

    /**
     * Finds a specific line within the string representation of a GFA file.
     * <p>
     * Will be removed once edges are retrieved via byte offsets.
     *
     * @param gfa        string containing the contents of the GFA file
     * @param lineNumber the line number where the node or edge should be located
     * @return the line of the file belonging to the node or edge
     * @throws ParseException if the line number is out of bounds
     */
    private String getLine(final BufferedReader gfa, final int lineNumber) throws ParseException {
        if (lineNumber <= 0) {
            throw new ParseException("Line " + lineNumber + " is not a valid line number.");
        }

        final int offset = (lineNumber - 1) - readerPosition;
        if (offset < 0) {
            throw new ParseException("Line " + lineNumber + " cannot be lower than the previous line number.");
        }

        final String line = gfa.lines().skip(offset).findFirst().orElseThrow(
                () -> new ParseException("Line " + lineNumber + " is not found in GFA file.")
        );

        readerPosition = lineNumber;

        return line;
    }

    /**
     * Finds a specific line within the string representation of a GFA file.
     *
     * @param gfa        string containing the contents of the GFA file
     * @param byteOffset the line number where the node or edge should be located
     * @return the line of the file belonging to the node or edge
     * @throws ParseException if the line number is out of bounds
     */
    private String getLine(final RandomAccessFile gfa, final long byteOffset) throws ParseException {
        if (byteOffset < 0) {
            throw new ParseException("Byte offset " + byteOffset + " is not a valid byte offset.");
        }

        try {
            gfa.seek(byteOffset);

            return gfa.readLine();
        } catch (final IOException e) {
            throw new ParseException("Line could not be read.", e);
        }
    }

    /**
     * Validates if the given line starts with the expected prefix.
     *
     * @param line           a line of the GFA file that 'might' belong to a node or an edge
     * @param expectedPrefix the expected prefix of the line
     * @param byteOffset     the byte offset where the node or edge is located
     * @throws ParseException if the line does not start with the prefix
     */
    private void validateLine(final String line, final String expectedPrefix,
                              final long byteOffset) throws ParseException {
        if (!line.startsWith(expectedPrefix)) {
            throw new ParseException("Expected line at position " + byteOffset + " to start with " + expectedPrefix);
        }
    }

    /**
     * Initializes a {@link StringTokenizer} to be used for parsing.
     *
     * @param line       the line of the GFA file belonging to a node or an edge
     * @param byteOffset the byte offset where the node or edge is located
     * @return an initialized {@link StringTokenizer} object
     * @throws ParseException if the given line does not contain any tabs
     */
    private StringTokenizer initializeStringTokenizer(final String line, final long byteOffset) throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            throw new ParseException("Line at position " + byteOffset + " is not valid");
        }
        return st;
    }

    /**
     * Creates a new reader for a GFA file and resets the reader position.
     *
     * @param gfaFile a reference to the current {@link GfaFile}
     * @return {@link BufferedReader} for a {@link GfaFile}
     * @throws ParseException if a buffered reader cannot be initiated
     */
    private BufferedReader createReader(final GfaFile gfaFile) throws ParseException {
        readerPosition = 0;
        return gfaFile.readFile();
    }

    /**
     * Returns all genomes of the node on the given {@code lineNumber}.
     * <p>
     * It does this by reading the appropriate genome header field of that line.
     *
     * @param headerField the header field (including the prefix) to parse
     * @param byteOffset  the byte offset of the current line
     * @return the list of genomes in that file
     * @throws ParseException if the GFA file or given line is invalid
     */
    private List<String> parseGenomes(final String headerField, final long byteOffset) throws ParseException {
        if (!headerField.startsWith(GENOME_LIST_HEADER_PREFIX)) {
            throw new ParseException("Expected genome header at position " + byteOffset + ".");
        }

        final String genomeListString = headerField.substring(GENOME_LIST_HEADER_PREFIX.length());
        final StringTokenizer bodyTokenizer = new StringTokenizer(genomeListString, ";");
        final List<String> genomes = new ArrayList<>();

        while (bodyTokenizer.hasMoreTokens()) {
            final String nextGenome = bodyTokenizer.nextToken();

            genomes.add(nextGenome);
        }

        return genomes;
    }
}
