package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.node.FillNode;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.node.Node;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link EdgeOptimizer}.
 */
public class EdgeOptimizerTest extends LayerConstructingTestBase {
    @Test
    void testNoParentInPreviousLayerDoesNotChangeTheLayout() {
        final Map<Integer, Node> layer1 = createLayer(1, 2);
        final Map<Integer, Node> layer2 = createLayer(3, 4);
        createEdges(new int[][] {{1, 3}, {2, 4}}, layer1, layer2);
        final Node[][] layers = combineLayers(layer1, layer2);
        final Node[] layer2Before = Arrays.copyOf(layers[1], layers[1].length);

        final LayoutableNode[] newLayer2 = new EdgeOptimizer(layers).fixJumpingEdges(1);

        assertThat(newLayer2).isEqualTo(layer2Before);
    }

    @Test
    void testNodeWithNoEdgesToPreviousLayerDoesNotChangeTheLayout() {
        final Map<Integer, Node> layer1 = createLayer(1, 2);
        final Map<Integer, Node> layer2 = createLayer(3, 4);
        final Node[][] layers = combineLayers(layer1, layer2);
        final Node[] layer2Before = Arrays.copyOf(layers[1], layers[1].length);

        final LayoutableNode[] newLayer2 = new EdgeOptimizer(layers).fixJumpingEdges(1);

        assertThat(newLayer2).isEqualTo(layer2Before);
    }

    @Test
    void testFillLayerWithFillNodesToKeepNodesOnTheSameLayer() {
        final Map<Integer, Node> layer1 = createLayer(1, -2);
        final Map<Integer, Node> layer2 = createLayer(-3);
        createEdges(new int[][] {{-2, -3}}, layer1, layer2);
        final Node[][] layers = combineLayers(layer1, layer2);

        final LayoutableNode[] newLayer2 = new EdgeOptimizer(layers).fixJumpingEdges(1);

        assertThatLayerContainsExactly((Node[]) newLayer2, -9999, -3);
    }

    @Test
    void testSwapFillNode() {
        final Map<Integer, Node> layer1 = createLayer(1, -2);
        final Map<Integer, Node> layer2 = createLayer(-3, -9999);
        layer2.put(-9999, new FillNode());
        createEdges(new int[][] {{-2, -3}}, layer1, layer2);
        final Node[][] layers = combineLayers(layer1, layer2);

        final LayoutableNode[] newLayer2 = new EdgeOptimizer(layers).fixJumpingEdges(1);

        assertThatLayerContainsExactly((Node[]) newLayer2, -9999, -3);
    }
}
