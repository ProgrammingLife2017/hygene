package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link BarycentricCrossingsReducer}s.
 */
@SuppressWarnings("JavadocStyle") // Using Javadoc with alternative style in this test
public final class BarycentricCrossingsReducerTest {

    /**
     *   1   2   3
     *  / \ / \ / \
     * 4   5   6   7
     */
    @Test
    void testGraph1() {
        final Map<Integer, NewNode> layer1 = createLayer(1, 2, 3);
        final Map<Integer, NewNode> layer2 = createLayer(7, 5, 4, 6);
        createEdges(new int[][] {{3, 7}, {2, 5}, {1, 5}, {1, 4}, {2, 6}, {3, 6}}, layer1, layer2);

        final NewNode[][] layers = combineLayers(layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThat(layers[1]).containsExactly(layer2.get(4), layer2.get(5), layer2.get(6), layer2.get(7));
    }

    /**
     * 1   2
     * | /  \  \
     * 4     5  6
     */
    @Test
    void testGraph2() {
        final Map<Integer, NewNode> layer1 = createLayer(1, 2);
        final Map<Integer, NewNode> layer2 = createLayer(6, 4, 5);
        createEdges(new int[][] {{2, 6}, {1, 4}, {2, 4}, {2, 5}}, layer1, layer2);

        final NewNode[][] layers = combineLayers(layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThat(layers[1]).containsExactly(layer2.get(4), layer2.get(5), layer2.get(6));
    }

    /**
     *     1        2
     *    / \   \ /  /
     *  /     \ / \ /
     * 4       5   6
     */
    @Test
    void testGraph3() {
        final Map<Integer, NewNode> layer1 = createLayer(1, 2);
        final Map<Integer, NewNode> layer2 = createLayer(5, 6, 4);
        createEdges(new int[][] {{1, 5}, {2, 5}, {1, 6}, {2, 6}, {1, 4}}, layer1, layer2);

        final NewNode[][] layers = combineLayers(layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThat(layers[1]).containsExactly(layer2.get(4), layer2.get(5), layer2.get(6));
    }


    private Map<Integer, NewNode> createLayer(final int... nodeIds) {
        final Map<Integer, NewNode> layer = new HashMap<>();

        Arrays.stream(nodeIds).forEach(nodeId -> layer.put(nodeId, new Segment(0, 0, 0)));

        return layer;
    }

    private void createEdges(final int[][] edges,
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

    private NewNode[][] combineLayers(final Map<Integer, NewNode> layer1, final Map<Integer, NewNode> layer2) {
        return new NewNode[][] {
                layer1.values().stream().toArray(NewNode[]::new),
                layer2.values().stream().toArray(NewNode[]::new)
        };
    }
}
