package org.dnacronym.hygene.ui.node;

import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.GfaNode;


/**
 * Create a neighbour visualizer.
 * <p>
 * A neighbour visualizer is a simple visualisation tool which draws a circle in the middle of the canvas,
 * and lines on the left and right side representing the neighbours.
 */
final class NeighbourVisualizer {
    private static final double NODE_WIDTH_PORTION_OF_CANVAS = 0.25;
    private static final double ARC_PORTION_OF_CANVAS = 0.1;

    private Canvas canvas;
    private GraphicsContext graphicsContext;


    /**
     * Create a neighbour visualiser which gives a visualisation of the neighbours of a node.
     *
     * @param edgeColorProperty property which determines the color of edges to neighbours
     * @param nodeProperty      property which determines what node should actually be visualised
     */
    NeighbourVisualizer(final ObjectProperty<Color> edgeColorProperty, final ObjectProperty<GfaNode> nodeProperty) {
        edgeColorProperty.addListener((observable, oldColor, newColor) -> draw(nodeProperty.get(), newColor));
        nodeProperty.addListener((observable, oldNode, newNode) -> draw(newNode, edgeColorProperty.get()));
    }


    /**
     * Set the canvas on which the node shall be drawn.
     *
     * @param canvas canvas on which the node shall be drawn
     */
    void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();
    }

    /**
     * Clear the canvas.
     */
    private void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draw the node and outgoing edges.
     *
     * @param node      the {@link GfaNode} to draw
     * @param edgeColor the {@link Color} of edges
     */
    private void draw(final GfaNode node, final Color edgeColor) {
        clear();

        if (node == null) {
            return;
        }

        final int leftNeighbours = node.getIncomingEdges().size();
        final int rightNeighbours = node.getOutgoingEdges().size();
        final Color nodeColor = node.getColor();

        final double topLeftX = canvas.getWidth() / 2 - canvas.getWidth() * NODE_WIDTH_PORTION_OF_CANVAS / 2;
        final double topLeftY = canvas.getHeight() / 2 - canvas.getHeight() * NODE_WIDTH_PORTION_OF_CANVAS / 2;
        final double width = NODE_WIDTH_PORTION_OF_CANVAS * canvas.getWidth();
        final double height = NODE_WIDTH_PORTION_OF_CANVAS * canvas.getHeight();

        graphicsContext.setFill(edgeColor);

        for (int left = 0; left < leftNeighbours; left++) {
            graphicsContext.strokeLine(
                    0,
                    topLeftY + (height / (leftNeighbours + 1)) * (left + 1),
                    canvas.getWidth() / 2,
                    topLeftY + (height / (leftNeighbours + 1)) * (left + 1)
            );
        }

        for (int right = 0; right < rightNeighbours; right++) {
            graphicsContext.strokeLine(
                    canvas.getWidth() / 2,
                    topLeftY + (height / (rightNeighbours + 1)) * (right + 1),
                    canvas.getWidth(),
                    topLeftY + (height / (rightNeighbours + 1)) * (right + 1)
            );
        }

        graphicsContext.setFill(nodeColor);
        graphicsContext.fillRoundRect(
                topLeftX,
                topLeftY,
                width,
                height,
                ARC_PORTION_OF_CANVAS * canvas.getWidth(),
                ARC_PORTION_OF_CANVAS * canvas.getHeight());
    }
}
