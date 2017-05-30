package org.dnacronym.hygene.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;


/**
 * Enables dynamic centre point queries for a {@link Graph}.
 * <p>
 * Query results are cached and can be used later.
 * <p>
 * This class functions lazily, which means that the cache is only approximately correct. While the intended nodes
 * will always be in the cache, the cache may contain more nodes. The excessive nodes are flushed from the cache by
 * rebuilding the cache after a number of calls.
 */
public final class GraphQuery {
    /**
     * The maximal acceptable difference between the preferred radius and the cached radius.
     */
    private static final int MAX_RADIUS_DIFFERENCE = 10;
    /**
     * The maximum cached radius before the cached is "too large".
     */
    private static final int MAX_RADIUS_FACTOR = 2;

    /**
     * The queried {@link Graph}.
     */
    private final Graph graph;
    /**
     * The iterator with which to iterate over the {@link Graph}.
     */
    private final GraphIterator iterator;
    /**
     * Maps each node in the cache to the distance from the centre point of the query.
     */
    private final NodeDistanceMap cache;
    /**
     * Lists all nodes in the cache of which all left neighbours are not in the cache.
     */
    private final List<Integer> sourceNeighbours;
    /**
     * Lists all nodes in the cache of which all right neighbours are not in the cache.
     */
    private final List<Integer> sinkNeighbours;

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
     * Constructs a new {@link GraphQuery}.
     *
     * @param graph the {@link Graph} to query on
     */
    public GraphQuery(final Graph graph) {
        this.graph = graph;
        this.iterator = new GraphIterator(graph);
        this.cache = new NodeDistanceMap();

        this.sourceNeighbours = new ArrayList<>();
        this.sinkNeighbours = new ArrayList<>();
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

        cache.clear();
        iterator.visitIndirectNeighboursWithinRange(centre, radius, (depth, node) -> cache.setDistance(node, depth));
        findSentinelNeighbours();
    }

    /**
     * Moves the centre to the given node.
     *
     * @param centre a node id
     */
    public void moveTo(final int centre) {
        if (centre < 0) {
            throw new IllegalArgumentException("Centre point node id cannot be negative.");
        }
        if (centre >= graph.getNodeArrays().length) {
            throw new IllegalArgumentException("Centre point node id cannot exceed graph size.");
        }

        this.centre = centre;
        fixCentre();
    }

    /**
     * Increases the radius by one.
     */
    public void incrementRadius() {
        radius++;

        final Integer centreDistance = cache.getDistance(centre);
        if (centreDistance == null) {
            // This branch should be unreachable, but is required by the Checker Framework
            query(centre, radius);
            return;
        }
        if (radius <= cacheRadius - centreDistance) {
            return;
        }

        incrementCacheRadius();
    }

    /**
     * Decreases the radius by one.
     * <p>
     * An actual update is deferred because it would be a waste to throw away the outer layer.
     */
    public void decrementRadius() {
        if (radius <= 0) {
            return;
        }

        radius--;
        if (cacheRadius - radius > MAX_RADIUS_DIFFERENCE) {
            query(centre, radius);
        }
    }

    /**
     * Visits all nodes in the cache and applies the given {@link Consumer} to them.
     *
     * @param action a {@link Consumer} for node ids
     */
    public void visit(final Consumer<Integer> action) {
        cache.keySet().forEach(action::accept);
    }

    /**
     * Visits all nodes in the cache in breadth-first order and applies the given {@link Consumer} to them.
     *
     * @param direction the direction to visit in
     * @param action    a {@link Consumer} for node ids
     */
    public void visitBFS(final SequenceDirection direction, final Consumer<Integer> action) {
        final Queue<Integer> queue = new LinkedList<>();
        queue.addAll(direction.ternary(sinkNeighbours, sourceNeighbours));

        final boolean[] visited = new boolean[graph.getNodeArrays().length];
        while (!queue.isEmpty()) {
            final int head = queue.remove();
            if (visited[head]) {
                continue;
            }

            action.accept(head);
            visited[head] = true;

            iterator.visitDirectNeighbours(head, direction, index -> {
                if (!visited[index] && cache.containsNode(index)) {
                    queue.add(index);
                }
            });
        }
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
     * Ensures that the query on the cached centre and radius are a superset of the preferred centre and radius.
     */
    private void fixCentre() {
        final Integer centreDistance = cache.getDistance(centre);
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
        cache.getNodes(cacheRadius - 1).forEach(node ->
                iterator.visitDirectNeighbours(node, neighbour -> cache.setDistance(neighbour, cacheRadius)));
        findSentinelNeighbours();
    }

    /**
     * Finds the neighbours of the (virtual) source and sink nodes, and puts them in the respective {@link List}s.
     */
    private void findSentinelNeighbours() {
        sourceNeighbours.clear();
        sinkNeighbours.clear();

        visit(node -> {
            final boolean[] hasNeighbours = {false, false};

            iterator.visitDirectNeighboursWhile(node, SequenceDirection.LEFT,
                    neighbour -> !cache.containsNode(neighbour),
                    neighbour -> hasNeighbours[0] = true,
                    ignored -> {
                    }
            );
            iterator.visitDirectNeighboursWhile(node, SequenceDirection.RIGHT,
                    neighbour -> !cache.containsNode(neighbour),
                    neighbour -> hasNeighbours[1] = true,
                    ignored -> {
                    }
            );

            if (!hasNeighbours[0]) {
                sourceNeighbours.add(node);
            }
            if (!hasNeighbours[1]) {
                sinkNeighbours.add(node);
            }
        });
    }
}
