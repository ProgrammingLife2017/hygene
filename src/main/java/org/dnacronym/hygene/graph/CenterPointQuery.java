package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.events.CenterPointQueryChangeEvent;
import org.dnacronym.hygene.graph.layout.Layout;
import org.dnacronym.hygene.graph.layout.SugiyamaLayout;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.GraphIterator;
import org.dnacronym.hygene.models.NodeDistanceMap;
import org.dnacronym.hygene.models.SequenceDirection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Enables dynamic centre point queries for a {@link Graph}.
 * <p>
 * Query results are cached and can be used later.
 * <p>
 * This class functions lazily, which means that the cache is only approximately correct. While the intended nodes
 * will always be in the cache, the cache may contain more nodes. The excessive nodes are flushed from the cache by
 * rebuilding the cache after a number of calls.
 */
public final class CenterPointQuery {
    /**
     * The width of an edge.
     */
    // TODO Move this constant to the layout algorithm.
    private static final int EDGE_WIDTH = 1000;

    /**
     * The maximal acceptable difference between the preferred radius and the cached radius.
     */
    private static final int MAX_RADIUS_DIFFERENCE = 10;
    /**
     * The maximum cached radius before the cache is "too large".
     */
    private static final int MAX_RADIUS_FACTOR = 2;
    /**
     * The largest number by which the radius can be increased using the {@link #setRadius(int)} method before it is
     * deemed necessary to rebuild the cache.
     */
    private static final int MAX_SET_RADIUS_INCREASE = 5;
    //
    private static final Layout LAYOUT = new SugiyamaLayout();

    /**
     * The queried {@link Graph}.
     */
    private final Graph graph;
    /**
     * The iterator with which to iterate over the {@link Graph}.
     */
    private final GraphIterator iterator;
    /**
     * Maps {@link Graph} node ids to {@link NewNode} {@link UUID}s.
     */
    private final Map<Integer, UUID> nodes;
    /**
     * The backing cache.
     */
    private final Subgraph subgraph;
    /**
     * Maps each node id in the cache to the distance from the centre point of the query.
     */
    private final NodeDistanceMap distanceMap;

    /**
     * The node id of the centre node in the current query.
     */
    private int centre;
    /**
     * The maximum distance to the centre the nodes in the cache should have.
     */
    private int radius;
    /**
     * The maximum distance to the centre the nodes in the cache actually have.
     */
    private int cacheRadius;


    /**
     * Constructs a new {@link CenterPointQuery}.
     *
     * @param graph the {@link Graph} to query on
     */
    public CenterPointQuery(final Graph graph) {
        this.graph = graph;
        this.iterator = new GraphIterator(graph);
        this.nodes = new HashMap<>();
        this.subgraph = new Subgraph();
        this.distanceMap = new NodeDistanceMap();
    }


    /**
     * Returns the query's current centre node id.
     *
     * @return the query's current centre node id
     */
    public int getCentre() {
        return centre;
    }

    /**
     * Returns the query's current radius.
     *
     * @return the query's current radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Clears the cache and rebuilds the query.
     *
     * @param centre the centre of the query
     * @param radius the radius of the query
     */
    public void query(final int centre, final int radius) {
        if (centre < 0) {
            throw new IllegalArgumentException("Centre point node id cannot be negative.");
        }
        if (centre >= graph.getNodeArrays().length) {
            throw new IllegalArgumentException("Centre point node id cannot exceed graph size.");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must cannot be negative.");
        }

        this.centre = centre;
        this.radius = radius;
        this.cacheRadius = radius;

        clear();

        iterator.visitIndirectNeighboursWithinRange(centre, radius, (depth, nodeId) -> {
            if (nodeId == 0 || nodeId == graph.getNodeArrays().length - 1) {
                return;
            }

            distanceMap.setDistance(nodeId, depth);

            final NewNode node = new Segment(nodeId, graph.getLineNumber(nodeId), graph.getSequenceLength(nodeId));
            node.setXPosition(graph.getUnscaledXPosition(nodeId) + graph.getUnscaledXEdgeCount(nodeId) * EDGE_WIDTH);
            node.setYPosition(graph.getUnscaledYPosition(nodeId));
            nodes.put(nodeId, node.getUuid());
            subgraph.addNode(node);
        });
        iterator.visitIndirectNeighboursWithinRange(centre, radius - 1, (depth, nodeId) -> addEdges(nodeId));
        LAYOUT.layOut(subgraph);

        postEvent();
    }

    /**
     * Moves the centre to the given node.
     *
     * @param centre a node id
     */
    public void setCenter(final int centre) {
        if (centre < 0) {
            throw new IllegalArgumentException("Centre point node id cannot be negative.");
        }
        if (centre >= graph.getNodeArrays().length) {
            throw new IllegalArgumentException("Centre point node id cannot exceed graph size.");
        }

        this.centre = centre;
        fixCentre();

        postEvent();
    }

    /**
     * Sets the radius.
     * <p>
     * If the new radius value is below zero, the radius will be set to 0.
     *
     * @param radius the new radius
     */
    public void setRadius(final int radius) {
        if (radius > this.radius) {
            incrementRadius(radius - this.radius);
        } else if (radius < this.radius) {
            decrementRadius(this.radius - Math.max(0, radius));
        }
    }

    /**
     * Returns the backing {@link Subgraph} cache.
     *
     * @return the backing {@link Subgraph} cache.
     */
    public Subgraph getCache() {
        return subgraph;
    }


    /*
     * Query methods
     */

    /**
     * Ensures that the query on the cached centre and radius are a superset of the preferred centre and radius.
     */
    private void fixCentre() {
        final Integer centreDistance = distanceMap.getDistance(centre);
        if (centreDistance == null || cacheRadius >= radius * MAX_RADIUS_FACTOR) {
            query(centre, radius);
            return;
        }

        int offset = radius - (cacheRadius - centreDistance);
        while (offset > 0) {
            incrementCacheRadius();
            offset--;
        }
    }

    /**
     * Increments the cached radius and adds the nodes that are now within range.
     */
    private void incrementCacheRadius() {
        cacheRadius++;

        distanceMap.getNodes(cacheRadius - 1).forEach(nodeId -> {
            iterator.visitDirectNeighbours(nodeId, neighbour -> distanceMap.setDistance(neighbour, cacheRadius));
            addEdges(nodeId);
        });
        LAYOUT.layOut(subgraph);
    }

    /**
     * Increments the radius by the specified amount.
     *
     * @param increment the amount by which to increment the radius
     */
    private void incrementRadius(final int increment) {
        final int newRadius = radius + increment;
        assert newRadius >= radius;

        final Integer centreDistance = distanceMap.getDistance(centre);
        if (centreDistance == null) {
            // This branch should be unreachable, but is required by the Checker Framework
            query(centre, newRadius);
            return;
        }

        final int effectiveCacheRadius = cacheRadius - centreDistance;
        if (newRadius <= effectiveCacheRadius) {
            this.radius = newRadius;
        } else if (newRadius - effectiveCacheRadius > MAX_SET_RADIUS_INCREASE) {
            query(centre, newRadius);
        } else {
            final int cacheRadiusTarget = newRadius + effectiveCacheRadius;
            while (cacheRadius < cacheRadiusTarget) {
                incrementCacheRadius();
            }

            this.radius = newRadius;
        }
    }

    /**
     * Decrements the radius by the specified amount.
     *
     * @param decrement the amount by which to decrement the radius
     */
    private void decrementRadius(final int decrement) {
        final int newRadius = radius - decrement;
        assert newRadius <= radius;
        assert newRadius >= 0;

        if (cacheRadius - newRadius <= MAX_RADIUS_DIFFERENCE) {
            this.radius = newRadius;
        } else {
            query(centre, newRadius);
        }
    }


    /*
     * Helper methods
     */

    /**
     * Empties the {@link CenterPointQuery}.
     */
    private void clear() {
        nodes.clear();
        subgraph.clear();
        distanceMap.clear();
    }

    /**
     * Returns the {@link NewNode} with the given id, or {@code null} if there is no such node in the cache.
     *
     * @param nodeId a node id, as given by the {@link Graph}
     * @return the {@link NewNode} with the given id, or {@code null} if there is no such node in the cache.
     */
    private @Nullable NewNode getNode(final int nodeId) {
        final UUID uuid = nodes.get(nodeId);
        if (uuid == null) {
            return null;
        }

        return subgraph.getNode(uuid);
    }

    /**
     * Adds all edges from the specified node to the corresponding {@link NewNode} if both ends of the edge are in the
     * cache.
     *
     * @param nodeId a node id, as given by the {@link Graph}
     */
    private void addEdges(final int nodeId) {
        final NewNode node = getNode(nodeId);
        if (node == null) {
            return;
        }

        iterator.visitDirectNeighbours(nodeId, SequenceDirection.RIGHT, neighbourId -> {
            final NewNode neighbour = getNode(neighbourId);
            if (node == null || neighbour == null) {
                return;
            }

            final Edge edge = new Edge(node, neighbour);
            node.getOutgoingEdges().add(edge);
            neighbour.getIncomingEdges().add(edge);
        });
        iterator.visitDirectNeighbours(nodeId, SequenceDirection.LEFT, neighbourId -> {
            final NewNode neighbour = getNode(neighbourId);
            if (node == null || neighbour == null) {
                return;
            }

            final Edge edge = new Edge(neighbour, node);
            node.getIncomingEdges().add(edge);
            neighbour.getOutgoingEdges().add(edge);
        });
    }

    /**
     * Posts event indicating a change in the center point query to the event bus.
     */
    private void postEvent() {
        HygeneEventBus.getInstance().post(new CenterPointQueryChangeEvent(this));
    }
}
