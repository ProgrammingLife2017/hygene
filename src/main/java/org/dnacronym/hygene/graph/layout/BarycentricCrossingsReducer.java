package org.dnacronym.hygene.graph.layout;

import org.apache.commons.lang3.ArrayUtils;
import org.dnacronym.hygene.graph.NewNode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;


/**
 * Part of the {@link SugiyamaLayout} algorithm, reduces the number of edge crossings in a layout using the
 * barycentric method.
 */
@SuppressWarnings("keyfor") // Not possible to add the annotations for this within the lambdas used
public final class BarycentricCrossingsReducer implements SugiyamaCrossingsReducer {
    @Override
    public void reduceCrossings(final NewNode[][] layers) {
        for (int i = 1; i < layers.length; i++) {
            layers[i] = reduceCrossingsBetweenLayers(layers[i - 1], layers[i]);
        }
    }

    /**
     * Reduces the crossings between two layers.
     * <p>
     * First it will compute the ordinal position of a node then it will sort the nodes according to these ordinal
     * positions and return them in array.
     * <p>
     * The ordinal position is computed by dividing the sum of ordinal positions of the parents of a node in layer 1
     * by the total number of parents the node haves. So, the ordinal position of a node will be the average of the
     * ordinal positions of its parents.
     *
     * @param layer1 the layer for which the node positions are already known
     * @param layer2 the layer for which we want to determine the node positions
     * @return the nodes from layer 2 sorted by the computed ordinal position
     */
    private NewNode[] reduceCrossingsBetweenLayers(final NewNode[] layer1, final NewNode[] layer2) {
        final Map<NewNode, Double> positions = new LinkedHashMap<>(); // maps nodes to ordinal positions

        for (int i = 0; i < layer2.length; i++) {
            final NewNode node = layer2[i];

            final int[] neighboursInLayer1 = neighboursInLayer1(node, layer1);

            final double nodeOrdinal = (double) IntStream.of(neighboursInLayer1).sum() / neighboursInLayer1.length;

            positions.put(node, nodeOrdinal);
        }

       return positions.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .map(Map.Entry::getKey)
                .toArray(NewNode[]::new);
    }

    /**
     * Returns the sorted ordinal positions of nodes in layer 1 that are in the intersection between the neighbours
     * of the given node and layer 1.
     *
     * @param node   a node from layer 2
     * @param layer1 the nodes in layer 1
     * @return the sorted ordinal position of neighbours in layer 1
     */
    private int[] neighboursInLayer1(final NewNode node, final NewNode[] layer1) {
        return node.getIncomingEdges().stream()
                .filter(neighbour -> Arrays.stream(layer1)
                        .anyMatch(layer1Element -> layer1Element == neighbour.getFrom())
                )
                .mapToInt(v -> ArrayUtils.indexOf(layer1, v.getFrom()) + 1)
                .sorted()
                .toArray();
    }
}
