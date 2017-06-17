package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.node.Node;


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
    void reduceCrossings(LayoutableNode[][] layers);
}
