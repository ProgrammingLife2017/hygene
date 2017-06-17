package org.dnacronym.hygene.graph;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.core.UnsignedInteger;
import org.dnacronym.hygene.graph.layout.Fafosp;
import org.dnacronym.hygene.model.NodeColor;
import org.dnacronym.hygene.parser.GfaFile;

import java.util.HashMap;
import java.util.Map;


/**
 * Class wraps around the graph data represented as a nested array and provides utility methods.
 * <p>
 * Node array format:
 * [[nodeByteOffset, sequenceLength, nodeColor, outgoingEdges, xPosition, yPosition, edge1, edge1ByteOffset...]]
 */
public final class Graph {
    public static final int NODE_BYTE_OFFSET_INDEX = 0;
    public static final int NODE_SEQUENCE_LENGTH_INDEX = 1;
    public static final int NODE_COLOR_INDEX = 2;
    public static final int UNSCALED_X_POSITION_INDEX = 3;
    public static final int UNSCALED_Y_POSITION_INDEX = 4;
    public static final int NODE_OUTGOING_EDGES_INDEX = 5;
    public static final int NODE_EDGE_DATA_OFFSET = 6;
    public static final int EDGE_BYTE_OFFSET_OFFSET = 1;
    public static final int EDGE_DATA_SIZE = 2;
    static final int MINIMUM_SEQUENCE_LENGTH = 500;

    private final int[][] nodeArrays;
    /**
     * Maps genome names to their respective indices.
     */
    private final Map<String, String> genomeMapping;
    private final GfaFile gfaFile;

    private @MonotonicNonNull GraphIterator graphIterator;


    /**
     * Constructs a graph from array based data structure.
     *
     * @param nodeArrays    nested array containing the graph's data
     * @param genomeMapping maps genomes to their full name
     * @param gfaFile       a reference to the GFA file from which the graph is created
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    @SuppressWarnings("PMD.ArrayIsStoredDirectly") // Performance
    public Graph(final int[][] nodeArrays, final Map<String, String> genomeMapping, final GfaFile gfaFile) {
        this.nodeArrays = nodeArrays;
        this.gfaFile = gfaFile;
        this.genomeMapping = genomeMapping;
    }

    /**
     * Constructs a graph from array based data structure.
     *
     * @param nodeArrays nested array containing the graph's data
     * @param gfaFile    a reference to the GFA file from which the graph is created
     */
    public Graph(final int[][] nodeArrays, final GfaFile gfaFile) {
        this(nodeArrays, new HashMap<>(), gfaFile);
    }


    /**
     * Creates an empty node array without edge details used to initialize a new node.
     *
     * @return an empty node array
     */
    public static int[] createEmptyNodeArray() {
        return new int[] {0, 0, 0, -1, -1, 0};
    }


    /**
     * Getter for the array representing a {@link Node}'s metadata.
     *
     * @param id the {@link Node}'s id
     * @return the array representing a {@link Node}'s metadata
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray") // Performance
    public int[] getNodeArray(final int id) {
        return nodeArrays[id];
    }

    /**
     * Gets the array representation of all node arrays.
     *
     * @return the array representation of all node arrays
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    @SuppressWarnings("PMD.MethodReturnsInternalArray") // Performance
    public int[][] getNodeArrays() {
        return nodeArrays;
    }

    /**
     * Gets the genome mapping.
     *
     * @return the genome mapping
     */
    public Map<String, String> getGenomeMapping() {
        return genomeMapping;
    }

    /**
     * Getter for the byte offset where the {@link Node}'s metadata resides.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s byte offset
     */
    public long getByteOffset(final int id) {
        return UnsignedInteger.toLong(nodeArrays[id][NODE_BYTE_OFFSET_INDEX]);
    }

    /**
     * Getter for the length of a {@link Node} (capped at a lower bound).
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s (capped) sequence length
     */
    public int getLength(final int id) {
        if (nodeArrays[id][NODE_SEQUENCE_LENGTH_INDEX] < MINIMUM_SEQUENCE_LENGTH) {
            return MINIMUM_SEQUENCE_LENGTH;
        }
        return nodeArrays[id][NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for the raw sequence length (number of base pairs) of a {@link Node}.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s sequence length
     */
    public int getSequenceLength(final int id) {
        return nodeArrays[id][NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for the color of a {@link Node}.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s color
     */
    public NodeColor getColor(final int id) {
        return NodeColor.values()[nodeArrays[id][NODE_COLOR_INDEX]];
    }

    /**
     * Getter for the unscaled x position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled x position
     */
    public int getUnscaledXPosition(final int id) {
        return nodeArrays[id][UNSCALED_X_POSITION_INDEX];
    }

    /**
     * Sets the unscaled x position.
     *
     * @param id                the {@link Node}'s id
     * @param unscaledXPosition the unscaled x position
     */
    public void setUnscaledXPosition(final int id, final int unscaledXPosition) {
        nodeArrays[id][UNSCALED_X_POSITION_INDEX] = unscaledXPosition;
    }

    /**
     * Getter for the unscaled y position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled y position
     */
    public int getUnscaledYPosition(final int id) {
        return nodeArrays[id][UNSCALED_Y_POSITION_INDEX];
    }

    /**
     * Sets the unscaled y position.
     *
     * @param id                the node's id
     * @param unscaledYPosition the unscaled y position
     */
    public void setUnscaledYPosition(final int id, final int unscaledYPosition) {
        nodeArrays[id][UNSCALED_Y_POSITION_INDEX] = unscaledYPosition;
    }

    /**
     * Returns the number of neighbours of a node in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to count
     * @return the number of neighbours of a node in the given direction
     */
    public int getNeighbourCount(final int id, final SequenceDirection direction) {
        return direction.ternary(
                (nodeArrays[id].length
                        - nodeArrays[id][NODE_OUTGOING_EDGES_INDEX] * EDGE_DATA_SIZE
                        - (NODE_OUTGOING_EDGES_INDEX + 1)
                ) / EDGE_DATA_SIZE,
                nodeArrays[id][NODE_OUTGOING_EDGES_INDEX]
        );
    }

    /**
     * Returns the {@link GraphIterator} for this {@link Graph} for iterating over its node.
     *
     * @return the {@link GraphIterator} for this {@link Graph} for iterating over its node
     */
    @EnsuresNonNull("graphIterator")
    public GraphIterator iterator() {
        if (graphIterator == null) {
            graphIterator = new GraphIterator(this);
        }
        return graphIterator;
    }

    /**
     * Returns a new {@link Fafosp} for invoking FAFOSP-related methods.
     * <p>
     * FAFOSP is the Felix Algorithm For Optimal Segment Positioning.
     *
     * @return a new {@link Fafosp}
     */
    public Fafosp fafosp() {
        return new Fafosp(this);
    }

    /**
     * Getter for the {@link GfaFile} instance where the graph belongs to.
     *
     * @return the {@link GfaFile} instance where the graph belongs to
     */
    public GfaFile getGfaFile() {
        return gfaFile;
    }
}
