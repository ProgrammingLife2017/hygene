package org.dnacronym.hygene.graph.layout;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.NewNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Part of the {@link SugiyamaLayout} algorithm, reduces the number of edge crossings in a layout using the
 * barycentric method.
 */
@SuppressWarnings("keyfor") // Not possible to add the annotations for this within the lambdas used
public final class BarycentricCrossingsReducer implements SugiyamaCrossingsReducer {
    private final LengthyNodeFinder lengthyNodeFinder;
    private @MonotonicNonNull Map<NewNode, Integer> lengthyNodes;

    /**
     * Constructs and initializes {@link BarycentricCrossingsReducer}.
     */
    public BarycentricCrossingsReducer() {
        lengthyNodeFinder = new LengthyNodeFinder();
    }

    @Override
    public void reduceCrossings(final NewNode[][] layers) {
        lengthyNodes = lengthyNodeFinder.findInLayers(layers);

        for (int i = 1; i < layers.length; i++) {
            layers[i] = reduceCrossingsBetweenLayers(layers[i - 1], layers[i]);
        }
    }

    /**
     * Reduces the crossings between two layers.
     * <p>
     * First it will compute the ordinal position of a node, then it will sort the nodes according to these ordinal
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
    @SuppressWarnings("nullness") // false positive
    private NewNode[] reduceCrossingsBetweenLayers(final NewNode[] layer1, final NewNode[] layer2) {
        final Map<NewNode, Double> positions = new LinkedHashMap<>(); // maps nodes to ordinal positions

        final List<@Nullable NewNode> result = new ArrayList<>();
        for (int i = 0; i < layer2.length; i++) {
            result.add(null);
        }

        final List<NewNode> nonLengthy = giveLengthyNodesSamePosition(layer1, layer2, result);

        for (final NewNode node : nonLengthy) {
            final int[] neighboursInLayer1 = neighboursInLayer1(node, layer1);

            final double nodeOrdinal = (double) IntStream.of(neighboursInLayer1).sum() / neighboursInLayer1.length;

            positions.put(node, nodeOrdinal);
        }

        final List<NewNode> sortedNonLengthy = positions.entrySet()
                .stream()
                .sorted(getNodeOrderingComparator())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        fillGapsWithResults(sortedNonLengthy, result);

        return result.toArray(new NewNode[result.size()]);
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


    /**
     * Puts lengthy nodes in the same position in the results as their parent.
     *
     * @param layer1 the nodes in layer 1
     * @param layer2 the nodes in layer 2
     * @param results the results of the current iteration
     * @return list containing all nodes that are not lengthy
     */
    private List<NewNode> giveLengthyNodesSamePosition(final NewNode[] layer1, final NewNode[] layer2,
                                                       final List<@Nullable NewNode> results) {
        final List<NewNode> nonLengthy = new ArrayList<>();

        Arrays.stream(layer2).forEach(layer2Node -> {
            final int layer1Position = ArrayUtils.indexOf(layer1, layer2Node);
            if (layer1Position > -1) {
                results.set(layer1Position, layer2Node);
            } else {
                nonLengthy.add(layer2Node);
            }
        });

        return nonLengthy;
    }

    /**
     * Compares two map entries with a node as key and the ordinal position as value.
     * <p>
     * First it will compare the ordinal positions, if they are not equal then the lengthy nodes length of a node will
     * be compared. The highest lengthy nodes length will be put on the left.
     *
     * @return a comparator
     */
    private Comparator<Map.Entry<NewNode, Double>> getNodeOrderingComparator() {
        return (entry1, entry2) -> {
            final int compare = Double.compare(entry1.getValue(), entry2.getValue());
            if (compare == 0 && lengthyNodes != null) {
                final Integer lengthyNodeLengthEntry1 = lengthyNodes.get(entry1.getKey());
                final Integer lengthyNodeLengthEntry2 = lengthyNodes.get(entry2.getKey());

                if (lengthyNodeLengthEntry1 != null && lengthyNodeLengthEntry2 != null) {
                    return Integer.compare(lengthyNodeLengthEntry2, lengthyNodeLengthEntry1);
                }
            }
            return compare;
        };
    }

    /**
     * Fills the gaps around the pre-positioned lengthy nodes with the nodes of the result of the crossing reduction.
     *
     * @param sortedNonLengthy a list of non lengthy nodes in the correct order
     * @param result the result of the current iteration
     */
    private void fillGapsWithResults(final List<NewNode> sortedNonLengthy, final List<@Nullable NewNode> result) {
        int resultPosition = 0;
        for (final NewNode node : sortedNonLengthy) {
            while (result.get(resultPosition) != null) {
                resultPosition++;
            }

            result.set(resultPosition, node);

            resultPosition++;
        }
    }

    /**
     * Returns the maximum number of children with the same distance to the given node.
     *
     * @param layers an array of layers
     * @param node   a {@link NewNode}
     * @return the maximum number of children with the same distance to the given node
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod") // This method will be used later
    // TODO use this method and remove the suppression above
    private int getMaxChildrenWidth(final NewNode node) {
        int[] maxChildrenWidth = {-1};

        final Set<NewNode> visited = new HashSet<>();
        final Queue<NewNode> queue = new LinkedList<>();
        queue.add(node);

        final int[] depthIncreaseTimes = {1, 0};
        while (!queue.isEmpty()) {
            final NewNode head = queue.remove();

            if (!visited.contains(head)) {
                visited.add(head);

                head.getOutgoingEdges().forEach(edge -> {
                    if (!visited.contains(edge.getTo())) {
                        depthIncreaseTimes[1]++;
                        queue.add(edge.getTo());
                    }
                });
            }

            depthIncreaseTimes[0]--;
            if (depthIncreaseTimes[0] <= 0) {
                depthIncreaseTimes[0] = depthIncreaseTimes[1];
                depthIncreaseTimes[1] = 0;

                maxChildrenWidth[0] = Math.max(maxChildrenWidth[0], depthIncreaseTimes[0]);
                if (depthIncreaseTimes[0] == 1) {
                    return maxChildrenWidth[0];
                }
            }
        }

        return maxChildrenWidth[0];
    }
}
