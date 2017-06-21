package org.dnacronym.hygene.graph.layout;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.ArrayUtils;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.FillNode;
import org.dnacronym.hygene.graph.node.LayoutableNode;

import java.util.Arrays;
import java.util.Optional;


/**
 * Optimizes edge positioning by adding {@link FillNode}s when necessary to improve alignment.
 */
public class EdgeOptimizer {
    private final LayoutableNode[][] layers;


    /**
     * Constructs and initializes {@link LayoutableNode}.
     *
     * @param layers an array of layers
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    @SuppressWarnings("PMD.ArrayIsStoredDirectly") // For performance reason, we don't want to create a copy here
    public EdgeOptimizer(final LayoutableNode[][] layers) {
        this.layers = layers;
    }


    /**
     * Fixes jumping and zigzag edges (e.g. _/\_) between nodes.
     *
     * @param layer2Index the index of layer 2
     * @return a new version of layer 2
     */
    @SuppressWarnings({"nullness", "PMD.AvoidInstantiatingObjectsInLoops"})
    // False positive of nullness and we explicitly want to create new objects
    public LayoutableNode[] fixJumpingEdges(final int layer2Index) {
        final LayoutableNode[] layer1 = layers[layer2Index - 1];
        LayoutableNode[] layer2 = layers[layer2Index];

        for (int i = layer2.length - 1; i >= 0; i--) {
            final LayoutableNode node = layer2[i];

            final int positionOfNodeInLayer1 = parentPositionInPreviousLayer(node, layer1);
            final int positionOfNodeInLayer2 = i;

            // If there is no parent node in the previous layer or the situation is not appropriate, we skip the layer.
            if (positionOfNodeInLayer1 == -1 || positionOfNodeInLayer1 <= positionOfNodeInLayer2) {
                continue;
            }

            if (positionOfNodeInLayer1 >= layer2.length) {
                // Fill
                layer2 = Arrays.copyOf(layer2, positionOfNodeInLayer1 + 1);
                for (int j = i; j < positionOfNodeInLayer1; j++) {
                    layer2[j] = new FillNode();
                }
                layer2[positionOfNodeInLayer1] = node;
            } else if (layer2[positionOfNodeInLayer1] instanceof FillNode) {
                // Swap
                final LayoutableNode fillNode = layer2[positionOfNodeInLayer1];
                layer2[positionOfNodeInLayer1] = node;
                layer2[i] = fillNode;
            }
        }

        return layer2;
    }

    /**
     * Finds the position of parent node of the current node in the previous layer, if it exists.
     *
     * @param node  the node for which the parent needs to be found
     * @param layer the layer in which the parent needs to be found
     * @return the position of parent node of the current node in the previous layer, if it exists, otherwise {@code -1}
     */
    private int parentPositionInPreviousLayer(final LayoutableNode node, final LayoutableNode[] layer) {
        final Optional<Edge> possibleParent = node.getIncomingEdges().stream().findFirst();
        if (!possibleParent.isPresent()) {
            return -1;
        }
        final LayoutableNode parent = possibleParent.get().getFrom();

        return ArrayUtils.indexOf(layer, parent);
    }
}
