package org.dnacronym.hygene.graph.edge;

import org.dnacronym.hygene.graph.node.Node;

import java.util.Collection;
import java.util.LinkedHashSet;


/**
 * An aggregation of edges.
 */
public final class AggregateEdge extends Edge {
    private final Collection<Edge> edges;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from  the source of the edge
     * @param to    the destination of the edge
     * @param edges the edges to aggregate
     */
    public AggregateEdge(final Node from, final Node to, final Collection<Edge> edges) {
        super(from, to);

        this.edges = new LinkedHashSet<>(edges);
    }


    /**
     * Returns the aggregated edges.
     *
     * @return the aggregated edges
     */
    public Collection<Edge> getEdges() {
        return edges;
    }
}
