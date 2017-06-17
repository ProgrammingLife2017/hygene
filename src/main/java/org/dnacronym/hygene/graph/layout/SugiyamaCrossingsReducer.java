package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.node.NewNode;


/**
 * Part of the {@link SugiyamaLayout} algorithm, reduces the number of edge crossings in a layout.
 */
@SuppressWarnings("squid:S1609") // Not a function
public interface SugiyamaCrossingsReducer {
    /**
     * Reduces the number of edge crossings in the given layers.
     *
     * @param layers an array of layers
     */
    void reduceCrossings(NewNode[][] layers);
}
