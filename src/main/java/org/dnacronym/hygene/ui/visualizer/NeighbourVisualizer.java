package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.Node;


/**
 * Create a neighbour visualizer.
 * <p>
 * A neighbour visualizer is a simple visualisation tool which draws a circle in the middle of the canvas,
 * and lines on the left and right side representing the neighbours.
 */
public class NeighbourVisualizer {
    private static final double NODE_WIDTH_PORTION_OF_CANVAS = 0.25;

    private final ObjectProperty<Node> nodeProperty;
    private final ObjectProperty<Color> edgeColorProperty;

    private @MonotonicNonNull Canvas canvas;
    private @MonotonicNonNull GraphicsContext graphicsContext;


    /**
     * Create a neighbour visualiser which gives a visualisation of the neighbours of a node.
     *
     * @param edgeColorProperty property which determines the color of edges to neighbours
     * @param nodeProperty      property which determines what node should actually be visualised
     */
    @SuppressWarnings("nullness")
    public NeighbourVisualizer(final ObjectProperty<Color> edgeColorProperty,
                               final ObjectProperty<Node> nodeProperty) {
        this.edgeColorProperty = new SimpleObjectProperty<>();
        this.nodeProperty = new SimpleObjectProperty<>();

        this.edgeColorProperty.bind(edgeColorProperty);
        this.nodeProperty.bind(nodeProperty);

        this.nodeProperty.addListener((observable, oldNode, newNode) -> draw());
    }


    /**
     * Set the canvas on which the node shall be drawn.
     *
     * @param canvas canvas on which the node shall be drawn
     */
    public final void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();
    }

    /**
     * Clear the canvas.
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draw the node and outgoing edges.
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void draw() {
        final Node node = nodeProperty.get();
        if (node != null) {
            final int leftNeighbours = node.getNumberOfIncomingEdges();
            final int rightNeighbours = node.getNumberOfOutgoingEdges();
            final Color nodeColor = Color.BLACK; // node.getColor().getFxColor();

            graphicsContext.setFill(nodeColor);
            graphicsContext.fillOval(
                    canvas.getWidth() * NODE_WIDTH_PORTION_OF_CANVAS,
                    canvas.getWidth() * NODE_WIDTH_PORTION_OF_CANVAS,
                    canvas.getWidth() / 2,
                    canvas.getWidth() / 2
            );

            graphicsContext.setFill(edgeColorProperty.get());

            for (int left = 0; left < leftNeighbours; left++) {
                graphicsContext.strokeLine(
                        0,
                        left * (canvas.getHeight() / 2 / leftNeighbours),
                        canvas.getWidth() / 2,
                        left * (canvas.getHeight() / leftNeighbours)
                );
            }

            for (int right = 0; right < rightNeighbours; right++) {
                graphicsContext.strokeLine(
                        canvas.getWidth() / 2,
                        right * (canvas.getHeight() / 2 / rightNeighbours),
                        0,
                        right * (canvas.getHeight() / rightNeighbours)
                );
            }
        }
    }
}
