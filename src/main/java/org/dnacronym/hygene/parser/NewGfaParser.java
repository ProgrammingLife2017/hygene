package org.dnacronym.hygene.parser;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeColor;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Parses GFA to a {@code Graph}.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class NewGfaParser {
    private final Map<String, Integer> nodeIds; // node id string => nodeArrays index (internal node id)
    private final AtomicInteger nodeVectorPosition = new AtomicInteger(0);
    private int[][] nodeVectors;

    /**
     * Constructs and initializes a new instance of {@code GfaParser}.
     */
    public NewGfaParser() {
        this.nodeIds = new ConcurrentHashMap<>();
        this.nodeVectors = new int[0][];
    }

    /**
     * Parses a GFA file to a {@code Graph}.
     *
     * @param gfaFile an instance of {@code GfaFile}
     * @return a {@code Graph}
     * @throws ParseException if the given {@code String} is not GFA-compliant
     */
    @EnsuresNonNull("nodeVectors")
    public Graph parse(final NewGfaFile gfaFile) throws ParseException {
        BufferedReader gfa = gfaFile.readFile();

        allocateNodes(gfa);

        nodeVectors = new int[nodeIds.size()][];
        Arrays.setAll(nodeVectors, i -> Node.createEmptyNodeArray());

        // Get new buffered reader
        gfa = gfaFile.readFile();

        try {
            int offset = 0;
            String line;
            while ((line = gfa.readLine()) != null) {
                parseLine(line, offset + 1);
                offset++;
            }
        } catch (final IOException e) {
            throw new ParseException("Error while reading file: " + e.getMessage(), e);
        }

        return new Graph(nodeVectors, gfaFile);
    }

    /**
     * Allocates the required internal node IDs.
     * <p>
     * This step is necessary because we need to know the internal node IDs
     * upfront to be able to add edges to the correct node vectors.
     *
     * @param gfa lines of a GFA-compliant {@code String}
     */
    private void allocateNodes(final BufferedReader gfa) {
        gfa.lines().parallel().filter(line -> line.startsWith("S\t")).forEach(line -> {
            nodeIds.put(parseNodeName(line), nodeVectorPosition.get());
            nodeVectorPosition.incrementAndGet();
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

            final int nodeId = Optional.ofNullable(nodeIds.get(name)).orElseThrow(
                    () -> new ParseException("Node name '" + name + "' not registered with a node id")
            );

            nodeVectors[nodeId] = NodeBuilder.fromArray(nodeId, nodeVectors[nodeId])
                    .withLineNumber(offset)
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

            final int fromId = Optional.ofNullable(nodeIds.get(from)).orElseThrow(
                    () -> new ParseException("Link has reference to non existing node " + from)
            );
            final int toId = Optional.ofNullable(nodeIds.get(to)).orElseThrow(
                    () -> new ParseException("Link has reference to non existing node " + to)
            );

            addIncomingEdge(fromId, toId, offset);
            addOutgoingEdge(fromId, toId, offset);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for link on line " + offset, e);
        }
    }

    /**
     * Parses the name of the node (the node id) from a GFA file line.
     *
     * @param line a GFA file line
     * @return the name of the node (the node id)
     */
    private String parseNodeName(final String line) {
        final String name = line.substring(2);

        return name.substring(0, name.indexOf('\t'));
    }

    /**
     * Adds an incoming edge to the node vector.
     *
     * @param fromId node ID of edge start node
     * @param toId   node ID of edge end node
     * @param offset line number of edge
     */
    private void addIncomingEdge(final int fromId, final int toId, final int offset) {
        nodeVectors[toId] = NodeBuilder.fromArray(toId, nodeVectors[toId])
                .withIncomingEdge(fromId, offset).toArray();
    }

    /**
     * Adds an outgoing edge to the node vector.
     *
     * @param fromId node ID of edge start node
     * @param toId   node ID of edge end node
     * @param offset line number of edge
     */
    private void addOutgoingEdge(final int fromId, final int toId, final int offset) {
        nodeVectors[fromId] = NodeBuilder.fromArray(fromId, nodeVectors[fromId])
                .withOutgoingEdge(toId, offset).toArray();
    }
}
