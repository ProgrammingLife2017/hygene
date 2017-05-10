package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
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
public class GraphPane extends Pane {
    private static final double DEFAULT_NODE_HEIGHT = 100;

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private final ObjectProperty<SequenceNode> selectedNodeProperty;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;

    private SequenceGraph sequenceGraph;

    private double laneHeight;

    /**
     * Create a new {@link GraphPane} instance.
     */
    @SuppressWarnings("nullness") // Superclass width and height has already been instantiated, so can't be null.
    public GraphPane() {
        super();

        canvas = new Canvas();
        graphicsContext = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        edgeColorProperty = new SimpleObjectProperty<>(Color.BLACK);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);

        selectedNodeProperty = new SimpleObjectProperty<>();
        canvas.setOnMouseClicked(event -> {
            if (sequenceGraph != null) {
                final double xPos = event.getSceneX();
                final double yPos = event.getSceneY();

                final int[] positions = toSequenceNodeCoordinates(xPos, yPos);
                final int nodeXPos = positions[0];
                final int nodeLane = positions[1];
            }
        });

        this.getChildren().add(canvas);
    }


    /**
     * Draw line on the {@link Canvas}.
     *
     * @param startX x position of the start of the line
     * @param startY y position of the start of the line
     * @param endX   x position of the end of the line
     * @param endY   y position of the end of the line
     */
    private void drawEdge(final double startX, final double startY,
                          final double endX, final double endY) {
        graphicsContext.strokeLine(startX, startY, endX, endY);
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
        // TODO iterate over neighbours, draw edges and call method on those neighbours
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
     * Draw a rectangle on the {@link Canvas}.
     *
     * @param startX x position of the upper left corner of rectangle
     * @param startY y position of the upper left corner of rectangle
     * @param width  width of the rectangle
     * @param height height of the rectangle
     * @param color  color of the rectangle
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
     * @param sequenceNode the node which should be drawn onscreen
     * @param nodeHeight   how tall each node should be. If this is more than laneHeight, nodes will overlap
     * @param laneHeight   the height of a step. A single step represents a band onscreen
     * @see SequenceNode#getRightNeighbours()
     */
    private void drawNodes(final SequenceNode sequenceNode, final double nodeHeight, final double laneHeight) {
        // TODO draw node based on x, lane, width and its color. Iterate over right neighbours and call method again.
    }

    /**
     * Visualise a {@link SequenceNode} in the {@link Canvas}.
     * <p>
     * A {@link SequenceNode} is drawn in the center of each band.
     *
     * @param sequenceNode sequenceNode to draw.
     * @param stepHeight   denotes the height of each of the onscreen bands in which nodes reside.
     */
    private void draw(final SequenceNode sequenceNode, final double stepHeight) {
        drawNodes(sequenceNode, nodeHeightProperty.get(), stepHeight);
        drawEdges(sequenceNode, edgeColorProperty.get());
    }

    /**
     * Converts onscreen coordinates to coordinates which can be used to find the correct sequenceNode.
     * <p>
     * The x coordinate depends on the widthproperty. The y property denotes in which lane the click is.
     *
     * @param xPos x position onscreen
     * @param yPos y position onscreen
     * @return x and y position in a double array of size 2 which correspond with x and y position of
     * {@link SequenceNode}.
     */
    private int[] toSequenceNodeCoordinates(final double xPos, final double yPos) {
        // TODO write, and update javadoc to use widthProperty

        final int lane = (int) Math.floor(yPos / laneHeight) + 1;

        return new int[]{0, lane};
    }

    /**
     * Sets the {@link #selectedNodeProperty} to the found node in the {@link SequenceGraph}.
     * <p>
     * The nodeX should not be onscreen position, but should have been converted to a {@link SequenceGraph} x. The band
     * is the vertical position of the node, and should denote in which band the node should be.
     *
     * @param nodeX x position of the node.
     * @param band  band the node is in.
     * @see #getSelectedNodeProperty()
     * @see SequenceNode#getHorizontalRightEnd()
     * @see SequenceNode#getVerticalPosition()
     */
    private void setCurrentNode(final int nodeX, final int band) {
        for (SequenceNode node : sequenceGraph) {
            if (node.getVerticalPosition() == band) {
                final int nodeRightEnd = node.getHorizontalRightEnd();
                final int nodeLeftEnd = 0;
                
                if (nodeLeftEnd <= nodeX && nodeX < nodeRightEnd) {
                    selectedNodeProperty.set(node);
                    break;
                }
            }
        }
    }

    /**
     * Clear the canvas.
     */
    public final void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * The property of the selected node. This node is updated every time the user clicks on the canvas.
     *
     * @return Selected {@link SequenceNode} by the user. Can be null.
     */
    public final ObjectProperty<SequenceNode> getSelectedNodeProperty() {
        return selectedNodeProperty;
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
     * Populate the graphs primitives with the given sequence graph.
     * <p>
     * First clears the graph before drawing. If {@link SequenceGraph} is null, only clears the canvas.
     *
     * @param sequenceGraph {@link SequenceGraph} to populate canvas with.
     */
    public final void draw(final @Nullable SequenceGraph sequenceGraph) {
        this.sequenceGraph = sequenceGraph;
        clear();

        if (sequenceGraph != null) {
            // TODO retrieve bandcount from sequenceGraph
            final double bandCount = 1;
            laneHeight = canvas.getHeight() / bandCount;

            final SequenceNode sink = sequenceGraph.getSinkNode();
            draw(sink, laneHeight);
        }
    }
}
