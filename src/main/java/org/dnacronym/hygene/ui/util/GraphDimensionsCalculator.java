package org.dnacronym.hygene.ui.util;

import javafx.scene.canvas.Canvas;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.GraphIterator;
import org.dnacronym.hygene.models.GraphQuery;

import java.util.LinkedList;
import java.util.List;


/**
 * Performs calculations for determining node/graph positions and dimensions.
 */
public final class GraphDimensionsCalculator {
    private static final Logger LOGGER = LogManager.getLogger(GraphDimensionsCalculator.class);

    private final Graph graph;
    private final GraphQuery query;
    private final double canvasHeight;
    private final double canvasWidth;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final double nodeHeight;

    private double laneHeight = -1;
    private int laneCount = -1;

    private final List<Integer> neighbours;


    /**
     * Constructs and initializes an instance of {@link GraphDimensionsCalculator}.
     * <p>
     * The calculator also internally stores the id of all the neighbours of a set center node id.
     *
     * @param graph        a reference to the current {@link Graph}, used to get node properties
     * @param canvas       a reference to the current {@link Canvas}, used for determining width and height
     * @param centerNodeId node id of the center node
     * @param hops         Amount of hops allowed from center node
     * @param nodeHeight   the height of a single node
     * @see org.dnacronym.hygene.models.GraphIterator#visitIndirectNeighboursWithinRange(int, int,
     * java.util.function.Consumer)
     */
    public GraphDimensionsCalculator(final Graph graph, final Canvas canvas, final int centerNodeId, final int hops,
                                     final double nodeHeight) {
        this.graph = graph;
        this.canvasHeight = canvas.getHeight();
        this.canvasWidth = canvas.getWidth();
        this.nodeHeight = nodeHeight;

        final int unscaledCenterX = graph.getUnscaledXPosition(centerNodeId);
        final int[] tempMinX = {unscaledCenterX};
        final int[] tempMaxX = {unscaledCenterX};
        final int[] tempMinY = {graph.getUnscaledYPosition(centerNodeId)};
        final int[] tempMaxY = {graph.getUnscaledYPosition(centerNodeId)};

        neighbours = new LinkedList<>();
        neighbours.add(centerNodeId);

        query = new GraphQuery(graph);
        query.query(centerNodeId, hops);
        query.visit(nodeId -> {
            neighbours.add(nodeId);
            tempMinX[0] = Math.min(tempMinX[0], graph.getUnscaledXPosition(nodeId));
            tempMaxX[0] = Math.max(tempMaxX[0], graph.getUnscaledXPosition(nodeId) + graph.getLength(nodeId));
            tempMinY[0] = Math.min(tempMinY[0], graph.getUnscaledYPosition(nodeId));
            tempMaxY[0] = Math.max(tempMaxY[0], graph.getUnscaledYPosition(nodeId));
        });

        this.minX = tempMinX[0];
        this.maxX = tempMaxX[0];
        this.minY = tempMinY[0];
        this.maxY = tempMaxY[0];
    }


    /**
     * Computes the diameter of the graph.
     *
     * @return the diameter of the graph
     */
    public int computeDiameter() {
        return maxX - minX;
    }

    /**
     * Computes the absolute x position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute x position of a node within the current canvas
     */
    public double computeXPosition(final int nodeId) {
        if (computeDiameter() == 0) {
            LOGGER.warn("Diameter of graph is 0");
            return 0;
        }

        final int xPosition = graph.getUnscaledXPosition(nodeId) - graph.getLength(nodeId);
        return (double) (xPosition - minX) / computeDiameter() * canvasWidth;
    }

    /**
     * Computes the absolute right x position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute right x position of a node within the current canvas
     */
    public double computeRightXPosition(final int nodeId) {
        return computeXPosition(nodeId) + computeWidth(nodeId);
    }

    /**
     * Computes the absolute y position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute y position of a node within the current canvas
     */
    public double computeYPosition(final int nodeId) {
        final int yPosition = graph.getUnscaledYPosition(nodeId);

        return (yPosition - minY) * getLaneHeight() + getLaneHeight() / 2 - nodeHeight / 2;
    }

    /**
     * Computes the absolute middle y position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute middle y position of a node within the current canvas
     */
    public double computeMiddleYPosition(final int nodeId) {
        return computeYPosition(nodeId) + getNodeHeight() / 2;
    }

    /**
     * Computes the width of a node.
     *
     * @param nodeId ID of the node
     * @return the width of a node
     */
    public double computeWidth(final int nodeId) {
        return (double) graph.getLength(nodeId) / computeDiameter() * canvasWidth;
    }

    /**
     * Gets the height of a node.
     *
     * @return the height of a node
     */
    public double getNodeHeight() {
        return nodeHeight;
    }

    /**
     * Gets the lane height.
     *
     * @return the height of a lane
     */
    public double getLaneHeight() {
        if (Double.compare(laneHeight, -1) == 0) {
            laneHeight = canvasHeight / getLaneCount();
        }

        return laneHeight;
    }

    /**
     * Gets the lane count.
     *
     * @return the lane count
     */
    public int getLaneCount() {
        if (Double.compare(laneCount, -1) == 0) {
            laneCount = Math.abs(maxY - minY) + 1;
        }

        return laneCount;
    }

    /**
     * Get the neighbours of the set center node, no more than set hops away.
     *
     * @return neighbours of set center node
     */
    public List<Integer> getNeighbours() {
        return neighbours;
    }
}
