package org.dnacronym.hygene.ui.util;

import javafx.scene.canvas.Canvas;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Graph;


/**
 * Performs calculations for determinging node/graph positions and dimensions.
 */
public final class GraphDimensionsCalculator {
    private static final Logger LOGGER = LogManager.getLogger(GraphDimensionsCalculator.class);

    private final Graph graph;
    private final int laneCount;
    private final double canvasHeight;
    private final double canvasWidth;
    private final int minX;
    private final int maxX;
    private final double nodeHeight;

    private double laneHeight = -1;


    /**
     * Constructs and initializes an instance of {@link GraphDimensionsCalculator}.
     *
     * @param graph      a reference to the current {@link Graph}, used to get node properties
     * @param canvas     a reference to the current {@link Canvas}, used for determining width and height
     * @param laneCount  the total number of lanes
     * @param minX       the lowest x position in the current subgraph
     * @param maxX       the highest x position in the current subgraph
     * @param nodeHeight the height of a single node
     */
    public GraphDimensionsCalculator(final Graph graph, final Canvas canvas, final int laneCount,
                                     final int minX, final int maxX, final double nodeHeight) {
        this.graph = graph;
        this.laneCount = laneCount;
        this.canvasHeight = canvas.getHeight();
        this.canvasWidth = canvas.getWidth();
        this.minX = minX;
        this.maxX = maxX;
        this.nodeHeight = nodeHeight;
    }


    /**
     * Computes the diameter of the graph.
     *
     * @return the diameter of the graph.
     */
    public int computeDiameter() {
        return maxX - minX;
    }

    /**
     * Computes the absolute x position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute x position of a node within the current canvas.
     */
    public double computeXPosition(final int nodeId) {
        final int fafospPosition = graph.getUnscaledXPosition(nodeId);

        if (computeDiameter() == 0) {
            LOGGER.info("Diameter was 0");
            return 0;
        }
        return (double) (fafospPosition - minX) / computeDiameter() * canvasWidth;
    }

    /**
     * Computes the absolute right x position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute right x position of a node within the current canvas.
     */
    public double computeRightXPosition(final int nodeId) {
        return computeXPosition(nodeId) + computeWidth(nodeId);
    }

    /**
     * Computes the absolute y position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute y position of a node within the current canvas.
     */
    public double computeYPosition(final int nodeId) {
        final int fafospPosition = graph.getUnscaledYPosition(nodeId);

        return fafospPosition * getLaneHeight() + getLaneHeight() / 2 - nodeHeight / 2;
    }

    /**
     * Computes the absolute middle y position of a node within the current canvas.
     *
     * @param nodeId ID of the node
     * @return the absolute middle y position of a node within the current canvas.
     */
    public double computeMiddleYPosition(final int nodeId) {
        return computeYPosition(nodeId) + getNodeHeight() / 2;
    }

    /**
     * Computes the width of a node.
     *
     * @param nodeId ID of the node
     * @return the width of a node.
     */
    public double computeWidth(final int nodeId) {
        return (double) graph.getSequenceLength(nodeId) / computeDiameter() * canvasWidth;
    }

    /**
     * Gets the height of a node.
     *
     * @return the height of a node.
     */
    public double getNodeHeight() {
        return nodeHeight;
    }

    /**
     * Gets the lane height.
     *
     * @return the height of a lane.
     */
    public double getLaneHeight() {
        if (laneHeight == -1) {
            laneHeight = canvasHeight / laneCount;
        }

        return laneHeight;
    }
}
