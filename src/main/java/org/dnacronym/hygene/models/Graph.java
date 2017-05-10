package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.dnacronym.hygene.parser.NewGfaFile;


/**
 * Class wraps around the graph data represented as a nested array and provides utility methods.
 * <p>
 * Node array format:
 * [[nodeLineNumber, nodeColor, outgoingEdges, xPosition, yPosition, edge1, edge1LineNumber...]]
 */
public final class Graph {
    private final int[][] nodeArrays;
    private final NewGfaFile gfaFile;


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
    public Graph(final int[][] nodeArrays, final NewGfaFile gfaFile) {
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
     * Getter for the {@code GfaFile} instance where the graph belongs to.
     *
     * @return the {@code GfaFile} instance where the graph belongs to.
     */
    public NewGfaFile getGfaFile() {
        return gfaFile;
    }
}
