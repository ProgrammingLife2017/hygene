package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;


/**
 * Part of the {@link SugiyamaLayout} algorithm, layers a {@link Subgraph} into a number of layers.
 */
@SuppressWarnings("squid:S1609") // Not a function
public interface SugiyamaLayerer {
    int LAYER_WIDTH = 1000;


    /**
     * Layers the given {@link Subgraph} into a number of layers.
     *
     * @param subgraph a {@link Subgraph}
     * @return an array of layers
     */
    NewNode[][] layer(Subgraph subgraph);
}
