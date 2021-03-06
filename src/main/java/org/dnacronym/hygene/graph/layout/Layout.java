package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.Subgraph;


/**
 * A layout algorithm for a graph.
 */
@SuppressWarnings("squid:S1609") // Not a function
public interface Layout {
    /**
     * Lays out the nodes in the given {@link Subgraph} by setting their positions.
     *
     * @param subgraph a {@link Subgraph}
     */
    void layOut(Subgraph subgraph);
}
