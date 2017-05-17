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

import java.util.LinkedList;
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
    private static final double ARC_SIZE = 20;
    /**
     * Range used when new graph is set, unless graph contains too few nodes.
     */
    private static final double DEFAULT_RANGE = 100;

    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;

    private final ObjectProperty<Node> selectedNodeProperty;

    private final IntegerProperty centerNodeIdProperty;
    private final IntegerProperty hopsProperty;

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

        centerNodeIdProperty = new SimpleIntegerProperty(0);
        hopsProperty = new SimpleIntegerProperty(0);
        centerNodeIdProperty.addListener((observable, oldValue, newValue) -> draw());
        hopsProperty.addListener((observable, oldValue, newValue) -> draw());

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        edgeColorProperty.addListener((observable, oldValue, newValue) -> draw());
        nodeHeightProperty.addListener((observable, oldValue, newValue) -> draw());

        laneHeight = DEFAULT_NODE_HEIGHT;

        displayLaneBordersProperty = new SimpleBooleanProperty();
        borderDashLengthProperty = new SimpleDoubleProperty(DEFAULT_DASH_LENGTH);
        displayLaneBordersProperty.addListener((observable, oldValue, newValue) -> draw());
        borderDashLengthProperty.addListener((observable, oldValue, newValue) -> draw());
    }


    /**
     * Draw a node on the canvas.
     * <p>
     * The minimum and maximum x positions are assumed to be unscaled.
     *
     * @param graph      graph which contains all the nodes and their information
     * @param nodeId     id of node to draw
     * @param minX       unscaled minimum x position
     * @param maxX       unscaled maximum x position
     * @param laneHeight height of a single lane in pixels
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawNode(final Graph graph, final int nodeId, final double minX, final double maxX,
                          final double laneHeight) {
        final double diameter = maxX - minX;
        final double xPosition = graph.getUnscaledXPosition(nodeId);
        final double yPosition = graph.getUnscaledYPosition(nodeId);

        final double rectX = (xPosition - minX) / diameter * canvas.getWidth();
        final double rectY = yPosition * laneHeight + yPosition * laneHeight / 2 - nodeHeightProperty.get() / 2;
        final double rectWidth = graph.getSequenceLength(nodeId) / diameter * canvas.getWidth();
        final double rectHeight = nodeHeightProperty.get();

        graphicsContext.setFill(graph.getColor(nodeId).getFXColor());
        graphicsContext.fillRoundRect(rectX, rectY, rectWidth, rectHeight, ARC_SIZE, ARC_SIZE);
    }

    /**
     * Clear the canvas.
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Populate the graphs primitives with the given graph.
     * <p>
     * First clears the graph before drawing. If {@link Graph} is null, only clears the canvas.
     *
     * @throws IllegalStateException if the {@link Canvas} has not been set
     */
    @SuppressWarnings("nullness")
    public void draw() {
        if (canvas == null || graphicsContext == null) {
            throw new IllegalStateException("Attempting to draw whilst canvas not set.");
        }

        clear();
        if (graph != null && canvas != null) {
            final int centerNodeId = centerNodeIdProperty.get();
            final int unscaledCenterX = graph.getUnscaledXPosition(centerNodeId);

            minX = unscaledCenterX;
            maxX = unscaledCenterX;
            final int[] laneCount = {1};

            final List<Integer> neighbours = new LinkedList<>();
            graph.visitIndirectNeighboursWithinRange(centerNodeId, SequenceDirection.LEFT, hopsProperty.get(),
                    nodeId -> false,
                    nodeId -> {
                        if (graph != null) {
                            neighbours.add(nodeId);
                            laneCount[0] = Math.max(laneCount[0], graph.getUnscaledYPosition(nodeId));
                            minX = Math.min(minX, graph.getUnscaledXPosition(nodeId));
                        }
                    });
            graph.visitIndirectNeighboursWithinRange(centerNodeId, SequenceDirection.RIGHT, hopsProperty.get(),
                    nodeId -> false,
                    nodeId -> {
                        if (graph != null) {
                            neighbours.add(nodeId);
                            laneCount[0] = Math.max(laneCount[0], graph.getUnscaledYPosition(nodeId));
                            maxX = Math.max(maxX, graph.getUnscaledXPosition(nodeId) + graph.getSequenceLength(nodeId));
                        }
                    });

            laneHeight = laneCount[0] / canvas.getHeight();

            drawNode(graph, centerNodeId, minX, maxX, laneHeight);
            for (Integer nodeId : neighbours) {
                drawNode(graph, nodeId, minX, maxX, laneHeight);
            }

            if (displayLaneBordersProperty.get()) {
                drawLaneBorders(laneCount[0], laneHeight);
            }
        }
    }

    /**
     * Draw the border between bands as {@link Color#BLACK}.
     *
     * @param laneCount  amount of bands onscreen
     * @param laneHeight height of each band
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawLaneBorders(final int laneCount, final double laneHeight) {
        final Paint originalStroke = graphicsContext.getStroke();
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

        graphicsContext.setStroke(originalStroke);
        graphicsContext.setLineWidth(originalLineWidth);
        graphicsContext.setLineDashes(0);
    }

    /**
     * Set {@link Canvas} which the {@link GraphVisualizer} use to draw.
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
                final int nodeId = graph.getNode(centerNodeIdProperty.get(), hopsProperty.get(), nodeX, nodeLane);
                if (nodeId != -1) {
                    selectedNodeProperty.set(graph.getNode(nodeId));
                }
            }
        });
    }

    /**
     * Set the {@link Graph} reference, which may be {@code null}.
     * <p>
     * This graph is used when {@link #draw()} is called. The center node id is set to the center of the graph, and the
     * range property to the minimum of {@value DEFAULT_RANGE} and the radius of the graph.
     *
     * @param graph graph to set in the {@link GraphVisualizer}
     */
    public void setGraph(@Nullable final Graph graph) {
        this.graph = graph;

        // TODO get node count from graph metadata and pick middle node
        centerNodeIdProperty.set((int) Math.round(DEFAULT_RANGE));
        // TODO get node count from graph metadata
        hopsProperty.set((int) Math.min(DEFAULT_RANGE, Integer.MAX_VALUE));
    }

    /**
     * Converts onscreen coordinates to coordinates which can be used to find the correct node.
     *
     * @param canvas canvas who's width is used to get unscaled x position
     * @param xPos   x position onscreen
     * @param yPos   y position onscreen
     * @return x and y position in a double array of size 2 which correspond with x and y position of {@link Node}
     */
    private int[] toNodeCoordinates(final Canvas canvas, final double xPos, final double yPos) {
        final int diameter = maxX - minX;

        final int unscaledX = (int) (xPos / canvas.getWidth()) * diameter + minX;
        final int unscaledY = (int) (yPos / laneHeight);
        return new int[]{unscaledX, unscaledY};
    }

    /**
     * The property of the selected node.
     * <p>
     * This node is updated every time the user clicks on the canvas.
     *
     * @return Selected {@link Node} by the user, which can be {@code null}
     */
    public ObjectProperty<Node> getSelectedNodeProperty() {
        return selectedNodeProperty;
    }

    /**
     * Property which determines the current center {@link Node} id.
     *
     * @return property which decides the current center {@link Node} id
     */
    public IntegerProperty getCenterNodeIdProperty() {
        return centerNodeIdProperty;
    }

    /**
     * The property which determines the range to draw around the center node.
     * <p>
     * This range is given in the amount of hops
     *
     * @return property which determines the amount of hops to draw in each direction around the center node
     */
    public IntegerProperty getHopsProperty() {
        return hopsProperty;
    }

    /**
     * The property of onscreen edge {@link Color}s.
     *
     * @return property which decides the {@link Color} of edges
     */
    public ObjectProperty<Color> getEdgeColorProperty() {
        return edgeColorProperty;
    }

    /**
     * The property of onscreen node heights.
     *
     * @return property which decides the height of nodes
     */
    public DoubleProperty getNodeHeightProperty() {
        return nodeHeightProperty;
    }

    /**
     * The property which determines whether to display the border between bands as black bands.
     *
     * @return property which decides whether to display the border between bands
     */
    public BooleanProperty getDisplayBordersProperty() {
        return displayLaneBordersProperty;
    }

    /**
     * The property which determines how long the onscreen dashes should be.
     *
     * @return property which determines the dash length
     */
    public DoubleProperty getBorderDashLengthProperty() {
        return borderDashLengthProperty;
    }
}
