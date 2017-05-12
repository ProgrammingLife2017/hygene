package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.parser.ParseException;

import java.util.Set;
import java.util.TreeSet;


/**
 * The {@code Node} class wraps around a node array and provides convenience methods.
 * <p>
 * Node array format:
 * [[nodeLineNumber, nodeColor, xPosition, yPosition, outgoingEdges, edge1, edge1LineNumber...]]
 */
public final class Node {
    static final int NODE_LINE_NUMBER_INDEX = 0;
    static final int NODE_COLOR_INDEX = 1;
    static final int UNSCALED_X_POSITION_INDEX = 2;
    static final int UNSCALED_Y_POSITION_INDEX = 3;
    static final int NODE_OUTGOING_EDGES_INDEX = 4;
    static final int NODE_EDGE_DATA_OFFSET = 5;
    static final int EDGE_LINE_NUMBER_OFFSET = 1;
    static final int EDGE_DATA_SIZE = 2;

    private final int id;
    private final int[] data;
    private final @Nullable Graph graph;

    private volatile @MonotonicNonNull Set<Edge> incomingEdges;
    private volatile @MonotonicNonNull Set<Edge> outgoingEdges;


    /**
     * Constructor for {@code Node}.
     *
     * @param id    the node's id
     * @param data  the node array representing the node's data
     * @param graph the graph containing the node, in case there is no graph (yet)
     *              for this node to be on, null is accepted
     */
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
    public int[] toArray() {
        return data;
    }

    /**
     * Getter for {@code Node} id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for {@link Node} metadata line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return data[NODE_LINE_NUMBER_INDEX];
    }

    /**
     * Getter for {@code Node} color.
     *
     * @return the node color
     */
    public NodeColor getColor() {
        return NodeColor.values()[data[NODE_COLOR_INDEX]];
    }

    /**
     * Getter for the computed unscaled X position.
     *
     * @return the computed unscaled X position.
     */
    public int getUnscaledXPosition() {
        return data[UNSCALED_X_POSITION_INDEX];
    }

    /**
     * Getter for the computed unscaled Y position.
     *
     * @return the computed unscaled Y position.
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
     * @return the number of incoming edges.
     */
    public int getNumberOfIncomingEdges() {
        final int metadataLength = NODE_EDGE_DATA_OFFSET - 1;
        final int outgoingEdgesLength = data[NODE_OUTGOING_EDGES_INDEX] * EDGE_DATA_SIZE;

        return (data.length - metadataLength - outgoingEdgesLength) / EDGE_DATA_SIZE;
    }

    /**
     * Creates a set containing the outgoing edges of the {@code Node}.
     * <p>
     * Warning: This method creates of copy of the edges data and should be used with caution.
     *
     * @return set containing the outgoing edges of the {@code Node}.
     */
    public Set<Edge> getOutgoingEdges() {
        if (outgoingEdges == null) {
            synchronized (Node.class) {
                if (outgoingEdges == null) {
                    Set<Edge> newOutgoingEdges = new TreeSet<>();

                    final int offset = NODE_EDGE_DATA_OFFSET;

                    for (int i = 0; i < getNumberOfOutgoingEdges(); i++) {
                        int to = data[offset + i * EDGE_DATA_SIZE];
                        int lineNumber = data[offset + i * EDGE_DATA_SIZE + EDGE_LINE_NUMBER_OFFSET];
                        newOutgoingEdges.add(new Edge(id, to, lineNumber, graph));
                    }
                    this.outgoingEdges = newOutgoingEdges;
                }
            }
        }
        return outgoingEdges;
    }

    /**
     * Creates a set containing the incoming edges of the {@code Node}.
     * <p>
     * Warning: This method creates of copy of the edges data and should be used with caution.
     *
     * @return set containing the incoming edges of the {@code Node}.
     */
    public Set<Edge> getIncomingEdges() {
        if (incomingEdges == null) {
            synchronized (Node.class) {
                if (incomingEdges == null) {
                    Set<Edge> newIncomingEdges = new TreeSet<>();

                    final int offset = NODE_EDGE_DATA_OFFSET + getNumberOfOutgoingEdges() * EDGE_DATA_SIZE;

                    for (int i = 0; i < getNumberOfIncomingEdges(); i++) {
                        int from = data[offset + i * EDGE_DATA_SIZE];
                        int lineNumber = data[offset + i * EDGE_DATA_SIZE + EDGE_LINE_NUMBER_OFFSET];
                        newIncomingEdges.add(new Edge(from, id, lineNumber, graph));
                    }

                    this.incomingEdges = newIncomingEdges;
                }
            }
        }
        return incomingEdges;
    }

    /**
     * Getter for the {@link Graph} reference.
     *
     * @return a reference to the {@link Graph} the edge belongs to.
     */
    public @Nullable Graph getGraph() {
        return graph;
    }

    /**
     * Retrieves metadata of the node.
     *
     * @return metadata of the bode.
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public NodeMetadata retrieveMetadata() throws ParseException {
        return NodeMetadata.retrieveFor(this);
    }
}
