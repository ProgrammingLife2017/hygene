package org.dnacronym.hygene.ui.util;

import javafx.scene.canvas.Canvas;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Graph;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;


/**
 * Performs calculations for determinging node/graph positions and dimensions.
 */
public final class GraphDimensionsCalculator {
    private static final Logger LOGGER = LogManager.getLogger(GraphDimensionsCalculator.class);

    private final Graph graph;
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
     * Consumer)
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

        final Consumer<Integer> consumer = nodeId -> {
            neighbours.add(nodeId);
            tempMinX[0] = Math.min(tempMinX[0], graph.getUnscaledXPosition(nodeId));
            tempMaxX[0] = Math.max(tempMaxX[0], graph.getUnscaledXPosition(nodeId) + graph.getSequenceLength(nodeId));
            tempMinY[0] = Math.min(tempMinY[0], graph.getUnscaledYPosition(nodeId));
            tempMaxY[0] = Math.max(tempMaxY[0], graph.getUnscaledYPosition(nodeId));
        };

        graph.iterator().visitIndirectNeighboursWithinRange(centerNodeId, hops, consumer);

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

        final int xPosition = graph.getUnscaledXPosition(nodeId);
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
        return (double) graph.getSequenceLength(nodeId) / computeDiameter() * canvasWidth;
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
     * Converts onscreen coordinates to coordinates which can be used to find the correct node.
     *
     * @param xPos x position onscreen
     * @param yPos y position onscreen
     * @return x and y position in a double array of size 2 which correspond with x and y position of a node
     */
    public int[] toNodeCoordinates(final double xPos, final double yPos) {
        final int diameter = maxX - minX;

        final int unscaledX = (int) (xPos / canvasWidth) * diameter + minX;
        final int unscaledY = (int) (yPos / laneHeight);

        LOGGER.info("User clicked on x: " + xPos + ", y: " + yPos + ", "
                + "Unscaled x: " + unscaledX + ", unscaled Y: " + unscaledY);

        return new int[]{unscaledX, unscaledY};
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
