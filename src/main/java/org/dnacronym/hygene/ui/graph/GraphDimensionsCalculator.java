package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.GraphQuery;
import org.dnacronym.hygene.models.Node;

import java.util.LinkedList;
import java.util.List;


/**
 * Performs calculations for determining node/graph positions and dimensions.
 * <p>
 * These positions are used by the {@link GraphVisualizer} to draw nodes onscreen. The positions are determined by the
 * set canvas width and height, set center node id and set radius.
 * <p>
 * Every time the center node id is updated, or the range, a new query is performed to update the neighbours list. This
 * list represents the nodes that should be drawn onscreen. Node coordinates are determined as to make sure the nodes
 * fill the screen lengthwise.
 *
 * @see GraphDimensionsCalculator
 * @see GraphQuery
 */
public final class GraphDimensionsCalculator {
    /**
     * The default horizontal displacement between two adjacent nodes.
     */
    private static final int DEFAULT_EDGE_WIDTH = 1000;
    private static final int DEFAULT_RANGE = 10;

    private final IntegerProperty minXNodeIdProperty;
    private final IntegerProperty maxXNodeIdProperty;

    private final IntegerProperty centerNodeIdProperty;
    private final IntegerProperty radiusProperty;
    private final IntegerProperty onScreenNodeCountProperty;
    private final IntegerProperty nodeCountProperty;

    private final DoubleProperty nodeHeightProperty;
    private final DoubleProperty laneHeightProperty;
    private final IntegerProperty laneCountProperty;

    /**
     * The {@link Graph} used to get the unscaled coordinates of nodes.
     */
    private Graph graph;
    private int minX;
    private int maxX;
    private int minY;

    private double canvasWidth = 0;
    private double canvasHeight = 0;

    /**
     * List of nodes that should be drawn onscreen.
     */
    private final ObservableList<Integer> observableNeighbours;
    private GraphQuery graphQuery;


    /**
     * Create a new instance of {@link GraphDimensionsCalculator}.
     */
    public GraphDimensionsCalculator() {
        minXNodeIdProperty = new SimpleIntegerProperty(1);
        maxXNodeIdProperty = new SimpleIntegerProperty(1);

        centerNodeIdProperty = new SimpleIntegerProperty(1);
        radiusProperty = new SimpleIntegerProperty(1);
        centerNodeIdProperty.addListener((observable, oldValue, newValue) ->
                query(newValue.intValue(), radiusProperty.get()));
        radiusProperty.addListener((observable, oldValue, newValue) ->
                query(centerNodeIdProperty.get(), newValue.intValue()));

        onScreenNodeCountProperty = new SimpleIntegerProperty();
        nodeCountProperty = new SimpleIntegerProperty(1);

        nodeHeightProperty = new SimpleDoubleProperty();
        laneHeightProperty = new SimpleDoubleProperty(1);
        laneCountProperty = new SimpleIntegerProperty(1);

        observableNeighbours = FXCollections.observableArrayList();
    }


    /**
     * Calculate the following values.
     * <p>
     * <ul>
     * <li> Neighbours list
     * <li> Minimum unscaled x
     * <li> Maximum unscaled y
     * <li> Minimum unscaled y
     * <li> Lane count property
     * <li> Lane height property
     * <li> On screen node count property
     * </ul>
     * <p>
     * If the graph has not been set, this method does nothing.
     */
    private void calculate() {
        if (graph == null || radiusProperty.get() == 0) {
            return;
        }

        final int centerNodeId = centerNodeIdProperty.get();

        final int unscaledCenterX = graph.getUnscaledXPosition(centerNodeId)
                + graph.getUnscaledXEdgeCount(centerNodeId) * DEFAULT_EDGE_WIDTH;
        final int[] tempMinX = {unscaledCenterX};
        final int[] tempMaxX = {unscaledCenterX};
        final int[] tempMinY = {graph.getUnscaledYPosition(centerNodeId)};
        final int[] tempMaxY = {graph.getUnscaledYPosition(centerNodeId)};

        final List<Integer> neighbours = new LinkedList<>();
        neighbours.add(centerNodeId);

        graphQuery.visit(nodeId -> {
            neighbours.add(nodeId);

            final int nodeLeftX = graph.getUnscaledXPosition(nodeId)
                    + graph.getUnscaledXEdgeCount(nodeId) * DEFAULT_EDGE_WIDTH;
            if (tempMinX[0] > nodeLeftX) {
                tempMinX[0] = nodeLeftX;
                minXNodeIdProperty.set(nodeId);
            }

            final int nodeRightX = graph.getUnscaledXPosition(nodeId) + graph.getLength(nodeId)
                    + graph.getUnscaledXEdgeCount(nodeId) * DEFAULT_EDGE_WIDTH;
            if (tempMaxX[0] < nodeRightX) {
                tempMaxX[0] = nodeRightX;
                maxXNodeIdProperty.set(nodeId);
            }

            tempMinY[0] = Math.min(tempMinY[0], graph.getUnscaledYPosition(nodeId));
            tempMaxY[0] = Math.max(tempMaxY[0], graph.getUnscaledYPosition(nodeId));
        });

        observableNeighbours.setAll(neighbours);

        this.minX = tempMinX[0];
        this.maxX = tempMaxX[0];
        this.minY = tempMinY[0];
        final int maxY = tempMaxY[0];

        laneCountProperty.set(Math.abs(maxY - minY) + 1);
        laneHeightProperty.set(canvasHeight / laneCountProperty.get());
        onScreenNodeCountProperty.set(observableNeighbours.size());
    }

    /**
     * Set the {@link Graph} used for calculations.
     * <p>
     * Sets the center node id to halfway the graph, and the range to 10.
     *
     * @param graph the {@link Graph} for use by calculations
     */
    void setGraph(final Graph graph) {
        this.graph = graph;
        graphQuery = new GraphQuery(graph);

        nodeCountProperty.set(graph.getNodeArrays().length);
        centerNodeIdProperty.set(nodeCountProperty.divide(2).intValue());
        radiusProperty.set(DEFAULT_RANGE);
    }

    /**
     * Set the size of the canvas on which the {@link Graph} will be drawn.
     * <p>
     * This will perform a another calculation.
     *
     * @param canvasWidth  the width of the {@link javafx.scene.canvas.Canvas}
     * @param canvasHeight the height of the {@link javafx.scene.canvas.Canvas}
     */
    void setCanvasSize(final double canvasWidth, final double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        calculate();
    }

    /**
     * Perform a new query with the set center node id and set range.
     * <p>
     * If the center node id and range are equal to the stored values, this method does nothing. Otherwise, sets the
     * current center node id to the set value. If it is outside the bounds {@code [0, graph node count]}, it is set to
     * the closest bound. The current range is also updated to the set value. A new query of the graph is performed
     * using the internal {@link GraphQuery}.
     *
     * @param centerNodeId the center of the query
     * @param range        the range of the query
     * @see GraphQuery#query(int, int)
     */
    public void query(final int centerNodeId, final int range) {
        final int newCenterNodeId = Math.max(0, Math.min(centerNodeId, nodeCountProperty.get()) - 1);

        if (centerNodeIdProperty.get() != newCenterNodeId && radiusProperty.get() == range) {
            return;
        }

        centerNodeIdProperty.set(newCenterNodeId);
        radiusProperty.set(range);

        calculate();
    }

    /**
     * Computes the absolute x position of a node within the current canvas.
     *
     * @param nodeId the id of the node
     * @return the absolute x position of a node within the current canvas
     */
    public double computeXPosition(final int nodeId) {
        final int xPosition = graph.getUnscaledXPosition(nodeId) - graph.getLength(nodeId)
                + graph.getUnscaledXEdgeCount(nodeId) * DEFAULT_EDGE_WIDTH;
        return (double) (xPosition - minX) / (maxX - minX) * canvasWidth;
    }

    /**
     * Computes the absolute right x position of a node in pixels within the set canvas dimensions.
     *
     * @param nodeId the id of the node
     * @return the absolute right x position of a node within the current canvas
     */
    public double computeRightXPosition(final int nodeId) {
        return computeXPosition(nodeId) + computeWidth(nodeId);
    }

    /**
     * Computes the absolute y position of a node in pixels within the set canvas dimensions.
     *
     * @param nodeId the id of the node
     * @return the absolute y position of a node within the current canvas
     */
    public double computeYPosition(final int nodeId) {
        final int yPosition = graph.getUnscaledYPosition(nodeId);
        return (yPosition - minY) * laneHeightProperty.get() + laneHeightProperty.get() / 2
                - nodeHeightProperty.get() / 2;
    }

    /**
     * Computes the absolute middle y position of a node in pixels within the set canvas dimensions.
     *
     * @param nodeId the id of the node
     * @return the absolute middle y position of a node within the current canvas
     */
    public double computeMiddleYPosition(final int nodeId) {
        return computeYPosition(nodeId) + nodeHeightProperty.get() / 2;
    }

    /**
     * Computes the width of a node in pixels within the set canvas dimensions.
     *
     * @param nodeId the id of the node
     * @return the width of a node
     */
    public double computeWidth(final int nodeId) {
        return (double) graph.getLength(nodeId) / (maxX - minX) * canvasWidth;
    }

    /**
     * Get the {@link ObservableList} of the neighbours of the set center id with the set range.
     * <p>
     * Every time this list is updated, all nodes in the previous list should be discarded and all nodes in the new list
     * should be drawn.
     *
     * @return the {@link ObservableList} of the neighbours of the set neighbours set range away
     */
    public ObservableList<Integer> getNeighbours() {
        return observableNeighbours;
    }

    /**
     * Gets the node id with the minimum x in unscaled x coordinates.
     *
     * @return the minimum node id {@link IntegerProperty} in the x direction
     */
    public ReadOnlyIntegerProperty getMinXNodeIdProperty() {
        return minXNodeIdProperty;
    }

    /**
     * Gets the node id with the maximum x in unscaled x coordinates.
     *
     * @return the maximum node id {@link IntegerProperty} in the x direction
     */
    public ReadOnlyIntegerProperty getMaxXNodeIdProperty() {
        return maxXNodeIdProperty;
    }

    /**
     * Returns the {@link ReadOnlyIntegerProperty} which describes the amount of lanes.
     *
     * @return the {@link ReadOnlyIntegerProperty} which describes the amount of lanes
     */
    public ReadOnlyIntegerProperty getLaneCountProperty() {
        return laneCountProperty;
    }

    /**
     * Returns the {@link DoubleProperty} which describes the height of lanes.
     *
     * @return the {@link DoubleProperty} which describes the height of lanes
     */
    public DoubleProperty getLaneHeightProperty() {
        return laneHeightProperty;
    }

    /**
     * Returns the {@link DoubleProperty} which describes the height of nodes.
     *
     * @return the {@link DoubleProperty} which describes the height of nodes
     */
    public DoubleProperty getNodeHeightProperty() {
        return nodeHeightProperty;
    }

    /**
     * The property which describes the amount of node in the set graph.
     *
     * @return property which describes the amount of nodes in the set graph
     */
    public ReadOnlyIntegerProperty getNodeCountProperty() {
        return nodeCountProperty;
    }

    /**
     * The property which describes the amount of nodes onscreen.
     *
     * @return property which describes the amount of nodes on screen
     */
    public ReadOnlyIntegerProperty getOnScreenNodeCountProperty() {
        return onScreenNodeCountProperty;
    }

    /**
     * Property which determines the current center {@link Node} id.
     * <p>
     * Every time this value is updated, {@link #query(int, int)} is called. This method ensures that the center node id
     * remains in bounds.
     *
     * @return property which decides the current center {@link Node} id
     */
    public IntegerProperty getCenterNodeIdProperty() {
        return centerNodeIdProperty;
    }

    /**
     * The property which determines the range to draw around the center node.
     * <p>
     * Every time this value is updated, {@link #query(int, int)} is called. This method ensures that the range remains
     * in bounds.
     *
     * @return property which determines the amount of hops to draw in each direction around the center node
     */
    public IntegerProperty getRadiusProperty() {
        return radiusProperty;
    }
}
