package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.Node;
import org.dnacronym.hygene.graph.Subgraph;


/**
 * Part of the {@link SugiyamaLayout} algorithm, layers a {@link Subgraph} into a number of layers.
 */
public interface SugiyamaLayerer {
    /**
     * Layers the given {@link Subgraph} into a number of layers.
     *
     * @param subgraph a {@link Subgraph}
     * @return an array of layers
     */
    Node[][] layer(Subgraph subgraph);
}
