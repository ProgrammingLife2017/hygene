package org.dnacronym.hygene.graph;

import java.util.HashSet;
import java.util.Set;


/**
 * Class representing a collection of nodes and edges.
 * <p>
 * These nodes and edges do not have to form a connected graph. In fact, the edges even do not have to be connected to
 * any of the nodes in the collection.
 */
public final class GraphSelection {
    private final Set<NewNode> nodes;
    private final Set<Edge> edges;


    /**
     * Constructs a new {@link GraphSelection} instance with the given nodes and edges.
     *
     * @param nodes the nodes of this collection
     * @param edges the edges of this collection
     */
    public GraphSelection(final Set<NewNode> nodes, final Set<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * Constructs a new {@link GraphSelection} with empty node and edge sets.
     */
    public GraphSelection() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }


    /**
     * Returns the nodes.
     *
     * @return the nodes
     */
    public Set<NewNode> getNodes() {
        return nodes;
    }

    /**
     * Returns the edges.
     *
     * @return the edges
     */
    public Set<Edge> getEdges() {
        return edges;
    }
}
