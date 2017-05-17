package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.dnacronym.hygene.parser.GfaFile;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.NewGfaFile;


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
     * Getter for the unscaled y position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled y position.
     */
    public int getUnscaledYPosition(final int id) {
        return nodeArrays[id][Node.UNSCALED_Y_POSITION_INDEX];
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
     * Returns a new {@link GraphIterator} that can be used to iterate over this {@link Graph} or parts of it.
     *
     * @return a new {@link GraphIterator} that can be used to iterate over this {@link Graph} or parts of it
     */
    @EnsuresNonNull("iterator")
    public GraphIterator iterator() {
        if (iterator == null) {
            iterator = new GraphIterator(this);
        }
        return iterator;
    }


    /**
     * Finds the first node whose vertical and horizontal positions match.
     * <p>
     * This gives the {@link Node} where the node's x is in bounds and the band is equal to the given band, or
     * {@code null} if no such band is found
     *
     * @param centerNodeId      node id to search around
     * @param range             range which specifies how far to search for
     * @param unscaledXPosition x position of the node
     * @param unscaledYPosition band the node is in
     * @return node              id of found node, -1 if not found
     */
    public int getNode(final int centerNodeId, final int range,
                       final int unscaledXPosition, final int unscaledYPosition) {
        final int[] foundNode = {-1};
        visitNeighbours(centerNodeId, SequenceDirection.LEFT, nodeId -> {
            if (unscaledYPosition == getUnscaledYPosition(nodeId)
                    && unscaledXPosition > getUnscaledXPosition(nodeId)
                    && unscaledXPosition < getUnscaledXPosition(nodeId) + getSequenceLength(nodeId)) {
                foundNode[0] = nodeId;
            }
        });

        return foundNode[0];
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
