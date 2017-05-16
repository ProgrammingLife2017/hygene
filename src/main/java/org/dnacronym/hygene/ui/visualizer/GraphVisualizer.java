package org.dnacronym.hygene.ui.visualizer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.SequenceDirection;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * <p>
 * When passing a {@link Graph}, it will draw it using JavaFX primitives. If the {@link Canvas} has not been set
 * all methods related to drawing will thrown an {@link IllegalStateException}.
 *
 * @see Canvas
 * @see GraphicsContext
 */
public final class GraphVisualizer {
    private static final double DEFAULT_NODE_HEIGHT = 20;
    private static final double DEFAULT_NODE_WIDTH = 0.001;
    private static final double DEFAULT_DASH_LENGTH = 10;

    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;

    private final ObjectProperty<Node> selectedNodeProperty;

    private final IntegerProperty centerNodeProperty;
    private final IntegerProperty rangeProperty;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;
    private final DoubleProperty nodeWidthProperty;
    private final DoubleProperty laneHeightProperty;

    private final BooleanProperty displayLaneBordersProperty;
    private final DoubleProperty borderDashLengthProperty;

    private @Nullable Graph graph;

    private @MonotonicNonNull Canvas canvas;
    private @MonotonicNonNull GraphicsContext graphicsContext;

    /**
     * Create a new {@link GraphVisualizer} instance.
     */
    public GraphVisualizer() {
        super();

        selectedNodeProperty = new SimpleObjectProperty<>();

        centerNodeProperty = new SimpleIntegerProperty();
        rangeProperty = new SimpleIntegerProperty();

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        nodeWidthProperty = new SimpleDoubleProperty(DEFAULT_NODE_WIDTH);
        laneHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);

        displayLaneBordersProperty = new SimpleBooleanProperty();
        borderDashLengthProperty = new SimpleDoubleProperty(DEFAULT_DASH_LENGTH);
    }


    /**
     * Draw edge on the {@link Canvas}.
     *
     * @param startHorizontal x position of the start of the line
     * @param startVertical   y position of the start of the line
     * @param endHorizontal   x position of the end of the line
     * @param endVertical     y position of the end of the line
     * @param color           color of the edge
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawEdge(final double startHorizontal, final double startVertical,
                          final double endHorizontal, final double endVertical, final Color color) {
        graphicsContext.setStroke(color);
        graphicsContext.strokeLine(
                startHorizontal * nodeWidthProperty.get(),
                (startVertical + 1.0 / 2.0) * laneHeightProperty.get(),
                endHorizontal * nodeWidthProperty.get(),
                (endVertical + 1.0 / 2.0) * laneHeightProperty.get()
        );
    }

    /**
     * Draw a node on the {@link Canvas}.
     *
     * @param startHorizontal  unscaled x position of the node
     * @param verticalPosition unscaled y position of the node
     * @param width            unscaled width of the node
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

    private void drawNode(final Graph graph, final int nodeId) {
        final int nodeX = graph.getUnscaledXPosition(nodeId);
        final int nodeY = graph.getUnscaledYPosition(nodeId);

        final Color color = graph.getColor(nodeId).getFXColor();
    }

    /**
     * Populate the graphs primitives with the given graph.
     * <p>
     * First clears the graph before drawing. If {@link Graph} is null, only clears the canvas.
     *
     * @param graph {@link Graph} to populate canvas with
     * @throws IllegalStateException if the {@link Canvas} has not been set
     */
    public void draw(final @Nullable Graph graph) {
        if (canvas == null || graphicsContext == null) {
            throw new IllegalStateException("Attempting to draw whilst canvas not set.");
        }

        clear();
        this.graph = graph;
        if (graph != null && canvas != null) {
            final int centerNode = centerNodeProperty.get();
            final int range = rangeProperty.get();

            final List<Integer> neighbours = new ArrayList<>();
            graph.visitNeighbours(centerNode, SequenceDirection.LEFT, neighbours::add);
            graph.visitNeighbours(centerNode, SequenceDirection.RIGHT, neighbours::add);

            for (Integer nodeId : neighbours) {

            }
        }
    }

    /**
     * Redraw the most recently set {@link Graph}. If this is null, canvas is only cleared.
     */
    public void redraw() {
        draw(this.graph);
    }

    /**
     * Draw the border between bands as {@link Color#BLACK}.
     *
     * @param laneCount  amount of bands onscreen
     * @param laneHeight height of each band
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawBandEdges(final int laneCount, final double laneHeight) {
        final Paint orginalStroke = graphicsContext.getStroke();
        final double originalLineWidth = graphicsContext.getLineWidth();

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(1);
        graphicsContext.setLineDashes(borderDashLengthProperty.get());

        for (int band = 1; band < laneCount; band++) {
            graphicsContext.strokeLine(
                    0,
                    band * laneHeight,
                    canvas.getWidth(),
                    band * laneHeight
            );
        }

        graphicsContext.setStroke(orginalStroke);
        graphicsContext.setLineWidth(originalLineWidth);
        graphicsContext.setLineDashes(0);
    }

    /**
     * Clear the canvas.
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    public void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Set {@link Canvas} which the {@link GraphVisualizer} can draw on.
     *
     * @param canvas canvas to be used to {@link GraphVisualizer}
     */
    public void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(event -> {
            final int[] positions = toNodeCoordinates(event.getX(), event.getY());
            final int nodeXPos = positions[0];
            final int nodeLane = positions[1];
        });
    }

    /**
     * Converts onscreen coordinates to coordinates which can be used to find the correct node.
     * <p>
     * The x coordinate depends on the widthproperty. The y property denotes in which lane the click is.
     *
     * @param xPos x position onscreen
     * @param yPos y position onscreen
     * @return x and y position in a double array of size 2 which correspond with x and y position of {@link
     * Node}.
     */
    private int[] toNodeCoordinates(final double xPos, final double yPos) {
        return new int[]{
                (int) Math.round(xPos / nodeWidthProperty.get()),
                (int) Math.floor(yPos / laneHeightProperty.get())
        };
    }

    /**
     * The property of the selected node. This node is updated every time the user clicks on the canvas.
     *
     * @return Selected {@link Node} by the user. Can be null.
     */
    public ObjectProperty<Node> getSelectedNodeProperty() {
        return selectedNodeProperty;
    }

    /**
     * Property which determines the current center node.
     *
     * @return property which decides the current center node
     */
    public IntegerProperty getCenterNodeProperty() {
        return centerNodeProperty;
    }

    /**
     * The property which determines the range to draw around the center node.
     * <p>
     * The property determines how far should be iterated in both directions.
     *
     * @return property which decides the range to draw round the center node
     */
    public IntegerProperty getRangeProperty() {
        return rangeProperty;
    }

    /**
     * The property of onscreen edge colors.
     *
     * @return property which decides the color of edges.
     */
    public ObjectProperty<Color> getEdgeColorProperty() {
        return edgeColorProperty;
    }

    /**
     * The property of onscreen node heights.
     *
     * @return property which decides the height of nodes.
     */
    public DoubleProperty getNodeHeightProperty() {
        return nodeHeightProperty;
    }

    /**
     * The property of node widths.
     * <p>
     * Node width determines how wide a single width unit in the FAFOSP algorithm.
     *
     * @return property which decides the width of nodes.
     */
    public DoubleProperty getNodeWidthProperty() {
        return nodeWidthProperty;
    }

    /**
     * The property which determines whether to display the border between bands as black bands.
     *
     * @return property which decides whether to display the border between bands.
     */
    public BooleanProperty getDisplayBordersProperty() {
        return displayLaneBordersProperty;
    }

    /**
     * The property which determines how long the onscreen dashes should be.
     *
     * @return property which determines the dash length.
     */
    public DoubleProperty getBorderDashLengthProperty() {
        return borderDashLengthProperty;
    }
}
