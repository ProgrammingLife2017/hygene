package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * <p>
 * When passing a {@link SequenceGraph}, it will draw it using JavaFX primitives. If the {@link Canvas} has not been set
 * all methods related to drawing will thrown an {@link IllegalStateException}.
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

    private final ObjectProperty<SequenceNode> selectedNodeProperty;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;
    private final DoubleProperty nodeWidthProperty;
    private final DoubleProperty laneHeightProperty;

    private final BooleanProperty displayLaneBordersProperty;

    private @Nullable SequenceGraph sequenceGraph;

    private @MonotonicNonNull Canvas canvas;
    private @MonotonicNonNull GraphicsContext graphicsContext;

    /**
     * Create a new {@link GraphVisualizer} instance.
     */
    public GraphVisualizer() {
        super();

        selectedNodeProperty = new SimpleObjectProperty<>();

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        nodeWidthProperty = new SimpleDoubleProperty(DEFAULT_NODE_WIDTH);
        laneHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);

        displayLaneBordersProperty = new SimpleBooleanProperty();
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
        return new int[]{
                (int) Math.round(xPos / nodeWidthProperty.get()),
                (int) Math.floor(yPos / laneHeightProperty.get())
        };
    }

    /**
     * Draw edge on the {@link Canvas}.
     *
     * @param startHorizontal x position of the start of the line
     * @param startVertical   y position of the start of the line
     * @param endHorizontal   x position of the end of the line
     * @param endVertical     y position of the end of the line
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
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
                (double) (neighbour.getHorizontalRightEnd() - neighbour.getLength()),
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
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawEdges(final SequenceNode sequenceNode, final Color color) {
        graphicsContext.setStroke(color);
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
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
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
                (double) (node.getHorizontalRightEnd() - node.getLength()),
                node.getVerticalPosition(),
                node.getLength(),
                color
        );
    }

    /**
     * Draw the border between bands as {@link Color#BLACK}.
     *
     * @param laneCount  amount of bands onscreen
     * @param laneHeight height of each band
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawBandEdges(final int laneCount, final double laneHeight) {
        for (int band = 1; band < laneCount; band++) {
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.strokeLine(
                    0,
                    band * laneHeight,
                    canvas.getWidth(),
                    band * laneHeight
            );
        }
    }

    /**
     * Clear the canvas.
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
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
     * The property which determines whether to display the border between bands as black bands.
     *
     * @return property which decides whether to display the border between bands.
     */
    public final BooleanProperty getDisplayBordersProperty() {
        return displayLaneBordersProperty;
    }

    /**
     * Set {@link Canvas} which the {@link GraphVisualizer} can draw on.
     *
     * @param canvas canvas to be used to {@link GraphVisualizer}
     */
    public final void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(event -> {
            final int[] positions = toSequenceNodeCoordinates(event.getX(), event.getY());
            final int nodeXPos = positions[0];
            final int nodeLane = positions[1];

            if (sequenceGraph != null) {
                final SequenceNode node = sequenceGraph.getNode(nodeXPos, nodeLane);
                if (selectedNodeProperty != null && node != null) {
                    selectedNodeProperty.set(node);
                }
            }
        });
    }

    /**
     * Bind the height of the canvas to a given height property.
     *
     * @param heightProperty height canvas should be.
     */
    public final void setCanvasHeight(final ReadOnlyDoubleProperty heightProperty) {
        if (canvas != null) {
            canvas.heightProperty().bind(heightProperty);
        }
    }

    /**
     * Redraw the most recently set {@link SequenceGraph}. If this is null, canvas is only cleared.
     */
    public final void redraw() {
        draw(this.sequenceGraph);
    }

    /**
     * Populate the graphs primitives with the given sequence graph.
     * <p>
     * First clears the graph before drawing. If {@link SequenceGraph} is null, only clears the canvas.
     *
     * @param sequenceGraph {@link SequenceGraph} to populate canvas with.
     * @throws IllegalStateException if the {@link Canvas} has not been set.
     */
    public final void draw(final @Nullable SequenceGraph sequenceGraph) {
        if (canvas == null || graphicsContext == null) {
            throw new IllegalStateException("Attempting to draw whilst canvas not set.");
        }

        clear();
        this.sequenceGraph = sequenceGraph;
        if (sequenceGraph != null && canvas != null) {
            final double canvasWidth = sequenceGraph.getSinkNode().getHorizontalRightEnd() * nodeWidthProperty.get();
            canvas.setWidth(canvasWidth);

            // TODO get actual laneCount from FAFOSP (as soon as fixed)
            final int laneCount = 12;
            laneHeightProperty.set(canvas.getHeight() / laneCount);

            sequenceGraph.iterator(n -> !n.isVisited()).forEachRemaining(n -> n.setVisited(false));

            sequenceGraph.iterator(SequenceNode::isVisited).forEachRemaining(node -> {
                drawNode(node, DEFAULT_NODE_COLOR);
                drawEdges(node, edgeColorProperty.get());

                node.setVisited(true);
            });

            if (displayLaneBordersProperty.get()) {
                drawBandEdges(laneCount, laneHeightProperty.get());
            }
        }
    }
}
