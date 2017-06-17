package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Utility methods for testing functionality that uses node layers.
 * <p>
 * This class is intended to be extended.
 */
abstract class LayerConstructingTestBase {
    private Map<Integer, Node> nodes;


    @BeforeEach
    void setUpLayerConstructingTestBase() {
        nodes = new HashMap<>();
    }


    protected final Map<Integer, Node> createLayer(final int... nodeIds) {
        final Map<Integer, Node> layer = new HashMap<>();

        Arrays.stream(nodeIds).forEach(nodeId -> {
            final Node node = nodes.computeIfAbsent(nodeId, id -> {
                if (id < 0) {
                    return new DummyNode(null, null);
                }
                return new Segment(id, 0, 0);
            });
            layer.put(nodeId, node);
        });

        return layer;
    }

    protected final void createEdges(final int[][] edges,
                                     final Map<Integer, Node> layer1, final Map<Integer, Node> layer2) {
        Arrays.stream(edges).forEach(edgeArray -> {
                    final Node source = layer1.get(edgeArray[0]);
                    final Node destination = layer2.get(edgeArray[1]);
                    final Edge edge = new Edge(source, destination);

                    source.getOutgoingEdges().add(edge);
                    destination.getIncomingEdges().add(edge);
                }
        );
    }

    @SafeVarargs
    protected final Node[][] combineLayers(final Map<Integer, Node>... layers) {
        final Node[][] result = new Node[layers.length][];

        for (int i = 0; i < layers.length; i++) {
            result[i] = layers[i].values().stream().toArray(Node[]::new);
        }

        return result;
    }

    protected Node getNode(final int id) {
        return nodes.get(id);
    }
}
