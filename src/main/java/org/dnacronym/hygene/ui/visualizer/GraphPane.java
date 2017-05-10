package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * <p>
 * When passing a {@link SequenceGraph}, it will draw it using JavaFX primitives.
 *
 * @see Canvas
 * @see GraphicsContext
 */
public class GraphPane extends Pane {
    private static final double DEFAULT_NODE_HEIGHT = 100;

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;


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
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);

        this.getChildren().add(canvas);
    }


    /**
     * Draw line on the {@link Canvas}.
     *
     * @param startX x position of the start of the line.
     * @param startY y position of the start of the line.
     * @param endX   x position of the end of the line.
     * @param endY   y position of the end of the line.
     */
    private void drawEdge(final double startX, final double startY,
                          final double endX, final double endY) {
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    /**
     * draws all onscreen edges between the current {@link SequenceNode} and it's right neighbours.
     * <p>
     * Afterwards, calls itself on each of the right neighbours of the current node.
     *
     * @param sequenceNode the node who's edges should be drawn on the {@link Canvas}.
     * @see SequenceNode#getRightNeighbours()
     */
    private void drawEdges(final SequenceNode sequenceNode) {
        // TODO iterate over neighbours, draw edges and call method on those neighbours
    }

    /**
     * Sets the fill of the {@link GraphicsContext} before proceeding to draw all onscreen edges.
     *
     * @param sequenceNode the node representing the source of the graph.
     * @param color        the color with which all edges should be drawn.
     * @see #drawEdges(SequenceNode)
     */
    private void drawEdges(final SequenceNode sequenceNode, final Color color) {
        graphicsContext.setFill(color);
        drawEdges(sequenceNode);
    }

    /**
     * Draw a rectangle on the {@link Canvas}.
     *
     * @param startX x position of the upper left corner of rectangle.
     * @param startY y position of the upper left corner of rectangle.
     * @param width  width of the rectangle.
     * @param height height of the rectangle.
     * @param color  color of the rectangle.
     */
    private void drawNode(final double startX, final double startY,
                          final double width, final double height, final Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(startX, startY, startX + width, startY - height);
    }

    /**
     * Draw the given {@link SequenceNode} onscreen.
     * <p>
     * Color depends on the set color of he {@link SequenceNode}.
     * <p>
     * Afterwards, proceeds to draw all right neighbours of the given {@link SequenceNode}.
     *
     * @param sequenceNode the node which should be drawn onscreen.
     * @param laneHeight   the heigh of a step. A single step represents a band onscreen.
     * @see SequenceNode#getRightNeighbours()
     */
    private void drawNodes(final SequenceNode sequenceNode, final double laneHeight) {
        // TODO draw node based on x, lane, width and its color. Iterate over right neighbours and call method again.
    }

    /**
     * Clear the canvas.
     */
    private void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Visualise a {@link SequenceNode} in the {@link Canvas}.
     * <p>
     * A {@link SequenceNode} is drawn in the center of each band.
     *
     * @param sequenceNode sequenceNode to visualise.
     * @param stepHeight   denotes the height of each of the onscreen bands in which nodes reside.
     */
    private void visualise(final SequenceNode sequenceNode, final double stepHeight) {
        drawNodes(sequenceNode, stepHeight);
        drawEdges(sequenceNode, edgeColorProperty.get());
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
    public final void visualise(final SequenceGraph sequenceGraph) {
        clear();

        // TODO retrieve bandcount from sequenceGraph
        final double bandCount = 1;
        final double laneHeight = canvas.getHeight() / bandCount;

        final SequenceNode sink = sequenceGraph.getSinkNode();
        visualise(sink, laneHeight);
    }
}
