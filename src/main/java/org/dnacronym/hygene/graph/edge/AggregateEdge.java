package org.dnacronym.hygene.graph.edge;

import org.dnacronym.hygene.graph.node.GfaNode;

import java.util.Collection;
import java.util.LinkedHashSet;


/**
 * An aggregation of edges.
 */
public final class AggregateEdge extends Edge {
    private final GfaNode from;
    private final GfaNode to;
    private final Collection<Edge> edges;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from  the source of the edge
     * @param to    the destination of the edge
     * @param edges the edges to aggregate
     */
    public AggregateEdge(final GfaNode from, final GfaNode to, final Collection<Edge> edges) {
        super(from, to);

        this.from = from;
        this.to = to;
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

    @Override
    public GfaNode getFromSegment() {
        return from;
    }

    @Override
    public GfaNode getToSegment() {
        return to;
    }
}
