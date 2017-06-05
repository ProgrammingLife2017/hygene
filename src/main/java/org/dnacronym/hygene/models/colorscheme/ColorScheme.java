package org.dnacronym.hygene.models.colorscheme;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * Scheme for deciding color of node.
 */
public final class ColorScheme {
    private NodeColor nodeColor = new NodeColorSequenceLength();


    /**
     * Sets the current color modes, affecting the color fill of nodes.
     *
     * @param colorMode the mode of coloring nodes
     */
    public void setNodeColor(final ColorMode colorMode) {
        this.nodeColor = colorMode.nodeColor;
    }

    /**
     * Gets the fill {@link Color} of a {@link Node}.
     *
     * @param node the {@link Node} to fill
     * @return the {@link Color} of a node
     */
    public Color getColor(final Node node) {
        return nodeColor.calculateColor(node);
    }

    /**
     * The {@link Color} mode of a {@link Node}.
     */
    @SuppressWarnings("PMD.SingularField") // This is the most logical place to store this instance.
    public enum ColorMode {
        /**
         * Make the color dependent on the sequence length, determined by {@link NodeColorSequenceLength}.
         */
        SEQUENCE_LENGTH(new NodeColorSequenceLength()),
        /**
         * Make the color dependent on the amount of incoming edges, determined by {@link NodeColorIncomingEdges}.
         */
        INCOMING_EDGES(new NodeColorIncomingEdges()),
        /**
         * Make the color dependent on the amount of outgoing edges, determined by {@link NodeColorOutgoingEdges}.
         */
        OUTGOING_EDGES(new NodeColorOutgoingEdges());

        private NodeColor nodeColor;

        ColorMode(final NodeColor nodeColor) {
            this.nodeColor = nodeColor;
        }
    }
}
