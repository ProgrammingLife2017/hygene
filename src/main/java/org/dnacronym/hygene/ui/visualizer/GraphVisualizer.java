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
    private static final double DEFAULT_DASH_LENGTH = 10;
    /**
     * Range used when new graph is set, unless graph contains too few nodes.
     */
    private static final double DEFAULT_RANGE = 100;

    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;

    private final ObjectProperty<Node> selectedNodeProperty;

    private final IntegerProperty centerNodeProperty;
    private final IntegerProperty rangeProperty;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;

    private final BooleanProperty displayLaneBordersProperty;
    private final DoubleProperty borderDashLengthProperty;

    private double laneHeight;
    private int minX, maxX;

    private @Nullable Graph graph;

    private @MonotonicNonNull Canvas canvas;
    private @MonotonicNonNull GraphicsContext graphicsContext;


    /**
     * Create a new {@link GraphVisualizer} instance.
     */
    @SuppressWarnings("nullness") // For passing redraw method to listeners whilst object uninitialized
    public GraphVisualizer() {
        selectedNodeProperty = new SimpleObjectProperty<>();

        centerNodeProperty = new SimpleIntegerProperty(0);
        rangeProperty = new SimpleIntegerProperty(0);
        centerNodeProperty.addListener((observable, oldValue, newValue) -> redraw());
        rangeProperty.addListener((observable, oldValue, newValue) -> redraw());

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        edgeColorProperty.addListener((observable, oldValue, newValue) -> redraw());
        nodeHeightProperty.addListener((observable, oldValue, newValue) -> redraw());

        laneHeight = DEFAULT_NODE_HEIGHT;

        displayLaneBordersProperty = new SimpleBooleanProperty();
        borderDashLengthProperty = new SimpleDoubleProperty(DEFAULT_DASH_LENGTH);
        displayLaneBordersProperty.addListener((observable, oldValue, newValue) -> redraw());
        borderDashLengthProperty.addListener((observable, oldValue, newValue) -> redraw());
    }


    /**
     * Draw a node on the canvas. The minimum and maximum x positions are assumed to be unscaled, directly from the
     * FAFOSP algorithm.
     *
     * @param graph  graph which contains all the nodes and their information
     * @param nodeId id of node to draw
     * @param minX   unscaled minimum x position
     * @param maxX   unscaled maximum x position
     * @param lanes  amount of lines in the current visualisation
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawNode(final Graph graph, final int nodeId, final double minX, final double maxX, final int lanes) {
        final double diameter = maxX - minX;
        final int sequenceLength = 10;

        final double rectX = (graph.getUnscaledXPosition(nodeId) - minX) / diameter * canvas.getWidth();
        final double rectY = laneHeight / 2 + graph.getUnscaledYPosition(nodeId) * laneHeight;
        final double rectWidth = (sequenceLength / diameter) * canvas.getWidth();
        final double rectHeight = nodeHeightProperty.get();

        final Color color = graph.getColor(nodeId).getFXColor();

        graphicsContext.setFill(color);
        graphicsContext.fillRect(rectX, rectY, rectWidth, rectHeight);
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
            // TODO get node count from graph metadata
            rangeProperty.set((int) Math.min(DEFAULT_RANGE, Integer.MAX_VALUE));
            // TODO get node count from graph metadata and pick middle node
            centerNodeProperty.set((int) Math.round(DEFAULT_RANGE));

            final int centerNodeId = centerNodeProperty.get();
            final int unscaledCenterX = graph.getUnscaledXPosition(centerNodeId);
            final int range = rangeProperty.get();

            this.minX = unscaledCenterX - range;
            this.maxX = unscaledCenterX + range;
            final int[] lanes = {1};

            final List<Integer> neighbours = new ArrayList<>();
            graph.visitNeighbours(centerNodeId, SequenceDirection.LEFT, nodeId -> {
                neighbours.add(nodeId);
                if (graph != null) {
                    lanes[0] = Math.max(lanes[0], graph.getUnscaledYPosition(nodeId));
                }
            });
            graph.visitNeighbours(centerNodeId, SequenceDirection.RIGHT, nodeId -> {
                neighbours.add(nodeId);
                if (graph != null) {
                    lanes[0] = Math.max(lanes[0], graph.getUnscaledYPosition(nodeId));
                }
            });

            this.laneHeight = lanes[0] / canvas.getHeight();

            drawNode(graph, centerNodeId, minX, maxX, lanes[0]);
            for (Integer nodeId : neighbours) {
                drawNode(graph, nodeId, minX, maxX, lanes[0]);
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
            final int[] positions = toNodeCoordinates(canvas, event.getX(), event.getY());
            final int nodeX = positions[0];
            final int nodeLane = positions[1];

            if (graph != null) {
                final int nodeId = graph.getNode(centerNodeProperty.get(), rangeProperty.get(), nodeX, nodeLane);
                if (nodeId != -1) {
                    selectedNodeProperty.set(graph.getNode(nodeId));
                }
            }
        });
    }

    /**
     * Converts onscreen coordinates to coordinates which can be used to find the correct node.
     * <p>
     * The x coordinate depends on the widthproperty. The y property denotes in which lane the click is.
     *
     * @param canvas canvas who's width is used to get unscaled x position
     * @param xPos   x position onscreen
     * @param yPos   y position onscreen
     * @return x and y position in a double array of size 2 which correspond with x and y position of {@link
     * Node}.
     */
    private int[] toNodeCoordinates(final Canvas canvas, final double xPos, final double yPos) {
        final int diameter = maxX - minX;

        final int unscaledX = (int) (xPos / canvas.getWidth()) * diameter + minX;
        final int unscaledY = (int) (yPos / laneHeight);
        return new int[]{unscaledX, unscaledY};
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
    public IntegerProperty getCenterNodeIdProperty() {
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
