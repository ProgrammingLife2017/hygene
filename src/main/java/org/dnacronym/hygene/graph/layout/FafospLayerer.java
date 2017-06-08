package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.DummyEdge;
import org.dnacronym.hygene.graph.DummyNode;
import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Part of the {@link SugiyamaLayout} algorithm, layers a {@link Subgraph} into a number of layers using the FAFOSP-X
 * algorithm.
 * <p>
 * FAFOSP stands for "Felix Algorithm For Optimal Segment Positioning", and is a now-obsolete algorithm for laying
 * out graphs. Its algorithm for calculating horizontal layouts, FAFOSP-X, is still useful, however.
 */
public final class FafospLayerer implements SugiyamaLayerer {
    private static final int LAYER_WIDTH = 1000;


    @Override
    public NewNode[][] layer(final Subgraph subgraph) {
        if (subgraph.getNodes().isEmpty()) {
            return new NewNode[0][];
        }

        final Collection<NewNode> nodes = subgraph.getNodes();
        final NewNode[][] layers = getLayers(nodes);

        addToLayers(nodes, layers);

        return layers;
    }


    /**
     * Allocates and returns an array of layers into which the given nodes can be placed.
     *
     * @param nodes a {@link Collection} of {@link NewNode}s
     * @return an array of layers
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // That is exactly what this method should do
    private NewNode[][] getLayers(final Collection<NewNode> nodes) {
        final int[] layerHeights = getHeights(nodes);
        final NewNode[][] layers = new NewNode[layerHeights.length][];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new NewNode[layerHeights[i]];
        }

        return layers;
    }

    /**
     * Places the given nodes into the proper layers.
     *
     * @param nodes  a {@link Collection} of {@link NewNode}s
     * @param layers an array of layers
     */
    @SuppressWarnings({
            "PMD.AvoidInstantiatingObjectsInLoops", // That is exactly what this method should do
            "PMD.ConfusingTernary" // Checking for not null is not confusing
    })
    private void addToLayers(final Collection<NewNode> nodes, final NewNode[][] layers) {
        nodes.forEach(node -> {
            final int nodeStartLayer = positionToLayer(node.getXPosition());
            final int nodeEndLayer = positionToLayer(node.getXPosition() + node.getLength());

            for (int layer = nodeStartLayer; layer < nodeEndLayer; layer++) {
                addToLayerSomewhere(layers[layer], node);
            }

            final Set<Edge> toAdd = new HashSet<>();
            final Iterator<Edge> edges = node.getOutgoingEdges().iterator();
            while (edges.hasNext()) {
                final Edge edge = edges.next();
                final NewNode neighbour = edge.getTo();
                final int neighbourStartLayer = positionToLayer(neighbour.getXPosition());

                final int minLayer = nodeEndLayer;
                final int maxLayer = neighbourStartLayer - 1;
                assert maxLayer >= minLayer;

                neighbour.getIncomingEdges().remove(edge);
                edges.remove();

                DummyNode previousDummy = null;
                for (int layer = minLayer; layer <= maxLayer; layer++) {
                    final DummyNode dummy = new DummyNode(node, neighbour);
                    addToLayerSomewhere(layers[layer], dummy);

                    final DummyEdge incoming;
                    final DummyEdge outgoing;
                    if (layer == minLayer) {
                        incoming = new DummyEdge(node, dummy, edge);
                    } else if (previousDummy != null) {
                        incoming = new DummyEdge(previousDummy, dummy, edge);
                    } else {
                        throw new IllegalStateException("previousDummy is null but current dummy is not first dummy");
                    }
                    if (layer == maxLayer) {
                        outgoing = new DummyEdge(dummy, neighbour, edge);
                    } else {
                        outgoing = null;
                    }

                    if (incoming.getFrom().equals(node)) {
                        toAdd.add(incoming);
                    } else {
                        incoming.getFrom().getOutgoingEdges().add(incoming);
                    }
                    incoming.getTo().getIncomingEdges().add(incoming);
                    if (outgoing != null) {
                        outgoing.getFrom().getOutgoingEdges().add(outgoing);
                        outgoing.getTo().getIncomingEdges().add(outgoing);
                    }

                    previousDummy = dummy;
                }
            }

            node.getOutgoingEdges().addAll(toAdd);
        });
    }


    /**
     * Returns the number of the layer the given position would be in.
     *
     * @param position a position
     * @return the number of the layer the given position would be in
     */
    private int positionToLayer(final int position) {
        assert position >= 0;
        return (position + LAYER_WIDTH - 1) / LAYER_WIDTH;
    }

    /**
     * Returns the number of layers necessary for the given nodes.
     *
     * @param nodes a {@link Collection} of {@link NewNode}s
     * @return the number of layers necessary for the given nodes
     */
    @SuppressWarnings("squid:S3346") // False positive; assert doesn't have side effects
    private int getLayerCount(final Collection<NewNode> nodes) {
        assert !nodes.isEmpty();

        final int maxPosition = nodes.stream()
                .map(node -> node.getXPosition() + node.getLength())
                .max(Integer::compare)
                .orElseThrow(() -> new IllegalStateException("Non-empty collection has non maximum."));
        return positionToLayer(maxPosition);
    }

    /**
     * Returns the number of {@link NewNode}s per layer.
     *
     * @param nodes a {@link Collection} of {@link NewNode}s
     * @return the number of {@link NewNode}s per layer
     */
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

                final int minLayer = nodeEndLayer;
                final int maxLayer = neighbourStartLayer - 1;
                for (int layer = minLayer; layer <= maxLayer; layer++) {
                    heights[layer]++;
                }
            });
        });

        return heights;
    }

    /**
     * Adds the given {@link NewNode} somewhere in the given layer.
     *
     * @param layer a layer
     * @param node  a {@link NewNode}
     */
    private void addToLayerSomewhere(final NewNode[] layer, final NewNode node) {
        for (int i = 0; i < layer.length; i++) {
            if (layer[i] == null) {
                layer[i] = node;
                return;
            }
        }

        throw new IllegalStateException("Layer is full, getHeights method is erroneous.");
    }
}
