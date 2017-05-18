package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.GfaFile;


/**
 * Class wraps around the graph data represented as a nested array and provides utility methods.
 * <p>
 * Node array format:
 * [[nodeLineNumber, sequenceLength, nodeColor, outgoingEdges, xPosition, yPosition, edge1, edge1LineNumber...]]
 */
public final class Graph {
    private final int[][] nodeArrays;
    private final GfaFile gfaFile;

    private @MonotonicNonNull GraphIterator iterator;


    /**
     * Constructs a graph from array based data structure.
     *
     * @param nodeArrays nested array containing the graphs data
     * @param gfaFile    a reference to the GFA file from which the graph is created
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    public Graph(final int[][] nodeArrays, final GfaFile gfaFile) {
        this.nodeArrays = nodeArrays;
        this.gfaFile = gfaFile;
    }


    /**
     * Creates a new {@link Node} object containing a reference to the array of that node inside the graph array.
     *
     * @param id the id of the node
     * @return the created {@link Node} object
     */
    public Node getNode(final int id) {
        return new Node(id, nodeArrays[id], this);
    }

    /**
     * Getter for the array representing a {@link Node}'s metadata.
     *
     * @param id the {@link Node}'s id
     * @return the array representing a {@link Node}'s metadata.
     */
    public int[] getNodeArray(final int id) {
        return nodeArrays[id];
    }

    /**
     * Gets the array representation of all node arrays.
     *
     * @return the array representation of all node arrays.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    public int[][] getNodeArrays() {
        return nodeArrays;
    }

    /**
     * Getter for the line number where the {@link Node}'s metadata resides.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s line number.
     */
    public int getLineNumber(final int id) {
        return nodeArrays[id][Node.NODE_LINE_NUMBER_INDEX];
    }

    /**
     * Getter for the sequence length of a {@link Node}.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s sequence length.
     */
    public int getSequenceLength(final int id) {
        return nodeArrays[id][Node.NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for the color of a {@link Node}.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s color.
     */
    public NodeColor getColor(final int id) {
        return NodeColor.values()[nodeArrays[id][Node.NODE_COLOR_INDEX]];
    }

    /**
     * Getter for the unscaled x position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled x position.
     */
    public int getUnscaledXPosition(final int id) {
        return nodeArrays[id][Node.UNSCALED_X_POSITION_INDEX];
    }

    /**
     * Sets the unscaled x position.
     *
     * @param id                the {@link Node}'s id
     * @param unscaledXPosition the unscaled x position
     */
    void setUnscaledXPosition(final int id, final int unscaledXPosition) {
        nodeArrays[id][Node.UNSCALED_X_POSITION_INDEX] = unscaledXPosition;
    }

    /**
     * Getter for the unscaled y position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled y position.
     */
    public int getUnscaledYPosition(final int id) {
        return nodeArrays[id][Node.UNSCALED_Y_POSITION_INDEX];
    }

    /**
     * Sets the unscaled y position.
     *
     * @param id                the node's id
     * @param unscaledYPosition the unscaled y position
     */
    void setUnscaledYPosition(final int id, final int unscaledYPosition) {
        nodeArrays[id][Node.UNSCALED_Y_POSITION_INDEX] = unscaledYPosition;
    }

    /**
     * Returns the number of neighbours of a node in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to count
     * @return the number of neighbours of a node in the given direction.
     */
    public int getNeighbourCount(final int id, final SequenceDirection direction) {
        return direction.ternary(
                (nodeArrays[id].length
                        - nodeArrays[id][Node.NODE_OUTGOING_EDGES_INDEX] * Node.EDGE_DATA_SIZE
                        - (Node.NODE_OUTGOING_EDGES_INDEX + 1)
                ) / Node.EDGE_DATA_SIZE,
                nodeArrays[id][Node.NODE_OUTGOING_EDGES_INDEX]
        );
    }

    /**
     * Returns the {@link GraphIterator} for this {@link Graph} for iterating over its node.
     *
     * @return the {@link GraphIterator} for this {@link Graph} for iterating over its node
     */
    @EnsuresNonNull("iterator")
    public GraphIterator iterator() {
        if (iterator == null) {
            iterator = new GraphIterator(this);
        }
        return iterator;
    }

    /**
     * Returns a new {@link Fafosp} for invoking FAFOSP-related methods.
     * <p>
     * FAFOSP is the Felix Algorithm For Optimal Segment Positioning.
     *
     * @return a new {@link Fafosp}
     */
    Fafosp fafosp() {
        return new Fafosp(this);
    }

    /**
     * Getter for the {@code GfaFile} instance where the graph belongs to.
     *
     * @return the {@code GfaFile} instance where the graph belongs to.
     */
    public GfaFile getGfaFile() {
        return gfaFile;
    }
}
