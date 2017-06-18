package org.dnacronym.hygene.graph;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.core.UnsignedInteger;
import org.dnacronym.hygene.model.NodeColor;

import java.util.Set;
import java.util.TreeSet;


/**
 * The {@link ArrayBasedNode} class wraps around a node array and provides convenience methods.
 * <p>
 * ArrayBasedNode array format:
 * [[nodeByteOffset, sequenceLength, nodeColor, xPosition, outgoingEdges, edge1, edge1ByteOffset...]]
 */
public final class ArrayBasedNode {
    private final int id;
    private final int[] data;
    private final @Nullable Graph graph;

    private volatile @MonotonicNonNull Set<ArrayBasedEdge> incomingEdges;
    private volatile @MonotonicNonNull Set<ArrayBasedEdge> outgoingEdges;


    /**
     * Constructor for {@link ArrayBasedNode}.
     *
     * @param id    the node's id
     * @param data  the node array representing the node's data
     * @param graph the graph containing the node, in case there is no graph (yet) for this node to be on, null is
     *              accepted
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    // Performance
    ArrayBasedNode(final int id, final int[] data, final @Nullable Graph graph) {
        this.id = id;
        this.data = data;
        this.graph = graph;
    }


    /**
     * Creates a new {@link ArrayBasedNode} from a given graph for a give node id.
     *
     * @param graph the graph
     * @param id    the node id
     * @return a new {@link ArrayBasedNode} from a given graph for a give node id
     */
    public static ArrayBasedNode fromGraph(final Graph graph, final int id) {
        return new ArrayBasedNode(id, graph.getNodeArray(id), graph);
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
     * Getter for {@link ArrayBasedNode} id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the {@link ArrayBasedNode}'s byte offset.
     *
     * @return the byte offset
     */
    public long getByteOffset() {
        return UnsignedInteger.toLong(data[Graph.NODE_BYTE_OFFSET_INDEX]);
    }

    /**
     * Getter for the {@link ArrayBasedNode}'s sequence length.
     *
     * @return the sequence length of the {@link ArrayBasedNode}
     */
    public int getSequenceLength() {
        return data[Graph.NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for {@link ArrayBasedNode} color.
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
     * Creates a set containing the outgoing edges of the {@link ArrayBasedNode}.
     * <p>
     * Warning: This method creates of copy of the edges data and should be used with caution.
     *
     * @return set containing the outgoing edges of the {@link ArrayBasedNode}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Unique instance per iteration
    public Set<ArrayBasedEdge> getOutgoingEdges() {
        if (outgoingEdges == null) {
            synchronized (ArrayBasedNode.class) {
                if (outgoingEdges == null) {
                    final Set<ArrayBasedEdge> newOutgoingEdges = new TreeSet<>();
                    final int offset = Graph.NODE_EDGE_DATA_OFFSET;

                    for (int i = 0; i < getNumberOfOutgoingEdges(); i++) {
                        final int to = data[offset + i * Graph.EDGE_DATA_SIZE];
                        final int byteOffset = data[offset + i * Graph.EDGE_DATA_SIZE + Graph.EDGE_BYTE_OFFSET_OFFSET];

                        final ArrayBasedEdge edge = new ArrayBasedEdge(id, to, byteOffset, graph);
                        newOutgoingEdges.add(edge);
                    }
                    this.outgoingEdges = newOutgoingEdges;
                }
            }
        }
        return outgoingEdges;
    }

    /**
     * Creates a set containing the incoming edges of the {@link ArrayBasedNode}.
     * <p>
     * Warning: This method creates of copy of the edges data and should be used with caution.
     *
     * @return set containing the incoming edges of the {@link ArrayBasedNode}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Unique instance per iteration
    public Set<ArrayBasedEdge> getIncomingEdges() {
        if (incomingEdges == null) {
            synchronized (ArrayBasedNode.class) {
                if (incomingEdges == null) {
                    final Set<ArrayBasedEdge> newIncomingEdges = new TreeSet<>();
                    final int offset = Graph.NODE_EDGE_DATA_OFFSET + getNumberOfOutgoingEdges() * Graph.EDGE_DATA_SIZE;

                    for (int i = 0; i < getNumberOfIncomingEdges(); i++) {
                        final int from = data[offset + i * Graph.EDGE_DATA_SIZE];
                        final int byteOffset = data[offset + i * Graph.EDGE_DATA_SIZE + Graph.EDGE_BYTE_OFFSET_OFFSET];

                        final ArrayBasedEdge edge = new ArrayBasedEdge(from, id, byteOffset, graph);
                        newIncomingEdges.add(edge);
                    }

                    this.incomingEdges = newIncomingEdges;
                }
            }
        }
        return incomingEdges;
    }
}
