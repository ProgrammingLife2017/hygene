package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;


/**
 * Part of the {@link SugiyamaLayout} algorithm, layers a {@link Subgraph} into a number of layers using the FAFOSP-X
 * algorithm.
 * <p>
 * FAFOSP stands for "Felix Algorithm For Optimal Segment Positioning", and is a now-obsolete algorithm for laying
 * out graphs. Its algorithm for calculating horizontal layouts, FAFOSP-X, is still useful, however.
 */
public final class FafospLayerer implements SugiyamaLayerer {
    private static final int LAYER_WIDTH = 1000;

    // Checklist:
    // 1 Align all elements to horizontal layers
    // 2 Create layers
    // 3 Determine layer heights
    // 4 Assign nodes to layers and add dummy nodes

    @Override
    public NewNode[][] layer(final Subgraph subgraph) {
        if (subgraph.getNodes().size() == 0) {
            return null;
        }

        return new NewNode[0][];
    }


    //
    private int getLayerCount(final Subgraph subgraph) {
        final int maxPosition = subgraph.getNodes().stream().map(NewNode::getXPosition).max(Integer::compare).get();
        return positionToLayer(maxPosition);
    }

    //
    private int positionToLayer(final int position) {
        return ((position + LAYER_WIDTH - 1) / LAYER_WIDTH) * LAYER_WIDTH;
    }

    //
    private int layerToPosition(final int column) {
        return column * LAYER_WIDTH;
    }
}
