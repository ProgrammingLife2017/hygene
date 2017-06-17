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
        return UnsignedInteger.toLong(data[Graph.NODE_BYTE_OFFSET_INDEX]);
    }

    /**
     * Getter for the {@link Node}'s sequence length.
     *
     * @return the sequence length of the {@link Node}
     */
    public int getSequenceLength() {
        return data[Graph.NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for {@link Node} color.
     *
     * @return the node color
     */
    public NodeColor getColor() {
        return NodeColor.values()[data[Graph.NODE_COLOR_INDEX]];
    }

    /**
     * Getter for the computed unscaled X position.
     *
     * @return the computed unscaled X position
     */
    public int getUnscaledXPosition() {
        return data[Graph.UNSCALED_X_POSITION_INDEX];
    }

    /**
     * Getter for the computed unscaled Y position.
     *
     * @return the computed unscaled Y position
     */
    public int getUnscaledYPosition() {
        return data[Graph.UNSCALED_Y_POSITION_INDEX];
    }

    /**
     * Getter for the number of outgoing edges of this node.
     *
     * @return the number of outgoing edges
     */
    public int getNumberOfOutgoingEdges() {
        return data[Graph.NODE_OUTGOING_EDGES_INDEX];
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
        final int metadataLength = Graph.NODE_EDGE_DATA_OFFSET - 1;
        final int outgoingEdgesLength = data[Graph.NODE_OUTGOING_EDGES_INDEX] * Graph.EDGE_DATA_SIZE;

        return (data.length - metadataLength - outgoingEdgesLength) / Graph.EDGE_DATA_SIZE;
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
                    final int offset = Graph.NODE_EDGE_DATA_OFFSET;

                    for (int i = 0; i < getNumberOfOutgoingEdges(); i++) {
                        final int to = data[offset + i * Graph.EDGE_DATA_SIZE];
                        final int byteOffset = data[offset + i * Graph.EDGE_DATA_SIZE + Graph.EDGE_BYTE_OFFSET_OFFSET];

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
                    final int offset = Graph.NODE_EDGE_DATA_OFFSET + getNumberOfOutgoingEdges() * Graph.EDGE_DATA_SIZE;

                    for (int i = 0; i < getNumberOfIncomingEdges(); i++) {
                        final int from = data[offset + i * Graph.EDGE_DATA_SIZE];
                        final int byteOffset = data[offset + i * Graph.EDGE_DATA_SIZE + Graph.EDGE_BYTE_OFFSET_OFFSET];

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
