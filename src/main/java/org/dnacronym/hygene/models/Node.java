package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.core.UnsignedInteger;

import java.util.Set;
import java.util.TreeSet;


/**
 * The {@link Node} class wraps around a node array and provides convenience methods.
 * <p>
 * Node array format:
 * [[nodeByteOffset, sequenceLength, nodeColor, xPosition, yPosition, outgoingEdges, edge1, edge1ByteOffset...]]
 */
public final class Node {
    public static final int NODE_BYTE_OFFSET_INDEX = 0;
    public static final int NODE_SEQUENCE_LENGTH_INDEX = 1;
    public static final int NODE_COLOR_INDEX = 2;
    public static final int UNSCALED_X_POSITION_INDEX = 3;
    public static final int UNSCALED_Y_POSITION_INDEX = 4;
    public static final int NODE_OUTGOING_EDGES_INDEX = 5;
    public static final int NODE_EDGE_DATA_OFFSET = 6;
    public static final int EDGE_BYTE_OFFSET_OFFSET = 1;
    public static final int EDGE_DATA_SIZE = 2;

    private final int id;
    private final int[] data;
    private final @Nullable Graph graph;

    private volatile @MonotonicNonNull Set<Edge> incomingEdges;
    private volatile @MonotonicNonNull Set<Edge> outgoingEdges;


    /**
     * Constructor for {@link Node}.
     *
     * @param id    the node's id
     * @param data  the node array representing the node's data
     * @param graph the graph containing the node, in case there is no graph (yet) for this node to be on, null is
     *              accepted
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    // Performance
    Node(final int id, final int[] data, final @Nullable Graph graph) {
        this.id = id;
        this.data = data;
        this.graph = graph;
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
     * Getter for the node's array which contains its metadata.
     *
     * @return the node array
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    @SuppressWarnings("PMD.MethodReturnsInternalArray") // Performance
    public int[] toArray() {
        return data;
    }

    /**
     * Getter for {@link Node} id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the {@link Node}'s byte offset.
     *
     * @return the byte offset
     */
    public long getByteOffset() {
        return UnsignedInteger.toLong(data[NODE_BYTE_OFFSET_INDEX]);
    }

    /**
     * Getter for the {@link Node}'s sequence length.
     *
     * @return the sequence length of the {@link Node}
     */
    public int getSequenceLength() {
        return data[NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for {@link Node} color.
     *
     * @return the node color
     */
    public NodeColor getColor() {
        return NodeColor.values()[data[NODE_COLOR_INDEX]];
    }

    /**
     * Getter for the computed unscaled X position.
     *
     * @return the computed unscaled X position
     */
    public int getUnscaledXPosition() {
        return data[UNSCALED_X_POSITION_INDEX];
    }

    /**
     * Getter for the computed unscaled Y position.
     *
     * @return the computed unscaled Y position
     */
    public int getUnscaledYPosition() {
        return data[UNSCALED_Y_POSITION_INDEX];
    }

    /**
     * Getter for the number of outgoing edges of this node.
     *
     * @return the number of outgoing edges
     */
    public int getNumberOfOutgoingEdges() {
        return data[NODE_OUTGOING_EDGES_INDEX];
    }

    /**
     * Getter for the number of incoming edges of this node.
     * <p>
     * Computed by subtracting the length of the metadata and the length of the outgoing edges
     * from the length of the array. This result is divided by the length of a single edge, in
     * order to get the total number of incoming edges.
     *
     * @return the number of incoming edges
     */
    public int getNumberOfIncomingEdges() {
        final int metadataLength = NODE_EDGE_DATA_OFFSET - 1;
        final int outgoingEdgesLength = data[NODE_OUTGOING_EDGES_INDEX] * EDGE_DATA_SIZE;

        return (data.length - metadataLength - outgoingEdgesLength) / EDGE_DATA_SIZE;
    }

    /**
     * Creates a set containing the outgoing edges of the {@link Node}.
     * <p>
     * Warning: This method creates of copy of the edges data and should be used with caution.
     *
     * @return set containing the outgoing edges of the {@link Node}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Unique instance per iteration
    public Set<Edge> getOutgoingEdges() {
        if (outgoingEdges == null) {
            synchronized (Node.class) {
                if (outgoingEdges == null) {
                    final Set<Edge> newOutgoingEdges = new TreeSet<>();
                    final int offset = NODE_EDGE_DATA_OFFSET;

                    for (int i = 0; i < getNumberOfOutgoingEdges(); i++) {
                        final int to = data[offset + i * EDGE_DATA_SIZE];
                        final int byteOffset = data[offset + i * EDGE_DATA_SIZE + EDGE_BYTE_OFFSET_OFFSET];

                        final Edge edge = new Edge(id, to, byteOffset, graph);
                        newOutgoingEdges.add(edge);
                    }
                    this.outgoingEdges = newOutgoingEdges;
                }
            }
        }
        return outgoingEdges;
    }

    /**
     * Creates a set containing the incoming edges of the {@link Node}.
     * <p>
     * Warning: This method creates of copy of the edges data and should be used with caution.
     *
     * @return set containing the incoming edges of the {@link Node}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Unique instance per iteration
    public Set<Edge> getIncomingEdges() {
        if (incomingEdges == null) {
            synchronized (Node.class) {
                if (incomingEdges == null) {
                    final Set<Edge> newIncomingEdges = new TreeSet<>();
                    final int offset = NODE_EDGE_DATA_OFFSET + getNumberOfOutgoingEdges() * EDGE_DATA_SIZE;

                    for (int i = 0; i < getNumberOfIncomingEdges(); i++) {
                        final int from = data[offset + i * EDGE_DATA_SIZE];
                        final int byteOffset = data[offset + i * EDGE_DATA_SIZE + EDGE_BYTE_OFFSET_OFFSET];

                        final Edge edge = new Edge(from, id, byteOffset, graph);
                        newIncomingEdges.add(edge);
                    }

                    this.incomingEdges = newIncomingEdges;
                }
            }
        }
        return incomingEdges;
    }
}
