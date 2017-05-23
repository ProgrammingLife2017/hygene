package org.dnacronym.hygene.models;

import java.util.function.Consumer;


/**
 * Enables dynamic centre point queries for a {@link Graph}.
 * <p>
 * Query results are cached and can be used later.
 */
public final class GraphQuery {
    /**
     * The queried {@link Graph}.
     */
    private final Graph graph;
    /**
     * Maps each node in the cache to the distance from the centre point of the query.
     */
    private final NodeDistanceMap current;

    private int radius;
    private int centre;


    /**
     * Constructs a new {@link GraphQuery}.
     *
     * @param graph the {@link Graph} to query on
     */
    public GraphQuery(final Graph graph) {
        this.graph = graph;
        this.current = new NodeDistanceMap();
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

        this.radius = radius;
        this.centre = centre;

        current.clear();
        graph.iterator().visitIndirectNeighboursWithinRange(centre, radius,
                (depth, node) -> current.setDistance(node, depth));
    }

    /**
     * Increases the radius by one.
     */
    public void incrementRadius() {
        query(centre, radius + 1);
    }

    /**
     * Decreases the radius by one.
     */
    public void decrementRadius() {
        query(centre, radius - 1);
    }

    /**
     * Increases the centre node by one, i.e. moves it to the right.
     */
    public void incrementCentre() {
        query(centre + 1, radius);
    }

    /**
     * Decreases the centre node by one, i.e. moves it to the left.
     */
    public void decrementCentre() {
        query(centre - 1, radius);
    }

    /**
     * Visits all nodes in the cache and applies the given {@link Consumer} to them.
     *
     * @param action a {@link Consumer} for node ids
     */
    public void visit(final Consumer<Integer> action) {
        current.keySet().forEach(action::accept);
    }
}
