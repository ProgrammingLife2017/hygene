package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;

import java.util.Collection;


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

        final Collection<NewNode> nodes = subgraph.getNodes();
        final NewNode[][] layers = getLayers(nodes);

        nodes.forEach(node -> {
            //
        });

        return new NewNode[0][];
    }


    //
    private NewNode[][] getLayers(final Collection<NewNode> nodes) {
        final int[] layerHeights = getHeights(nodes);
        final NewNode[][] layers = new NewNode[layerHeights.length][];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new NewNode[layerHeights[i]];
        }

        return layers;
    }

    //
    private void addToLayers(final Collection<NewNode> nodes, final NewNode[][] layers) {
        nodes.forEach(node -> {
            final int nodeStartLayer = positionToLayer(node.getXPosition());
            final int nodeEndLayer = positionToLayer(node.getXPosition() + node.getLength());

            for (int layer = nodeStartLayer; layer < nodeEndLayer; layer++) {
                addToLayerSomewhere(layers[layer], node);
            }

            node.getOutgoingEdges().forEach(edge -> {
                final NewNode neighbour = edge.getTo();
                final int neighbourStartLayer = positionToLayer(neighbour.getXPosition());

                for (int layer = nodeEndLayer + 1; layer < neighbourStartLayer; layer++) {
                    addToLayerSomewhere(layers[layer], neighbour);
                }
            });
        });
    }


    //
    private int positionToLayer(final int position) {
        return ((position + LAYER_WIDTH - 1) / LAYER_WIDTH) * LAYER_WIDTH;
    }

    //
    private int layerToPosition(final int column) {
        return column * LAYER_WIDTH;
    }

    //
    private int getLayerCount(final Collection<NewNode> nodes) {
        final int maxPosition = nodes.stream().map(NewNode::getXPosition).max(Integer::compare).get();
        return positionToLayer(maxPosition);
    }

    //
    private int[] getHeights(final Collection<NewNode> nodes) {
        final int layerCount = getLayerCount(nodes);
        final int[] heights = new int[layerCount];

        nodes.forEach(node -> {
            final int nodeStartLayer = positionToLayer(node.getXPosition());
            final int nodeEndLayer = positionToLayer(node.getXPosition() + node.getLength());

            for (int layer = nodeStartLayer; layer < nodeEndLayer; layer++) {
                heights[layer]++;
            }

            node.getOutgoingEdges().forEach(edge -> {
                final NewNode neighbour = edge.getTo();
                final int neighbourStartLayer = positionToLayer(neighbour.getXPosition());

                for (int layer = nodeEndLayer + 1; layer < neighbourStartLayer; layer++) {
                    heights[layer]++;
                }
            });
        });

        return heights;
    }

    //
    private void addToLayerSomewhere(final NewNode[] layer, final NewNode value) {
        for (int i = 0; i < layer.length; i++) {
            if (layer[i] == null) {
                layer[i] = value;
                return;
            }
        }

        throw new IllegalStateException("");
    }
}
