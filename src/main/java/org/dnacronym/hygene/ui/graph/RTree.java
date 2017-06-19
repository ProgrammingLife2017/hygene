package org.dnacronym.hygene.ui.graph;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Line;
import com.github.davidmoten.rtree.geometry.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.github.davidmoten.rtree.geometry.Geometries.line;
import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;


/**
 * Represents an RTree which can be used by the UI classes to keep track of where elements are placed.
 */
public final class RTree {
    private static final Logger LOGGER = LogManager.getLogger(RTree.class);

    private static final double MAX_NEARNESS_DISTANCE = 20;

    private com.github.davidmoten.rtree.RTree<Integer[], Geometry> tree;

    /**
     * Constructs and initializes an instance of {@link RTree}.
     */
    public RTree() {
        tree = com.github.davidmoten.rtree.RTree.create();
    }

    /**
     * Adds a node to the RTree.
     *
     * @param id     the id of the node to add
     * @param x      the absolute x position of the node
     * @param y      the absolute y position of the node
     * @param width  the absolute width of the node
     * @param height the absolute height of the node
     */
    public void addNode(final int id, final double x, final double y, final double width, final double height) {
        tree = tree.add(new Integer[] {id}, rectangle(x, y, x + width, y + height));
    }

    /**
     * Adds an edge to the RTree.
     *
     * @param fromNodeId the node id of the from node
     * @param toNodeId   the node id of the to node
     * @param fromX      the absolute right x position of the from node of the edge
     * @param fromY      the absolute y position of the from node of the edge
     * @param toX        the absolute x position of the to node of the edge
     * @param toY        the absolute y position of the to node of the edge
     */
    public void addEdge(final int fromNodeId, final int toNodeId, final double fromX, final double fromY,
                        final double toX, final double toY) {
        tree = tree.add(new Integer[] {fromNodeId, toNodeId}, line(fromX, fromY, toX, toY));
    }

    /**
     * Finds the closest node or within the specified maximum radius.
     *
     * @param x          the x position of the query point
     * @param y          the y position of the query point
     * @param nodeAction the action that needs to be executed when a node is found
     */
    public void find(final double x, final double y, final Consumer<Integer> nodeAction) {
        find(x, y, nodeAction, (a, b) -> {
            // We don't care about the edge.
        });
    }

    /**
     * Finds the closest node or edges within the specified maximum radius.
     *
     * @param x          the x position of the query point
     * @param y          the y position of the query point
     * @param nodeAction the action that needs to be executed when a node is found
     * @param edgeAction the action that needs to be executed when an edge is found
     */
    @SuppressWarnings("squid:S1166") // No need to log the exception itself, a message is enough.
    public void find(final double x, final double y,
                     final Consumer<Integer> nodeAction, final BiConsumer<Integer, Integer> edgeAction) {
        try {
            final Entry<Integer[], Geometry> result = tree.nearest(point(x, y), MAX_NEARNESS_DISTANCE, 1)
                    .toBlocking()
                    .first();

            if (result.geometry() instanceof Rectangle) {
                nodeAction.accept(result.value()[0]);
            } else if (result.geometry() instanceof Line) {
                edgeAction.accept(result.value()[0], result.value()[1]);
            }
        } catch (final NoSuchElementException e) {
            // There is no need to log the exception itself.
            LOGGER.debug("No node or edge found at position (" + x + ", " + y + ").");
        }
    }
}
