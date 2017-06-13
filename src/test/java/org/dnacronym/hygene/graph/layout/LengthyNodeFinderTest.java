package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.NewNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link LengthyNodeFinder}s.
 */
final class LengthyNodeFinderTest extends LayerConstructingTestBase {
    @Test
    void testLengthyNodes1() {
        final Map<Integer, NewNode> layer1 = createLayer(1);
        final Map<Integer, NewNode> layer2 = createLayer(2, 5);
        final Map<Integer, NewNode> layer3 = createLayer(2);
        final Map<Integer, NewNode> layer4 = createLayer(2);
        final Map<Integer, NewNode> layer5 = createLayer(3);
        createEdges(new int[][] {{1, 2}, {1, 5}}, layer1, layer2);
        createEdges(new int[][] {{2, 3}}, layer4, layer5);

        final NewNode[][] layers = combineLayers(layer1, layer2, layer3, layer4, layer5);

        Map<NewNode, Integer> lengthyNodes = new LengthyNodeFinder().findInLayers(layers);

        assertThat(lengthyNodes.get(layer1.get(1))).isEqualTo(3);
        assertThat(lengthyNodes.get(layer2.get(2))).isEqualTo(3);
        assertThat(lengthyNodes.get(layer2.get(5))).isEqualTo(0);
        assertThat(lengthyNodes.get(layer5.get(3))).isEqualTo(0);
    }

    @Test
    void testLengthyNodes2() {
        final Map<Integer, NewNode> layer1 = createLayer(1);
        final Map<Integer, NewNode> layer2 = createLayer(2, 5);
        final Map<Integer, NewNode> layer3 = createLayer(2, 5);
        final Map<Integer, NewNode> layer4 = createLayer(2, 6);
        final Map<Integer, NewNode> layer5 = createLayer(3);
        createEdges(new int[][] {{1, 2}, {1, 5}}, layer1, layer2);
        createEdges(new int[][] {{5, 6}}, layer3, layer4);
        createEdges(new int[][] {{2, 3}}, layer4, layer5);

        final NewNode[][] layers = combineLayers(layer1, layer2, layer3, layer4, layer5);

        Map<NewNode, Integer> lengthyNodes = new LengthyNodeFinder().findInLayers(layers);

        assertThat(lengthyNodes.get(layer1.get(1))).isEqualTo(5);
        assertThat(lengthyNodes.get(layer2.get(2))).isEqualTo(3);
        assertThat(lengthyNodes.get(layer2.get(5))).isEqualTo(2);
        assertThat(lengthyNodes.get(layer4.get(6))).isEqualTo(0);
        assertThat(lengthyNodes.get(layer5.get(3))).isEqualTo(0);
    }

    @Test
    void testLengthyNodes3() {
        final Map<Integer, NewNode> layer1 = createLayer(1);
        final Map<Integer, NewNode> layer2 = createLayer(2, 3);
        final Map<Integer, NewNode> layer3 = createLayer(2, 3);
        final Map<Integer, NewNode> layer4 = createLayer(4);
        final Map<Integer, NewNode> layer5 = createLayer(5, 6);
        final Map<Integer, NewNode> layer6 = createLayer(5, 6);
        final Map<Integer, NewNode> layer7 = createLayer(7);
        createEdges(new int[][] {{1, 2}, {1, 3}}, layer1, layer2);
        createEdges(new int[][] {{2, 4}, {3, 4}}, layer3, layer4);
        createEdges(new int[][] {{4, 5}, {4, 6}}, layer4, layer5);
        createEdges(new int[][] {{5, 7}, {6, 7}}, layer6, layer7);

        final NewNode[][] layers = combineLayers(layer1, layer2, layer3, layer4, layer5, layer6, layer7);

        Map<NewNode, Integer> lengthyNodes = new LengthyNodeFinder().findInLayers(layers);

        assertThat(lengthyNodes.get(layer1.get(1))).isEqualTo(8);
        assertThat(lengthyNodes.get(layer4.get(4))).isEqualTo(4);
        assertThat(lengthyNodes.get(layer5.get(5))).isEqualTo(2);
        assertThat(lengthyNodes.get(layer5.get(6))).isEqualTo(2);
        assertThat(lengthyNodes.get(layer7.get(7))).isEqualTo(0);
    }

    @Test
    void testLengthyNodes4() {
        final Map<Integer, NewNode> layer1 = createLayer(1, 2);
        final Map<Integer, NewNode> layer2 = createLayer(3, 4, 5);
        final Map<Integer, NewNode> layer3 = createLayer(3, 5);
        createEdges(new int[][] {{1, 3}, {1, 4}, {2, 5}}, layer1, layer2);

        final NewNode[][] layers = combineLayers(layer1, layer2, layer3);

        Map<NewNode, Integer> lengthyNodes = new LengthyNodeFinder().findInLayers(layers);

        assertThat(lengthyNodes.get(layer1.get(1))).isEqualTo(2);
        assertThat(lengthyNodes.get(layer1.get(2))).isEqualTo(2);
    }

    @Test
    void testLengthyNodes5() {
        final Map<Integer, NewNode> layer1 = createLayer(1);
        final Map<Integer, NewNode> layer2 = createLayer(2, 3);
        final Map<Integer, NewNode> layer3 = createLayer(3);
        createEdges(new int[][] {{1, 2}, {1, 3}}, layer1, layer2);

        final NewNode[][] layers = combineLayers(layer1, layer2, layer3);

        Map<NewNode, Integer> lengthyNodes = new LengthyNodeFinder().findInLayers(layers);

        assertThat(lengthyNodes.get(layer1.get(1))).isEqualTo(2);
    }
}
