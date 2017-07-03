package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.Subgraph;
import org.dnacronym.hygene.graph.node.Node;

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
 * FAFOSP stands for "F's Algorithm For Optimal Segment Positioning", and is a now-obsolete algorithm for laying
 * out graphs. Its algorithm for calculating horizontal layouts, FAFOSP-X, is still useful, however.
 */
@SuppressWarnings("PMD.TooManyMethods") // Unfeasible to refactor to multiple classes
public final class FafospLayerer implements SugiyamaLayerer {
    public static final int LAYER_WIDTH = 1000;


    @Override
    public LayoutableNode[][] layer(final Subgraph subgraph) {
        if (subgraph.getNodes().isEmpty()) {
            return new LayoutableNode[0][];
        }

        final LayererData data = new LayererData(subgraph);
        final LayoutableNode[][] layers = createLayers(data);

        addToLayers(data, layers);

        return layers;
    }

    /**
     * Allocates and returns an array of layers into which the given nodes can be placed.
     *
     * @param data the {@link LayererData}
     * @return an array of layers
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // That is exactly what this method should do
    private LayoutableNode[][] createLayers(final LayererData data) {
        final int[] layerHeights = calculateHeights(data);
        final LayoutableNode[][] layers = new LayoutableNode[layerHeights.length][];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new LayoutableNode[layerHeights[i]];
        }

        return layers;
    }

    /**
     * Places the given nodes into the proper layers.
     *
     * @param data   the {@link LayererData}
     * @param layers an array of layers
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // That is exactly what this method should do
    private void addToLayers(final LayererData data, final LayoutableNode[][] layers) {
        final Set<Node> addLayoutableNodeLater = new HashSet<>();

        for (final LayoutableNode node : data.nodes) {
            if (Thread.interrupted()) {
                return;
            }

            forEachLayer(data, node, layer -> addToLayerSomewhere(layers[layer], node));

            final Set<Edge> addEdgeLater = new HashSet<>();
            final Set<Edge> removeEdgeLater = new HashSet<>();

            node.getOutgoingEdges().forEach(edge -> {
                if (layerSize(data, edge) < 0) {
                    return;
                }

                edge.getTo().getIncomingEdges().remove(edge);
                removeEdgeLater.add(edge);

                final List<DummyNode> dummyLayoutableNodes = createDummyNodes(data, layers, edge);
                addLayoutableNodeLater.addAll(dummyLayoutableNodes);

                final Edge firstEdge = connectDummies(edge, dummyLayoutableNodes);
                addEdgeLater.add(firstEdge);
            });

            node.getOutgoingEdges().addAll(addEdgeLater);
            node.getOutgoingEdges().removeAll(removeEdgeLater);
        }

        data.subgraph.addAll(addLayoutableNodeLater);
    }


    /*
     * Calculations
     */

    /**
     * Calculates the number of layers necessary for the given nodes.
     *
     * @param data the {@link LayererData}
     * @return the number of layers necessary for the given nodes
     */
    @SuppressWarnings("squid:S3346") // False positive; assert doesn't have side effects
    private int calculateLayerCount(final LayererData data) {
        assert !data.nodes.isEmpty();

        final long maxPosition = data.nodes.stream()
                .map(node -> node.getXPosition() + node.getLength())
                .max(Long::compare)
                .orElseThrow(() -> new IllegalStateException("Non-empty collection has no maximum."));
        return data.positionToLayer(maxPosition);
    }

    /**
     * Calculates the number of {@link LayoutableNode}s per layer.
     *
     * @param data the {@link LayererData}
     * @return the number of {@link LayoutableNode}s per layer
     */
    private int[] calculateHeights(final LayererData data) {
        final int layerCount = calculateLayerCount(data);
        final int[] heights = new int[layerCount];

        data.nodes.forEach(node -> {
            forEachLayer(data, node, layer -> heights[layer]++);

            node.getOutgoingEdges().forEach(edge -> forEachLayer(data, edge, layer -> heights[layer]++));
        });

        return heights;
    }

    /**
     * Creates a collection of unconnected {@link DummyNode}s, adds them to the correct layer, and sets their
     * horizontal position.
     *
     * @param data   the {@link LayererData}
     * @param layers an array of layers
     * @param edge   the {@link Edge} to replace with {@link DummyNode}s
     * @return a collection of unconnected {@link DummyNode}s
     */
    private List<DummyNode> createDummyNodes(final LayererData data, final LayoutableNode[][] layers, final Edge edge) {
        final List<DummyNode> dummyLayoutableNodes = new ArrayList<>();

        forEachLayer(data, edge, layer -> {
            final DummyNode dummy = new DummyNode(edge.getFrom(), edge.getTo());
            dummy.setXPosition((long) (layer + data.minLayer) * LAYER_WIDTH);

            dummyLayoutableNodes.add(dummy);
            addToLayerSomewhere(layers[layer], dummy);
        });

        return dummyLayoutableNodes;
    }

    /**
     * Connects the given {@link DummyNode}s with {@link DummyEdge}s and returns the first edge.
     * <p>
     * The returned {@link DummyEdge} is not added to the {@link LayoutableNode} from which it departs, because this
     * might result in a {@link java.util.ConcurrentModificationException}.
     *
     * @param edge                 the original {@link Edge} that was replaced with {@link DummyNode}s
     * @param dummyLayoutableNodes the {@link DummyNode}s that replaced the given {@link Edge}
     * @return the first of the added {@link DummyEdge}s
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Cannot be avoided
    private Edge connectDummies(final Edge edge, final List<DummyNode> dummyLayoutableNodes) {
        final Edge firstEdge = new DummyEdge(edge.getFrom(), dummyLayoutableNodes.get(0), edge);
        // `firstEdge` should be added to `edge.getFrom()` by caller to prevent concurrent modification
        dummyLayoutableNodes.get(0).getIncomingEdges().add(firstEdge);

        final LayoutableNode lastLayoutableNode = dummyLayoutableNodes.get(dummyLayoutableNodes.size() - 1);
        final Edge lastEdge = new DummyEdge((Node) lastLayoutableNode, edge.getTo(), edge);
        lastLayoutableNode.getOutgoingEdges().add(lastEdge);
        edge.getTo().getIncomingEdges().add(lastEdge);

        for (int i = 0; i < dummyLayoutableNodes.size() - 1; i++) {
            final Edge middleEdge = new DummyEdge(dummyLayoutableNodes.get(i), dummyLayoutableNodes.get(i + 1), edge);

            dummyLayoutableNodes.get(i).getOutgoingEdges().add(middleEdge);
            dummyLayoutableNodes.get(i + 1).getIncomingEdges().add(middleEdge);
        }

        return firstEdge;
    }


    /*
     * Utility methods
     */

    /**
     * Returns the number of layers the given {@link Edge} traverses.
     *
     * @param data the {@link LayererData}
     * @param edge an {@link Edge}
     * @return the number of layers the given {@link Edge} traverses
     */
    private int layerSize(final LayererData data, final Edge edge) {
        final int startLayer = data.positionToLayer(edge.getFrom().getXPosition() + edge.getFrom().getLength());
        final int endLayer = data.positionToLayer(edge.getTo().getXPosition()) - 1;

        return endLayer - startLayer;
    }

    /**
     * Executes the given {@link Consumer} for each layer in which the given {@link LayoutableNode} is.
     *
     * @param data   the {@link LayererData}
     * @param node   a {@link LayoutableNode}
     * @param action a {@link Consumer} for layer indices
     */
    private void forEachLayer(final LayererData data, final LayoutableNode node, final Consumer<Integer> action) {
        final int startLayer = data.positionToLayer(node.getXPosition());
        final int endLayer = data.positionToLayer(node.getXPosition() + node.getLength()) - 1;

        for (int layer = startLayer; layer <= endLayer; layer++) {
            action.accept(layer);
        }
    }

    /**
     * Executes the given {@link Consumer} for each layer the given {@link Edge} traverses.
     *
     * @param data   the {@link LayererData}
     * @param edge   an {@link Edge}
     * @param action a {@link Consumer} for layer indices
     */
    private void forEachLayer(final LayererData data, final Edge edge, final Consumer<Integer> action) {
        final int startLayer = data.positionToLayer(edge.getFrom().getXPosition() + edge.getFrom().getLength());
        final int endLayer = data.positionToLayer(edge.getTo().getXPosition()) - 1;

        for (int layer = startLayer; layer <= endLayer; layer++) {
            action.accept(layer);
        }
    }

    /**
     * Adds the given {@link LayoutableNode} somewhere in the given layer.
     *
     * @param layer a layer
     * @param node  a {@link LayoutableNode}
     */
    private void addToLayerSomewhere(final LayoutableNode[] layer, final LayoutableNode node) {
        for (int i = 0; i < layer.length; i++) {
            if (layer[i] == null) {
                layer[i] = node;
                return;
            }
        }

        throw new IllegalStateException("Layer is full, calculateHeights method is erroneous.");
    }


    /**
     * Class for easily passing data on the data to be laid out.
     */
    private static class LayererData {
        private final Subgraph subgraph;
        private final Collection<? extends LayoutableNode> nodes;
        private final int minLayer;


        /**
         * Constructs a new {@link LayererData}.
         *
         * @param subgraph a {@link Subgraph}
         */
        LayererData(final Subgraph subgraph) {
            this.subgraph = subgraph;
            this.nodes = subgraph.getNodes();

            final Long minPosition = nodes.stream()
                    .map(LayoutableNode::getXPosition)
                    .min(Long::compare)
                    .orElseThrow(() -> new IllegalStateException("Non-empty collection has no minimum."));
            this.minLayer = (int) ((minPosition + LAYER_WIDTH - 1) / LAYER_WIDTH) - 1;
        }


        /**
         * Returns the number of the layer the given position would be in.
         *
         * @param position a position
         * @return the number of the layer the given position would be in
         */
        int positionToLayer(final long position) {
            assert position >= 0;
            return (int) ((position + LAYER_WIDTH - 1) / LAYER_WIDTH) - minLayer;
        }
    }
}
