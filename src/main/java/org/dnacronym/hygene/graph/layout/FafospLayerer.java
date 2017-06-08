package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.DummyEdge;
import org.dnacronym.hygene.graph.DummyNode;
import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


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
        final NewNode[][] layers = createLayers(nodes);

        addToLayers(nodes, layers);

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
            "PMD.ConfusingTernary", // Checking for not null is not confusing
            "squid:S1188" // Cannot reduce lambda length without creating helper methods with large signatures
    })
    private void addToLayers(final Collection<NewNode> nodes, final NewNode[][] layers) {
        nodes.forEach(node -> {
            forEachLayer(node, layer -> addToLayerSomewhere(layers[layer], node));

            final Set<Edge> firstEdges = new HashSet<>();
            final Iterator<Edge> edges = node.getOutgoingEdges().iterator();
            while (edges.hasNext()) {
                final Edge edge = edges.next();
                final NewNode neighbour = edge.getTo();

                edges.remove();
                neighbour.getIncomingEdges().remove(edge);


                // Create dummy nodes
                final List<DummyNode> dummyNodes = new ArrayList<>();
                forEachLayer(edge, layer -> {
                    final DummyNode dummy = new DummyNode(node, neighbour);

                    dummyNodes.add(dummy);
                    addToLayerSomewhere(layers[layer], dummy);
                });


                // Insert edges
                final DummyEdge firstEdge = new DummyEdge(node, dummyNodes.get(0), edge);
                firstEdges.add(firstEdge); // Add it after the iterator to prevent concurrent modification
                dummyNodes.get(0).getIncomingEdges().add(firstEdge);

                final DummyNode lastNode = dummyNodes.get(dummyNodes.size() - 1);
                final DummyEdge lastEdge = new DummyEdge(lastNode, neighbour, edge);
                lastNode.getOutgoingEdges().add(lastEdge);
                neighbour.getIncomingEdges().add(lastEdge);

                for (int i = 0; i < dummyNodes.size() - 1; i++) {
                    final DummyEdge middleEdge = new DummyEdge(dummyNodes.get(i), dummyNodes.get(i + 1), edge);

                    dummyNodes.get(i).getOutgoingEdges().add(middleEdge);
                    dummyNodes.get(i + 1).getIncomingEdges().add(middleEdge);
                }
            }

            node.getOutgoingEdges().addAll(firstEdges);
        });
    }


    /*
     * Calculations
     */

    /**
     * Calculates the number of layers necessary for the given nodes.
     *
     * @param nodes a {@link Collection} of {@link NewNode}s
     * @return the number of layers necessary for the given nodes
     */
    @SuppressWarnings("squid:S3346") // False positive; assert doesn't have side effects
    private int calculateLayerCount(final Collection<NewNode> nodes) {
        assert !nodes.isEmpty();

        final int maxPosition = nodes.stream()
                .map(node -> node.getXPosition() + node.getLength())
                .max(Integer::compare)
                .orElseThrow(() -> new IllegalStateException("Non-empty collection has non maximum."));
        return positionToLayer(maxPosition);
    }

    /**
     * Calculates the number of {@link NewNode}s per layer.
     *
     * @param nodes a {@link Collection} of {@link NewNode}s
     * @return the number of {@link NewNode}s per layer
     */
    private int[] calculateHeights(final Collection<NewNode> nodes) {
        final int layerCount = calculateLayerCount(nodes);
        final int[] heights = new int[layerCount];

        nodes.forEach(node -> {
            forEachLayer(node, layer -> heights[layer]++);

            node.getOutgoingEdges().forEach(edge -> forEachLayer(edge, layer -> heights[layer]++));
        });

        return heights;
    }

    /**
     * Allocates and returns an array of layers into which the given nodes can be placed.
     *
     * @param nodes a {@link Collection} of {@link NewNode}s
     * @return an array of layers
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // That is exactly what this method should do
    private NewNode[][] createLayers(final Collection<NewNode> nodes) {
        final int[] layerHeights = calculateHeights(nodes);
        final NewNode[][] layers = new NewNode[layerHeights.length][];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new NewNode[layerHeights[i]];
        }

        return layers;
    }


    /*
     * Utility methods
     */

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
     * Executes the given {@link Consumer} for each layer in which the given {@link NewNode} is.
     *
     * @param node   a {@link NewNode}
     * @param action a {@link Consumer} for layer indices
     */
    private void forEachLayer(final NewNode node, final Consumer<Integer> action) {
        final int startLayer = positionToLayer(node.getXPosition());
        final int endLayer = positionToLayer(node.getXPosition() + node.getLength()) - 1;

        for (int layer = startLayer; layer <= endLayer; layer++) {
            action.accept(layer);
        }
    }

    /**
     * Executes the given {@link Consumer} for each layer the given {@link Edge} traverses.
     *
     * @param edge   an {@link Edge}
     * @param action a {@link Consumer} for layer indices
     */
    private void forEachLayer(final Edge edge, final Consumer<Integer> action) {
        final int startLayer = positionToLayer(edge.getFrom().getXPosition() + edge.getFrom().getLength());
        final int endLayer = positionToLayer(edge.getTo().getXPosition()) - 1;

        for (int layer = startLayer; layer <= endLayer; layer++) {
            action.accept(layer);
        }
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

        throw new IllegalStateException("Layer is full, calculateHeights method is erroneous.");
    }
}
