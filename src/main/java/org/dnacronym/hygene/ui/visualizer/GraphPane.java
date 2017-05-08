package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * When passing a {@link SequenceGraph}, it will draw it using JavaFX primitives.
 *
 * @see Canvas
 * @see GraphicsContext
 */
public class GraphPane extends Pane {
    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private final ObjectProperty<Color> edgeColorProperty;

    /**
     * Create a new {@link GraphPane} instance.
     */
    @SuppressWarnings("nullness")
    public GraphPane() {
        super();

        canvas = new Canvas();
        graphicsContext = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        edgeColorProperty = new SimpleObjectProperty<>(Color.BLACK);

        this.getChildren().add(canvas);
    }


    /**
     * Draw line onscreen.
     *
     * @param startX x position of the start of the line.
     * @param startY y position of the start of the line.
     * @param endX   x position of the end of the line.
     * @param endY   y position of the end of the line.
     * @param color  color of the line.
     */
    private void drawLine(final double startX, final double startY,
                                final double endX, final double endY, final Color color) {
        graphicsContext.setFill(color);
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    /**
     * Draw a rectangle onscreen. The width and height should be given.
     *
     * @param startX x position of the leftmost position of the rectangle.
     * @param startY y position of the bottom of the rectangle.
     * @param width  width of the rectangle.
     * @param height height of the rectangle.
     * @param color  color of the rectangle.
     */
    private void drawRectangle(final double startX, final double startY,
                                     final double width, final double height, final Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(startX, startY, startX + width, startY - height);
    }

    /**
     * Clear the canvas.
     */
    private void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Generate a color based on the sequence.
     *
     * @param sequence sequence which to base the color of the node off.
     * @return {@link Color} based on the given sequence.
     */
    private Color generateColor(final String sequence) {
        // TODO generate color based on current mode and sequence
        return Color.GRAY;
    }

    /**
     * Visualise a {@link SequenceNode} in the {@link Canvas}.
     *
     * @param sequenceNode sequenceNode to visualise.
     * @param stepHeight   denotes the height of each of the onscreen bands in which nodes reside.
     */
    private void visualise(final SequenceNode sequenceNode, final double stepHeight) {
        // TODO call drawRectangle
        final Color nodeColor = generateColor(sequenceNode.getSequence());
        drawRectangle(0, 0, 0, 0, nodeColor);

        for (SequenceNode rightNeighbour : sequenceNode.getRightNeighbours()) {
            // TODO draw line from rightmost point of node to leftmost point of next node (neighbour)
            final Color edgeColor = edgeColorProperty.get();
            drawLine(0, 0, 0, 0, edgeColor);

            visualise(sequenceNode, stepHeight);
        }
    }

    /**
     * The property of edge colors. This color determines the colors of edges.
     *
     * @return property which decides the color of edges.
     */
    public final ObjectProperty<Color> getEdgeColorProperty() {
        return edgeColorProperty;
    }

    /**
     * Populate the graphs primitives with the given sequence graph.
     * First clears the graph before drawing.
     *
     * @param sequenceGraph {@link SequenceGraph} to populate canvas with.
     */
    public final void visualise(final @NonNull SequenceGraph sequenceGraph) {
        clear();

        // TODO retrive bandcount from sequenceGraph
        final double bandCount = 1;
        final double stepHeight = canvas.getHeight() / bandCount;

        final SequenceNode sink = sequenceGraph.getSinkNode();
        visualise(sink, stepHeight);
    }
}
