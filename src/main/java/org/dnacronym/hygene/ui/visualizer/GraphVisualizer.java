package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.Nullable;
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
public class GraphVisualizer {
    private static final double DEFAULT_NODE_HEIGHT = 20;
    private static final double DEFAULT_NODE_WIDTH = 0.001;
    private static final double DEFAULT_EDGE_WIDTH = 2;

    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;
    private static final Color DEFAULT_NODE_COLOR = Color.BLUE;

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;
    private final DoubleProperty nodeWidthProperty;
    private final DoubleProperty laneHeightProperty;


    /**
     * Create a new {@link GraphVisualizer} instance.
     *
     * @param canvas the canvas to draw to
     */
    @SuppressWarnings("nullness") // Superclass width and height has already been instantiated, so can't be null.
    public GraphVisualizer(final Canvas canvas) {
        super();

        this.canvas = canvas;
        graphicsContext = canvas.getGraphicsContext2D();

        edgeColorProperty = new SimpleObjectProperty<>(Color.BLACK);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        nodeWidthProperty = new SimpleDoubleProperty(DEFAULT_NODE_WIDTH);
        laneHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
    }


    /**
     * Draw edge on the {@link Canvas}.
     *
     * @param startHorizontal x position of the start of the line
     * @param startVertical   y position of the start of the line
     * @param endHorizontal   x position of the end of the line
     * @param endVertical     y position of the end of the line
     */
    private void drawEdge(final double startHorizontal, final double startVertical,
                          final double endHorizontal, final double endVertical) {
        graphicsContext.setLineWidth(DEFAULT_EDGE_WIDTH);
        graphicsContext.strokeLine(
                startHorizontal * nodeWidthProperty.get(),
                (startVertical + 1.0 / 2.0) * laneHeightProperty.get(),
                endHorizontal * nodeWidthProperty.get(),
                (endVertical + 1.0 / 2.0) * laneHeightProperty.get()
        );
    }

    /**
     * Draws all onscreen edges between the current {@link SequenceNode} and it's right neighbours.
     * <p>
     * Afterwards, calls itself on each of the right neighbours of the current node.
     *
     * @param sequenceNode the node who's edges should be drawn on the {@link Canvas}
     * @see SequenceNode#getRightNeighbours()
     */
    private void drawEdges(final SequenceNode sequenceNode) {
        sequenceNode.getRightNeighbours().forEach(neighbour -> drawEdge(
                sequenceNode.getHorizontalRightEnd(),
                sequenceNode.getVerticalPosition(),
                neighbour.getHorizontalRightEnd() - neighbour.getSequence().length(),
                neighbour.getVerticalPosition()
        ));
    }

    /**
     * Sets the fill of the {@link GraphicsContext} before proceeding to draw all onscreen edges.
     *
     * @param sequenceNode the node representing the source of the graph
     * @param color        the color with which all edges should be drawn
     * @see #drawEdges(SequenceNode)
     */
    private void drawEdges(final SequenceNode sequenceNode, final Color color) {
        graphicsContext.setFill(color);
        drawEdges(sequenceNode);
    }

    /**
     * Draw a node on the {@link Canvas}.
     *
     * @param startHorizontal  x position of the node
     * @param verticalPosition y position of the node
     * @param width            width of the node
     * @param color            color of the node
     */
    private void drawNode(final double startHorizontal, final double verticalPosition,
                          final double width, final Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(
                startHorizontal * nodeWidthProperty.get(),
                (verticalPosition + 1.0 / 2.0) * laneHeightProperty.get() - 1.0 / 2.0 * nodeHeightProperty.get(),
                width * nodeWidthProperty.get(),
                nodeHeightProperty.get()
        );
    }

    /**
     * Draws the given node to the screen.
     *
     * @param node  the node to draw
     * @param color the color to draw with
     */
    private void drawNode(final SequenceNode node, final Color color) {
        drawNode(
                (double) node.getHorizontalRightEnd() - node.getSequence().length(),
                node.getVerticalPosition(),
                node.getSequence().length(),
                color
        );
    }

    /**
     * Clear the canvas.
     */
    public final void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * The property of onscreen edge colors.
     *
     * @return property which decides the color of edges.
     */
    public final ObjectProperty<Color> getEdgeColorProperty() {
        return edgeColorProperty;
    }

    /**
     * The property of onscreen node heights.
     *
     * @return property which decides the height of nodes.
     */
    public final DoubleProperty getNodeHeightProperty() {
        return nodeHeightProperty;
    }

    /**
     * The property of node widths.
     * <p>
     * Node width determines how wide a single width unit in the FAFOSP algorithm.
     *
     * @return property which decides the width of nodes.
     */
    public final DoubleProperty getNodeWidthProperty() {
        return nodeWidthProperty;
    }

    /**
     * Populate the graphs primitives with the given sequence graph.
     * <p>
     * First clears the graph before drawing. If {@link SequenceGraph} is null, only clears the canvas.
     *
     * @param sequenceGraph {@link SequenceGraph} to populate canvas with.
     */
    public final void draw(final @Nullable SequenceGraph sequenceGraph) {
        if (sequenceGraph != null) {
            clear();

            final double canvasWidth = sequenceGraph.getSinkNode().getHorizontalRightEnd() * nodeWidthProperty.get();
            canvas.setWidth(canvasWidth);

            // TODO get actual laneCount from FAFOSP (as soon as fixed)
            final double laneCount = 12;
            laneHeightProperty.set(canvas.getHeight() / laneCount);

            sequenceGraph.iterator(SequenceNode::isVisited).forEachRemaining(node -> {
                drawNode(node, DEFAULT_NODE_COLOR);
                drawEdges(node, DEFAULT_EDGE_COLOR);

                node.setVisited(true);
            });
        }
    }
}
