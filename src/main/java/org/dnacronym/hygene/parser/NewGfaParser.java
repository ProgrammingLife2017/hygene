package org.dnacronym.hygene.parser;

import com.sun.tools.javac.util.GraphUtils;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.dnacronym.hygene.models.NodeColor;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;


/**
 * Parses GFA to a {@code SequenceAlignmentGraph}.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class NewGfaParser {
    private int[][] nodeVectors;
    private int nodeVectorPosition = 0;
    private Map<String, Integer> nodeIds; //Node id string => nodeVectors index (internal node id)

    /**
     * Constructs and initializes a new instance of {@code GfaParser}.
     */
    public NewGfaParser() {
        this.nodeIds = new HashMap<>();
        this.nodeVectors = new int[0][];
    }

    /**
     * Parses a GFA-compliant {@code String} to a {@code SequenceAlignmentGraph}.
     *
     * @param gfa a GFA-compliant {@code String}
     * @return a {@code SequenceAlignmentGraph}
     * @throws ParseException if the given {@code String} is not GFA-compliant
     */
    @EnsuresNonNull("nodeVectors")
    public Graph parse(final String gfa) throws ParseException {
        final String[] lines = gfa.split("\\R");

        allocateNodes(lines);

        nodeVectors = new int[nodeIds.size()][];

        for (int offset = 0; offset < lines.length; offset++) {
            parseLine(lines[offset], offset);
        }

        return new Graph(nodeVectors);
    }

    /**
     * Allocates the required internal node IDs. This step is necessary because we need to know
     * the internal node IDs upfront to be able to add edges to the correct node vectors.
     *
     * @param lines lines of a GFA-compliant {@code String}
     */
    private void allocateNodes(final String[] lines) {
        Arrays.stream(lines).filter(line -> line.startsWith("S\t")).forEach(line -> {
            final String name = line.substring(2).split("\t")[0];
            nodeIds.put(name, nodeVectorPosition);
            nodeVectorPosition++;
        });
    }

    /**
     * Parses a line of a GFA-compliant {@code String} and adds it to the node vectors.
     *
     * @param line   a line of a GFA-compliant {@code String}
     * @param offset the current line number
     * @throws ParseException if the given {@code String}s are not GFA-compliant
     */
    private void parseLine(final String line, final int offset) throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            return;
        }

        final String recordType = st.nextToken();
        switch (recordType) {
            case "H":
            case "C":
            case "P":
                break;

            case "S":
                parseSegment(st, offset);
                break;

            case "L":
                parseLink(st, offset);
                break;

            default:
                throw new ParseException("Unknown record type `" + recordType + "` on line " + offset);
        }
    }

    /**
     * Parses a line to a {@code Segment}.
     *
     * @param st     the {@code StringTokenizer} in which each token is a GFA field
     * @param offset the current line number, used for debugging
     * @throws ParseException if the {@code Segment} is not GFA-compliant
     */
    private void parseSegment(final StringTokenizer st, final int offset) throws ParseException {
        try {
            final String name = st.nextToken();
            final String sequence = st.nextToken();

            nodeVectors[nodeIds.get(name)] = NodeBuilder.start()
                    .withLineNumber(offset + 1)
                    .withColor(NodeColor.sequenceToColor(sequence))
                    .toArray();

        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for segment on line " + offset, e);
        }
    }

    /**
     * Parses a line to a {@code Link}.
     *
     * @param st     the {@code StringTokenizer} in which each token is a GFA field
     * @param offset the current line number, used for debugging
     * @throws ParseException if the {@code Link} is not GFA-compliant
     */
    private void parseLink(final StringTokenizer st, final int offset) throws ParseException {
        try {
            final String from = st.nextToken();
            st.nextToken();
            final String to = st.nextToken();
            st.nextToken();

            int fromId = Optional.ofNullable(nodeIds.get(from)).orElseThrow(
                    () -> new ParseException("Link has reference to non existing node " + from)
            );
            int toId = Optional.ofNullable(nodeIds.get(to)).orElseThrow(
                    () -> new ParseException("Link has reference to non existing node " + to)
            );

            addIncomingEdge(fromId, toId, offset);
            addOutgoingEdge(fromId, toId, offset);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for link on line " + offset, e);
        }
    }

    /**
     * Adds an outgoing edge to the node vector.
     *
     * @param fromId node ID of edge start node
     * @param toId node ID of edge end node
     * @param offset line number of edge
     */
    private void addOutgoingEdge(final int fromId, final int toId, final int offset) {
        nodeVectors[fromId] = NodeBuilder.fromArray(fromId, nodeVectors[fromId])
                .withOutgoingEdge(toId, offset + 1).toArray();
    }

    /**
     * Adds an incoming edge to the node vector.
     *
     * @param fromId node ID of edge start node
     * @param toId node ID of edge end node
     * @param offset line number of edge
     */
    private void addIncomingEdge(final int fromId, final int toId, final int offset) {
        nodeVectors[toId] = NodeBuilder.fromArray(toId, nodeVectors[toId])
                .withIncomingEdge(fromId, offset + 1).toArray();
    }
}
