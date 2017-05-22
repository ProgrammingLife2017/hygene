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
import org.dnacronym.hygene.models.Edge;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.SequenceDirection;
import org.dnacronym.hygene.ui.util.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.util.RTree;

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
    private static final double DEFAULT_EDGE_WIDTH = 1;
    private static final double DEFAULT_DASH_LENGTH = 10;
    /**
     * Range used when new graph is set, unless graph contains too few nodes.
     */
    private static final double DEFAULT_RANGE = 10;

    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;

    private final ObjectProperty<Node> selectedNodeProperty;
    private final ObjectProperty<Edge> selectedEdgeProperty;

    private final IntegerProperty centerNodeIdProperty;
    private final IntegerProperty hopsProperty;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;

    private final BooleanProperty displayLaneBordersProperty;
    private final DoubleProperty borderDashLengthProperty;

    private final IntegerProperty nodeCountProperty;
    private Graph graph;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private RTree rTree;


    /**
     * Create a new {@link GraphVisualizer} instance.
     */
    public GraphVisualizer() {
        selectedNodeProperty = new SimpleObjectProperty<>();
        selectedEdgeProperty = new SimpleObjectProperty<>();

        centerNodeIdProperty = new SimpleIntegerProperty(0);
        hopsProperty = new SimpleIntegerProperty(0);
        centerNodeIdProperty.addListener((observable, oldValue, newValue) -> draw());
        hopsProperty.addListener((observable, oldValue, newValue) -> draw());

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        edgeColorProperty.addListener((observable, oldValue, newValue) -> draw());
        nodeHeightProperty.addListener((observable, oldValue, newValue) -> draw());

        displayLaneBordersProperty = new SimpleBooleanProperty();
        borderDashLengthProperty = new SimpleDoubleProperty(DEFAULT_DASH_LENGTH);
        displayLaneBordersProperty.addListener((observable, oldValue, newValue) -> draw());
        borderDashLengthProperty.addListener((observable, oldValue, newValue) -> draw());

        nodeCountProperty = new SimpleIntegerProperty();
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
    private void drawNode(final GraphDimensionsCalculator calculator, final Graph graph, final int nodeId) {
        final double rectX = calculator.computeXPosition(nodeId);
        final double rectY = calculator.computeYPosition(nodeId);
        final double rectWidth = calculator.computeWidth(nodeId);
        final double rectHeight = calculator.getNodeHeight();

        graphicsContext.setFill(graph.getColor(nodeId).getFXColor());
        graphicsContext.fillRect(rectX, rectY, rectWidth, rectHeight);

        rTree.addNode(nodeId, rectX, rectY, rectWidth, rectHeight);
    }

    /**
     * Draws an edge on the canvas.
     *
     * @param calculator reference to the {@link GraphDimensionsCalculator} for the current drawing
     * @param fromNodeId edge origin node ID
     * @param toNodeId   edge destination node ID
     */
    private void drawEdge(final GraphDimensionsCalculator calculator, final int fromNodeId, final int toNodeId) {
        final double fromX = calculator.computeRightXPosition(fromNodeId);
        final double fromY = calculator.computeMiddleYPosition(fromNodeId);
        final double toX = calculator.computeXPosition(toNodeId);
        final double toY = calculator.computeMiddleYPosition(toNodeId);

        graphicsContext.setStroke(edgeColorProperty.getValue());
        graphicsContext.setLineWidth(DEFAULT_EDGE_WIDTH);
        graphicsContext.strokeLine(fromX, fromY, toX, toY);

        rTree.addEdge(fromNodeId, toNodeId, fromX, fromY, toX, toY);
    }

    /**
     * Clear the canvas.
     */
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
    public void draw() {
        if (canvas == null || graphicsContext == null) {
            throw new IllegalStateException("Attempting to draw whilst canvas not set.");
        }

        clear();
        rTree = new RTree();

        final int centerNodeId = centerNodeIdProperty.get();

        final GraphDimensionsCalculator graphDimensionsCalculator = new GraphDimensionsCalculator(
                graph, canvas, centerNodeId, hopsProperty.get(), nodeHeightProperty.get()
        );
        final List<Integer> neighbours = graphDimensionsCalculator.getNeighbours();

        for (final Integer nodeId : neighbours) {
            drawNode(graphDimensionsCalculator, graph, nodeId);

            graph.iterator().visitDirectNeighbours(
                    nodeId, SequenceDirection.RIGHT,
                    neighbourId -> drawEdge(graphDimensionsCalculator, nodeId, neighbourId)
            );
        }

        if (displayLaneBordersProperty.get()) {
            drawLaneBorders(
                    graphDimensionsCalculator.getLaneCount(),
                    graphDimensionsCalculator.getLaneHeight());
        }
    }

    /**
     * Draw the border between bands as {@link Color#BLACK}.
     *
     * @param laneCount  amount of bands onscreen
     * @param laneHeight height of each band
     */
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
            selectedNodeProperty.setValue(null);
            selectedEdgeProperty.setValue(null);

            rTree.find(event.getX(), event.getY(),
                    nodeId -> selectedNodeProperty.setValue(graph.getNode(nodeId)),
                    (fromNodeId, toNodeId) -> graph.getNode(fromNodeId).getOutgoingEdges().stream()
                            .filter(edge -> edge.getTo() == toNodeId)
                            .findFirst()
                            .ifPresent(selectedEdgeProperty::setValue)
            );
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
        clear();

        nodeCountProperty.set(graph.getNodeArrays().length);
        centerNodeIdProperty.set(graph.getNodeArrays().length / 2);
        hopsProperty.set((int) Math.min(DEFAULT_RANGE, (double) nodeCountProperty.get() / 2));
    }

    /**
     * The property of the selected node.
     * <p>
     * This node is updated every time the user clicks on a node in the canvas.
     *
     * @return Selected {@link Node} by the user, which can be {@code null}
     */
    public ObjectProperty<Node> getSelectedNodeProperty() {
        return selectedNodeProperty;
    }

    /**
     * The property of the selected edge.
     * <p>
     * This edge is updated every time the user clicks on an edge in the canvas.
     *
     * @return Selected {@link Edge} by the user, which can be {@code null}
     */
    public ObjectProperty<Edge> getSelectedEdgeProperty() {
        return selectedEdgeProperty;
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

    /**
     * The property which describes the amount of node in the set graph.
     *
     * @return property which describes the amount of nodes in the set graph
     */
    public IntegerProperty getNodeCountProperty() {
        return nodeCountProperty;
    }
}
