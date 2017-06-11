package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.DummyEdge;
import org.dnacronym.hygene.graph.DummyNode;
import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
@SuppressWarnings("PMD.TooManyMethods") // Unfeasible to refactor to multiple classes
public final class FafospLayerer implements SugiyamaLayerer {
    private static final int LAYER_WIDTH = 1000;


    @Override
    public NewNode[][] layer(final Subgraph subgraph) {
        if (subgraph.getNodes().isEmpty()) {
            return new NewNode[0][];
        }

        final NewNode[][] layers = createLayers(subgraph);

        addToLayers(subgraph, layers);

        return layers;
    }

    /**
     * Allocates and returns an array of layers into which the given nodes can be placed.
     *
     * @param subgraph a {@link Subgraph}
     * @return an array of layers
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // That is exactly what this method should do
    private NewNode[][] createLayers(final Subgraph subgraph) {
        final int[] layerHeights = calculateHeights(subgraph.getNodes());
        final NewNode[][] layers = new NewNode[layerHeights.length][];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new NewNode[layerHeights[i]];
        }

        return layers;
    }

    /**
     * Places the given nodes into the proper layers.
     *
     * @param subgraph a {@link Subgraph}
     * @param layers an array of layers
     */
    private void addToLayers(final Subgraph subgraph, final NewNode[][] layers) {
        final Set<NewNode> addNodeLater = new HashSet<>();

        subgraph.getNodes().forEach(node -> {
            forEachLayer(node, layer -> addToLayerSomewhere(layers[layer], node));

            final Set<Edge> addEdgeLater = new HashSet<>();
            final Set<Edge> removeEdgeLater = new HashSet<>();

            node.getOutgoingEdges().forEach(edge -> {
                if (layerSize(edge) < 0) {
                    return;
                }

                edge.getTo().getIncomingEdges().remove(edge);
                removeEdgeLater.add(edge);

                final List<DummyNode> dummyNodes = createDummyNodes(layers, edge);
                addNodeLater.addAll(dummyNodes);

                final Edge firstEdge = connectDummies(edge, dummyNodes);
                addEdgeLater.add(firstEdge);
            });

            node.getOutgoingEdges().addAll(addEdgeLater);
            node.getOutgoingEdges().removeAll(removeEdgeLater);
        });

        subgraph.addAll(addNodeLater);
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

        final long maxPosition = nodes.stream()
                .map(node -> node.getXPosition() + node.getLength())
                .max(Long::compare)
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
     * Creates a collection of unconnected {@link DummyNode}s, adds them to the correct layer, and sets their
     * horizontal position.
     *
     * @param layers an array of layers
     * @param edge   the {@link Edge} to replace with {@link DummyNode}s
     * @return a collection of unconnected {@link DummyNode}s
     */
    private List<DummyNode> createDummyNodes(final NewNode[][] layers, final Edge edge) {
        final List<DummyNode> dummyNodes = new ArrayList<>();

        forEachLayer(edge, layer -> {
            final DummyNode dummy = new DummyNode(edge.getFrom(), edge.getTo());
            dummy.setXPosition(layer * LAYER_WIDTH);

            dummyNodes.add(dummy);
            addToLayerSomewhere(layers[layer], dummy);
        });

        return dummyNodes;
    }

    /**
     * Connects the given {@link DummyNode}s with {@link DummyEdge}s and returns the first edge.
     * <p>
     * The returned {@link DummyEdge} is not added to the {@link NewNode} from which it departs, because this might
     * result in a {@link java.util.ConcurrentModificationException}.
     *
     * @param edge       the original {@link Edge} that was replaced with {@link DummyNode}s
     * @param dummyNodes the {@link DummyNode}s that replaced the given {@link Edge}
     * @return the first of the added {@link DummyEdge}s
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Cannot be avoided
    private Edge connectDummies(final Edge edge, final List<DummyNode> dummyNodes) {
        final Edge firstEdge = new DummyEdge(edge.getFrom(), dummyNodes.get(0), edge);
        // `firstEdge` should be added to `edge.getFrom()` by caller to prevent concurrent modification
        dummyNodes.get(0).getIncomingEdges().add(firstEdge);

        final NewNode lastNode = dummyNodes.get(dummyNodes.size() - 1);
        final Edge lastEdge = new DummyEdge(lastNode, edge.getTo(), edge);
        lastNode.getOutgoingEdges().add(lastEdge);
        edge.getTo().getIncomingEdges().add(lastEdge);

        for (int i = 0; i < dummyNodes.size() - 1; i++) {
            final Edge middleEdge = new DummyEdge(dummyNodes.get(i), dummyNodes.get(i + 1), edge);

            dummyNodes.get(i).getOutgoingEdges().add(middleEdge);
            dummyNodes.get(i + 1).getIncomingEdges().add(middleEdge);
        }

        return firstEdge;
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
    private int positionToLayer(final long position) {
        assert position >= 0;
        return (int) ((position + LAYER_WIDTH - 1) / LAYER_WIDTH);
    }

    /**
     * Returns the number of layers the given {@link Edge} traverses.
     *
     * @param edge an {@link Edge}
     * @return the number of layers the given {@link Edge} traverses
     */
    private int layerSize(final Edge edge) {
        final int startLayer = positionToLayer(edge.getFrom().getXPosition() + edge.getFrom().getLength());
        final int endLayer = positionToLayer(edge.getTo().getXPosition()) - 1;

        return endLayer - startLayer;
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
