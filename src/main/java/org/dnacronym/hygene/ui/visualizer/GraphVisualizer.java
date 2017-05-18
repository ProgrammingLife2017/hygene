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
import org.dnacronym.hygene.ui.util.GraphDimensionsCalculator;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;


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
    private static final double DEFAULT_EDGE_WIDTH = 2;
    private static final double DEFAULT_DASH_LENGTH = 10;
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

    private GraphDimensionsCalculator graphDimensionsCalculator;
    private double laneHeight;
    private int minX;
    private int maxX;

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
     * @param calculator reference to the {@link GraphDimensionsCalculator} for the current drawing
     * @param graph      graph which contains all the nodes and their information
     * @param nodeId     id of node to draw
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawNode(final GraphDimensionsCalculator calculator, final Graph graph, final int nodeId) {
        final double rectX = calculator.computeXPosition(nodeId);
        final double rectY = calculator.computeYPosition(nodeId);
        final double rectWidth = calculator.computeWidth(nodeId);
        final double rectHeight = calculator.getNodeHeight();

        graphicsContext.setFill(graph.getColor(nodeId).getFXColor());
        graphicsContext.fillRect(rectX, rectY, rectWidth, rectHeight);
    }

    /**
     * Draws an edge on the canvas.
     *
     * @param calculator reference to the {@link GraphDimensionsCalculator} for the current drawing
     * @param fromNodeId edge origin node ID
     * @param toNodeId   edge destination node ID
     */
    @SuppressWarnings("nullness") // For performance, to prevent null checks during every draw.
    private void drawEdge(final GraphDimensionsCalculator calculator, final int fromNodeId, final int toNodeId) {
        graphicsContext.setLineWidth(DEFAULT_EDGE_WIDTH);
        graphicsContext.strokeLine(
                calculator.computeRightXPosition(fromNodeId), calculator.computeMiddleYPosition(fromNodeId),
                calculator.computeXPosition(toNodeId), calculator.computeMiddleYPosition(toNodeId)
        );
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
            final int[] minY = {graph.getUnscaledYPosition(centerNodeId)};
            final int[] maxY = {graph.getUnscaledYPosition(centerNodeId)};

            final List<Integer> neighbours = new LinkedList<>();
            neighbours.add(centerNodeId);

            final Consumer<Integer> iteratorAction = nodeId -> {
                if (graph != null) {
                    neighbours.add(nodeId);
                    minY[0] = Math.min(minY[0], graph.getUnscaledYPosition(nodeId));
                    maxY[0] = Math.max(maxY[0], graph.getUnscaledYPosition(nodeId));
                    minX = Math.min(minX, graph.getUnscaledXPosition(nodeId));
                    maxX = Math.max(maxX, graph.getUnscaledXPosition(nodeId) + graph.getSequenceLength(nodeId));
                }
            };

            graph.iterator().visitIndirectNeighboursWithinRange(
                    centerNodeId, SequenceDirection.LEFT, hopsProperty.get(), iteratorAction);
            graph.iterator().visitIndirectNeighboursWithinRange(
                    centerNodeId, SequenceDirection.RIGHT, hopsProperty.get(), iteratorAction);

            this.graphDimensionsCalculator = new GraphDimensionsCalculator(
                    graph, canvas, minX, maxX, minY[0], maxY[0], nodeHeightProperty.get()
            );

            laneHeight = graphDimensionsCalculator.getLaneHeight();

            for (Integer nodeId : neighbours) {
                drawNode(graphDimensionsCalculator, graph, nodeId);

                graph.iterator().visitDirectNeighbours(nodeId, SequenceDirection.RIGHT,
                        neighbourId -> drawEdge(graphDimensionsCalculator, nodeId, neighbourId)
                );
            }

            if (displayLaneBordersProperty.get()) {
                drawLaneBorders(graphDimensionsCalculator.getLaneCount(), laneHeight);
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
            final int[] positions = graphDimensionsCalculator.toNodeCoordinates(event.getX(), event.getY());
            final int nodeX = positions[0];
            final int nodeLane = positions[1];

            // TODO write findNode(x, y) in Graph class
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
    public void setGraph(final Graph graph) {
        this.graph = graph;

        centerNodeIdProperty.set(graph.getNodeArrays().length / 2);
        hopsProperty.set((int) Math.min(DEFAULT_RANGE, (double) graph.getNodeArrays().length / 2));
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
