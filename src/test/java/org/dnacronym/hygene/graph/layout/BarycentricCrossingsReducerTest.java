package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.node.Node;
import org.junit.jupiter.api.Test;

import java.util.Map;


/**
 * Unit tests for {@link BarycentricCrossingsReducer}s.
 */
@SuppressWarnings("JavadocStyle") // Using Javadoc with alternative style in this test
public final class BarycentricCrossingsReducerTest extends LayerConstructingTestBase {
    /**
     *   1   2   3
     *  / \ / \ / \
     * 4   5   6   7
     */
    @Test
    void testGraph1() {
        final Map<Integer, Node> layer1 = createLayer(1, 2, 3);
        final Map<Integer, Node> layer2 = createLayer(7, 5, 4, 6);
        createEdges(new int[][] {{3, 7}, {2, 5}, {1, 5}, {1, 4}, {2, 6}, {3, 6}}, layer1, layer2);

        final Node[][] layers = combineLayers(layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThatLayerContainsExactly(layers[1], 4, 5, 6, 7);
    }

    /**
     * 1   2
     * | /  \  \
     * 4     5  6
     */
    @Test
    void testGraph2() {
        final Map<Integer, Node> layer1 = createLayer(1, 2);
        final Map<Integer, Node> layer2 = createLayer(6, 4, 5);
        createEdges(new int[][] {{2, 6}, {1, 4}, {2, 4}, {2, 5}}, layer1, layer2);

        final Node[][] layers = combineLayers(layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThatLayerContainsExactly(layers[1], 4, 5, 6);
    }

    /**
     *     1        2
     *    / \   \ /  /
     *  /     \ / \ /
     * 4       5   6
     */
    @Test
    void testGraph3() {
        final Map<Integer, Node> layer1 = createLayer(1, 2);
        final Map<Integer, Node> layer2 = createLayer(5, 6, 4);
        createEdges(new int[][] {{1, 5}, {2, 5}, {1, 6}, {2, 6}, {1, 4}}, layer1, layer2);

        final Node[][] layers = combineLayers(layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThatLayerContainsExactly(layers[1], 4, 5, 6);
    }

    /**
     *         0
     *      /  |  \
     *     1  2    5
     *    /  \||   ||
     *  /     ||\  ||
     * 3      2  4 5
     */
    @Test
    void testGraph4() {
        final Map<Integer, Node> layer0 = createLayer(0);
        final Map<Integer, Node> layer1 = createLayer(1, 2, 5);
        final Map<Integer, Node> layer2 = createLayer(3, 2, 4, 5);
        createEdges(new int[][] {{0, 1}, {0, 2}, {0, 5}}, layer0, layer1);
        createEdges(new int[][] {{1, 3}, {1, 4}}, layer1, layer2);

        final Node[][] layers = combineLayers(layer0, layer1, layer2);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThatLayerContainsExactly(layers[1], 2, 5, 1);
        assertThatLayerContainsExactly(layers[2], 2, 5, 3, 4);
    }

    /**
     *   0
     * /   \
     * 1     2
     * ||    |
     * 1     3
     * |     | \
     * 5     4  11
     * | \   || |
     * 6  7  4  12
     * | / \
     * 8 9 10
     */
    @Test
    void testGraph5() {
        final Map<Integer, Node> layer0 = createLayer(0);
        final Map<Integer, Node> layer1 = createLayer(1, 2);
        final Map<Integer, Node> layer2 = createLayer(1, 3);
        final Map<Integer, Node> layer3 = createLayer(5, 4, 11);
        final Map<Integer, Node> layer4 = createLayer(6, 7, 4, 12);
        final Map<Integer, Node> layer5 = createLayer(8, 9, 10);
        createEdges(new int[][] {{0, 1}, {0, 2}}, layer0, layer1);
        createEdges(new int[][] {{2, 3}}, layer1, layer2);
        createEdges(new int[][] {{1, 5}, {3, 4}, {3, 11}}, layer2, layer3);
        createEdges(new int[][] {{5, 6}, {5, 7}, {11, 12}}, layer3, layer4);
        createEdges(new int[][] {{6, 8}, {7, 9}, {7, 10}}, layer4, layer5);

        final Node[][] layers = combineLayers(layer0, layer1, layer2, layer3, layer4, layer5);

        new BarycentricCrossingsReducer().reduceCrossings(layers);

        assertThatLayerContainsExactly(layers[0], 0);
        assertThatLayerContainsExactly(layers[1], 1, 2);
        assertThatLayerContainsExactly(layers[2], 1, 3);
        assertThatLayerContainsExactly(layers[3], 5, -9999, -9999, 4, 11);
        assertThatLayerContainsExactly(layers[4], 6, 7, -9999, 4, 12);
        assertThatLayerContainsExactly(layers[5], 8, 9, 10);
    }
}
