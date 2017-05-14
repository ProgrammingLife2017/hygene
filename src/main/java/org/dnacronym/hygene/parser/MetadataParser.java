package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * This class handles parsing of metadata of nodes and edges contained in GFA Files.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class MetadataParser {

    /**
     * Parses the metadata of a segment (node) to a {@link NodeMetadata} object.
     *
     * @param gfa        string containing the contents of the GFA file
     * @param lineNumber the line number where the node should be located
     * @return a {@link NodeMetadata} object containing a segment's metadata.
     * @throws ParseException if the GFA file or given line is invalid
     */
    public NodeMetadata parseNodeMetadata(final String gfa, final int lineNumber) throws ParseException {
        final String line = getLine(gfa, lineNumber);

        validateLine(line, "S", lineNumber);

        final StringTokenizer st = initializeStringTokenizer(line, lineNumber);

        try {
            st.nextToken();
            final String name = st.nextToken();
            final String sequence = st.nextToken();

            return new NodeMetadata(name, sequence);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for segment on line " + lineNumber, e);
        }
    }

    /**
     * Parses the metadata of a link (edge) to an {@link EdgeMetadata} object.
     *
     * @param gfa        string containing the contents of the GFA file
     * @param lineNumber the line number where the edge should be located
     * @return an {@link EdgeMetadata} object containing a link's metadata.
     * @throws ParseException if the GFA file or given line is invalid
     */
    public EdgeMetadata parseEdgeMetadata(final String gfa, final int lineNumber) throws ParseException {
        final String line = getLine(gfa, lineNumber);

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
     *
     * @param gfa        string containing the contents of the GFA file
     * @param lineNumber the line number where the node or edge should be located
     * @return the line of the file belonging to the node or edge.
     * @throws ParseException if the line number is out of bounds
     */
    private String getLine(final String gfa, final int lineNumber) throws ParseException {
        final int arrayIndex = lineNumber - 1;

        final String[] lines = gfa.split("\\R");

        if (arrayIndex < 0 || arrayIndex > lines.length) {
            throw new ParseException("Line " + lineNumber + " is not found in GFA file.");
        }

        return gfa.split("\\R")[lineNumber - 1];
    }

    /**
     * Validates if the given line starts with the expected prefix.
     *
     * @param line           a line of the GFA file that 'might' belong to a node or an edge
     * @param expectedPrefix the expected prefix of the line
     * @param lineNumber     the line number where the node or edge is located
     * @throws ParseException if the line does not start with the prefix
     */
    private void validateLine(final String line, final String expectedPrefix,
                              final int lineNumber) throws ParseException {
        if (!line.startsWith(expectedPrefix)) {
            throw new ParseException("Expected line " + lineNumber + " to start with " + expectedPrefix);
        }
    }

    /**
     * Initializes a {@link StringTokenizer} to be used for parsing.
     *
     * @param line       the line of the GFA file belonging to a node or an edge
     * @param lineNumber the line number where the node or edge is located
     * @return an initialized {@link StringTokenizer} object.
     * @throws ParseException if the given line does not contain any tabs
     */
    private StringTokenizer initializeStringTokenizer(final String line, final int lineNumber) throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            throw new ParseException("Line " + lineNumber + " is not valid");
        }
        return st;
    }
}
