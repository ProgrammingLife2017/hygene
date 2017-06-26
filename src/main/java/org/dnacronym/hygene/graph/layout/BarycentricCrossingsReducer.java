package org.dnacronym.hygene.graph.layout;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.node.FillNode;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.node.Node;

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
@SuppressWarnings({"keyfor", "PMD.CyclomaticComplexity"})
// Not possible to add the annotations for this within the lambdas used
public final class BarycentricCrossingsReducer implements SugiyamaCrossingsReducer {
    private final LengthyNodeFinder lengthyNodeFinder;

    /**
     * Constructs and initializes {@link BarycentricCrossingsReducer}.
     */
    public BarycentricCrossingsReducer() {
        lengthyNodeFinder = new LengthyNodeFinder();
    }


    @Override
    public void reduceCrossings(final LayoutableNode[][] layers) {
        if (layers.length == 0) {
            return;
        }

        final Map<LayoutableNode, Integer> lengthyNodes = lengthyNodeFinder.findInLayers(layers);

        for (int i = 1; i < layers.length; i++) {
            if (Thread.interrupted()) {
                return;
            }
            layers[i] = reduceCrossingsBetweenLayers(layers, i, lengthyNodes);
        }

        final EdgeOptimizer edgeOptimizer = new EdgeOptimizer(layers);
        for (int i = 1; i < layers.length; i++) {
            if (Thread.interrupted()) {
                return;
            }
            layers[i] = edgeOptimizer.fixJumpingEdges(i);
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
     * @param layers       the layers
     * @param layer2Index  the index of the second layer, will also be used to compute the second layer
     * @param lengthyNodes a map mapping nodes to the summed length of all its lengthy children
     * @return the nodes from layer 2 sorted by the computed ordinal position
     */
    @SuppressWarnings("nullness") // False positive
    private LayoutableNode[] reduceCrossingsBetweenLayers(final LayoutableNode[][] layers,
                                                          final int layer2Index,
                                                          final Map<LayoutableNode, Integer> lengthyNodes) {
        final LayoutableNode[] layer1 = layers[layer2Index - 1];
        final LayoutableNode[] layer2 = layers[layer2Index];

        final List<@Nullable LayoutableNode> newLayer2 = new ArrayList<>();
        enlargeList(newLayer2, layer2.length);

        final Map<Integer, LayoutableNode> lengthy = giveLengthyNodesSamePosition(layer1, layer2, newLayer2);
        final List<LayoutableNode> nonLengthy = Arrays.stream(layer2)
                .filter(node -> !lengthy.containsValue(node))
                .collect(Collectors.toList());

        final Map<LayoutableNode, Double> positions = new LinkedHashMap<>(); // Maps nodes to ordinal positions

        for (final LayoutableNode node : nonLengthy) {
            final int[] neighboursInLayer1 = neighboursInLayer1(node, layer1);

            final double averageOfParents = (double) IntStream.of(neighboursInLayer1).sum() / neighboursInLayer1.length;

            positions.put(node, averageOfParents);
        }

        final Map<LayoutableNode, Double> sortedNonLengthy = positions.entrySet().stream()
                .sorted(getNodeOrderingComparator(lengthyNodes))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        fillGapsWithResults(sortedNonLengthy, lengthy, newLayer2);

        addDummyNodesBetweenWideChildrenNodesAndLengthyNodes(newLayer2, layer2Index, layers);

        return newLayer2.stream().map(node -> node == null ? new FillNode() : node).toArray(Node[]::new);
    }

    /**
     * Returns the sorted ordinal positions of nodes in layer 1 that are in the intersection between the neighbours
     * of the given node and layer 1.
     *
     * @param node   a node from layer 2
     * @param layer1 the nodes in layer 1
     * @return the sorted ordinal position of neighbours in layer 1
     */
    private int[] neighboursInLayer1(final LayoutableNode node, final LayoutableNode[] layer1) {
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
     * @param layer1    the nodes in layer 1
     * @param layer2    the nodes in layer 2
     * @param newLayer2 the results of the current iteration
     * @return map from position in results to lengthy nodes
     */
    private Map<Integer, LayoutableNode> giveLengthyNodesSamePosition(final LayoutableNode[] layer1,
                                                                      final LayoutableNode[] layer2,
                                                                      final List<@Nullable LayoutableNode> newLayer2) {
        final Map<Integer, LayoutableNode> lengthy = new LinkedHashMap<>();

        Arrays.stream(layer2).forEach(layer2Node -> {
            final int layer1Position = ArrayUtils.indexOf(layer1, layer2Node);
            if (layer1Position > -1) {
                enlargeList(newLayer2, layer1Position + 1);
                newLayer2.set(layer1Position, layer2Node);
                lengthy.put(layer1Position, layer2Node);
            }
        });

        return lengthy;
    }


    /**
     * Compares two map entries with a node as key and the ordinal position as value.
     * <p>
     * First it will compare the ordinal positions, if they are not equal then the lengthy nodes length of a node will
     * be compared. The highest lengthy nodes length will be put on the left.
     *
     * @param lengthyNodes map mapping nodes to the summed length of all its lengthy children
     * @return a comparator
     */
    private Comparator<Map.Entry<LayoutableNode, Double>> getNodeOrderingComparator(
            final Map<LayoutableNode, Integer> lengthyNodes) {
        return (entry1, entry2) -> {
            final int compare = Double.compare(entry1.getValue(), entry2.getValue());
            if (compare == 0) {
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
     * @param sortedNonLengthy a map from non lengthy nodes in the correct order to their positions
     * @param lengthy          a map from the positions of lengthy nodes to the lengthy nodes in the correct order
     * @param result           the result of the current iteration
     */
    private void fillGapsWithResults(final Map<LayoutableNode, Double> sortedNonLengthy,
                                     final Map<Integer, LayoutableNode> lengthy,
                                     final List<@Nullable LayoutableNode> result) {
        int resultPosition = 0;
        for (final Map.Entry<LayoutableNode, Double> entry : sortedNonLengthy.entrySet()) {

            // Make sure that nodes are correctly aligned between/around lengthy nodes
            for (final Map.Entry<Integer, LayoutableNode> lengthyEntry : lengthy.entrySet()) {
                if (entry.getValue() > lengthyEntry.getKey()) {
                    resultPosition = lengthyEntry.getKey() + 1;
                }
            }

            // Make the result list bigger if needed
            enlargeList(result, resultPosition + 1);

            while (result.get(resultPosition) != null) {
                resultPosition++;

                enlargeList(result, resultPosition + 1);
            }

            result.set(resultPosition, entry.getKey());

            resultPosition++;
        }
    }

    /**
     * Adds dummy nodes between nodes with a large children width and lengthy nodes, in order to move lengthy nodes
     * to the right.
     *
     * @param newLayer2   the result of the current iteration
     * @param layer2Index the index of the second layer
     * @param layers      the layers
     */
    @SuppressWarnings("squid:ForLoopCounterChangedCheck") // For this specific implementation this is not a problem
    private void addDummyNodesBetweenWideChildrenNodesAndLengthyNodes(final List<@Nullable LayoutableNode> newLayer2,
                                                                      final int layer2Index,
                                                                      final LayoutableNode[][] layers) {
        for (int i = 0; i < newLayer2.size() - 1; i++) {
            final LayoutableNode node = newLayer2.get(i);
            final LayoutableNode rightNeighbour = newLayer2.get(i + 1);

            if (node == null || rightNeighbour == null) {
                continue;
            }

            if (lengthyNodeFinder.isLengthy(rightNeighbour, layer2Index, layers)) {
                final int childrenWidth = getMaxChildrenWidth(node);

                for (int child = 0; child < childrenWidth - 1; child++) {
                    newLayer2.add(i + 1, null);
                }

                if (childrenWidth > 0) {
                    i += childrenWidth - 1;
                }
            }
        }
    }

    /**
     * Enlarges the result list to the given size padded with null values.
     *
     * @param result  the result list to be enlarged
     * @param newSize the new size of the list
     */
    private void enlargeList(final List<@Nullable LayoutableNode> result, final int newSize) {
        while (result.size() < newSize) {
            result.add(null);
        }
    }

    /**
     * Returns the maximum number of children with the same distance to the given node.
     *
     * @param node a {@link LayoutableNode}
     * @return the maximum number of children with the same distance to the given node
     */
    private int getMaxChildrenWidth(final LayoutableNode node) {
        int[] maxChildrenWidth = {-1};

        final Set<LayoutableNode> visited = new HashSet<>();
        final Queue<LayoutableNode> queue = new LinkedList<>();
        queue.add(node);

        final int[] depthIncreaseTimes = {1, 0};
        while (!queue.isEmpty()) {
            final LayoutableNode head = queue.remove();

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
