package org.dnacronym.hygene.parser;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeBuilder;
import org.dnacronym.hygene.models.NodeColor;
import org.dnacronym.hygene.models.SequenceDirection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


/**
 * Parses GFA to a {@link Graph}.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class NewGfaParser {
    private static final String SOURCE_NAME = "<source>";
    private static final String SINK_NAME = "<sink>";

    private final Map<String, Integer> nodeIds; // node id string => nodeArrays index (internal node id)
    private final AtomicInteger nodeVectorPosition = new AtomicInteger(0);
    private int[][] nodeArrays;

    /**
     * Constructs and initializes a new instance of {@link GfaParser}.
     */
    public NewGfaParser() {
        this.nodeIds = new ConcurrentHashMap<>();
        this.nodeArrays = new int[0][];
    }

    /**
     * Parses a GFA file to a {@link Graph}.
     *
     * @param gfaFile an instance of {@link GfaFile}
     * @return a {@link Graph}
     * @throws ParseException if the given {@link String} is not GFA-compliant
     */
    @EnsuresNonNull("nodeArrays")
    public Graph parse(final GfaFile gfaFile) throws ParseException {
        BufferedReader gfa = gfaFile.readFile();

        allocateNodes(gfa);

        nodeArrays = new int[nodeIds.size()][];
        Arrays.setAll(nodeArrays, i -> Node.createEmptyNodeArray());

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
            throw new ParseException("An error while reading the GFA file.", e);
        }

        final Graph graph = new Graph(nodeArrays, gfaFile);

        addEdgesToSentinelNodes(graph);

        return graph;
    }

    /**
     * Allocates the required internal node IDs.
     * <p>
     * This step is necessary because we need to know the internal node IDs
     * upfront to be able to add edges to the correct node vectors.
     *
     * @param gfa lines of a GFA-compliant {@link String}
     */
    private void allocateNodes(final BufferedReader gfa) {
        addNodeId(SOURCE_NAME);
        gfa.lines().filter(line -> line.startsWith("S\t")).forEach(line -> addNodeId(parseNodeName(line)));
        addNodeId(SINK_NAME);
    }

    /**
     * Add a node ID to the list of node IDs and increment the node vector position.
     *
     * @param nodeName The name of the node as specified in the GFA file
     */
    private void addNodeId(final String nodeName) {
        nodeIds.put(nodeName, nodeVectorPosition.get());
        nodeVectorPosition.incrementAndGet();
    }

    /**
     * Parses a line of a GFA-compliant {@link String} and adds it to the node vectors.
     *
     * @param line   a line of a GFA-compliant {@link String}
     * @param offset the current line number
     * @throws ParseException if the given {@link String}s are not GFA-compliant
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
     * Parses a line to a {@link Segment}.
     *
     * @param st     the {@link StringTokenizer} in which each token is a GFA field
     * @param offset the current line number, used for debugging
     * @throws ParseException if the {@link Segment} is not GFA-compliant
     */
    private void parseSegment(final StringTokenizer st, final int offset) throws ParseException {
        try {
            final String name = st.nextToken();
            final String sequence = st.nextToken();

            final int nodeId = getNodeId(name);

            nodeArrays[nodeId] = NodeBuilder.fromArray(nodeId, nodeArrays[nodeId])
                    .withLineNumber(offset)
                    .withSequenceLength(sequence.length())
                    .withColor(NodeColor.sequenceToColor(sequence))
                    .toArray();

        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for segment on line " + offset, e);
        }
    }

    /**
     * Parses a line to a {@link Link}.
     *
     * @param st     the {@link StringTokenizer} in which each token is a GFA field
     * @param offset the current line number, used for debugging
     * @throws ParseException if the {@link Link} is not GFA-compliant
     */
    private void parseLink(final StringTokenizer st, final int offset) throws ParseException {
        try {
            final String from = st.nextToken();
            st.nextToken();
            final String to = st.nextToken();
            st.nextToken();

            final int fromId = getNodeId(from);
            final int toId = getNodeId(to);

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
        nodeArrays[toId] = NodeBuilder.fromArray(toId, nodeArrays[toId])
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
        nodeArrays[fromId] = NodeBuilder.fromArray(fromId, nodeArrays[fromId])
                .withOutgoingEdge(toId, offset).toArray();
    }

    /**
     * Add edges for nodes without incoming or outgoing edges to the source or sink.
     *
     * @param graph the graph data structure including the source and sink
     * @throws ParseException if the graph has an invalid number of nodes
     */
    private void addEdgesToSentinelNodes(final Graph graph) throws ParseException {
        final int source = getNodeId(SOURCE_NAME);
        final int sink = getNodeId(SINK_NAME);

        if (nodeArrays.length == 2) {
            throw new ParseException("The GFA file should contain at least one segment.");
        }

        IntStream.range(1, nodeArrays.length - 1).parallel().forEach(nodeId -> {
            if (graph.getNeighbourCount(nodeId, SequenceDirection.LEFT) == 0) {
                addIncomingEdge(source, nodeId, -1);
                addOutgoingEdge(source, nodeId, -1);
            }

            if (graph.getNeighbourCount(nodeId, SequenceDirection.RIGHT) == 0) {
                addIncomingEdge(nodeId, sink, -1);
                addOutgoingEdge(nodeId, sink, -1);
            }
        });
    }

    /**
     * Gets node id belonging to a node name.
     *
     * @param nodeName name of the node as specified in the GFA file
     * @return node id belonging to a node name
     * @throws ParseException if the node name does not exist
     */
    private int getNodeId(final String nodeName) throws ParseException {
        return Optional.ofNullable(nodeIds.get(nodeName)).orElseThrow(
                () -> new ParseException("Link has reference to non existing node " + nodeName)
        );
    }
}
