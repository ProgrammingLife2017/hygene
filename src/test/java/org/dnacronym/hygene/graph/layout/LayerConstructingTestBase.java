package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
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
    private HashMap<Integer, NewNode> nodes;

    @BeforeEach
    void setUpLayerConstructingTestBase() {
        nodes = new HashMap<>();
    }


    protected Map<Integer, NewNode> createLayer(final int... nodeIds) {
        final Map<Integer, NewNode> layer = new HashMap<>();

        Arrays.stream(nodeIds).forEach(nodeId -> {
            final NewNode node = nodes.computeIfAbsent(nodeId, id -> new Segment(id, 0, 0));
            layer.put(nodeId, node);
        });

        return layer;
    }

    protected void createEdges(final int[][] edges,
                             final Map<Integer, NewNode> layer1, final Map<Integer, NewNode> layer2) {
        Arrays.stream(edges).forEach(edgeArray -> {
                    final NewNode source = layer1.get(edgeArray[0]);
                    final NewNode destination = layer2.get(edgeArray[1]);
                    final Edge edge = new Edge(source, destination);

                    source.getOutgoingEdges().add(edge);
                    destination.getIncomingEdges().add(edge);
                }
        );
    }

    protected NewNode[][] combineLayers(final Map<Integer, NewNode>... layers) {
        final NewNode[][] result =  new NewNode[layers.length][];

        for (int i = 0; i < layers.length; i++) {
            result[i] = layers[i].values().stream().toArray(NewNode[]::new);
        }

        return result;
    }
}
