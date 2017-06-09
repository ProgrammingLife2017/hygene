package org.dnacronym.hygene.graph.layout;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.DummyNode;
import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.FillNode;
import org.dnacronym.hygene.graph.NewNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Part of the {@link SugiyamaLayout} algorithm, reduces the number of edge crossings in a layout using the
 * barycentric method.
 */
@SuppressWarnings({"keyfor", "PMD.CyclomaticComplexity"}) // Not possible to add the annotations for this within the lambdas used
public final class BarycentricCrossingsReducer implements SugiyamaCrossingsReducer {
    private final LengthyNodeFinder lengthyNodeFinder;


    /**
     * Constructs and initializes {@link BarycentricCrossingsReducer}.
     */
    public BarycentricCrossingsReducer() {
        lengthyNodeFinder = new LengthyNodeFinder();
    }


    @Override
    public void reduceCrossings(final NewNode[][] layers) {
        final Map<NewNode, Integer> lengthyNodes = lengthyNodeFinder.findInLayers(layers);

        for (int i = 1; i < layers.length; i++) {
            layers[i] = reduceCrossingsBetweenLayers(layers, i, lengthyNodes);
        }

        for (int i = 1; i < layers.length - 1; i++) {
            layers[i] = fixJumpingEdges(layers, i);
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
    private NewNode[] reduceCrossingsBetweenLayers(final NewNode[][] layers, final int layer2Index,
                                                   final Map<NewNode, Integer> lengthyNodes) {
        final NewNode[] layer1 = layers[layer2Index - 1];
        final NewNode[] layer2 = layers[layer2Index];

        final List<@Nullable NewNode> newLayer2 = new ArrayList<>();
        enlargeList(newLayer2, layer2.length);

        final Map<Integer, NewNode> lengthy = giveLengthyNodesSamePosition(layer1, layer2, newLayer2);
        final List<NewNode> nonLengthy = Arrays.stream(layer2)
                .filter(node -> !lengthy.containsValue(node))
                .collect(Collectors.toList());

        final Map<NewNode, Double> positions = new LinkedHashMap<>(); // Maps nodes to ordinal positions

        for (final NewNode node : nonLengthy) {
            final int[] neighboursInLayer1 = neighboursInLayer1(node, layer1);

            final double averageOfParents = (double) IntStream.of(neighboursInLayer1).sum() / neighboursInLayer1.length;

            positions.put(node, averageOfParents);
        }

        final Map<NewNode, Double> sortedNonLengthy = positions.entrySet().stream()
                .sorted(getNodeOrderingComparator(lengthyNodes))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        fillGapsWithResults(sortedNonLengthy, lengthy, newLayer2);

        addDummyNodesBetweenWideChildrenNodesAndLengthyNodes(newLayer2, layer2Index, layers);

        return newLayer2.stream().map(node -> node == null ? new FillNode() : node).toArray(NewNode[]::new);
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
     * @param newLayer2 the results of the current iteration
     * @return map from position in results to lengthy nodes
     */
    private Map<Integer, NewNode> giveLengthyNodesSamePosition(final NewNode[] layer1, final NewNode[] layer2,
                                                       final List<@Nullable NewNode> newLayer2) {
        final Map<Integer, NewNode> lengthy = new LinkedHashMap<>();

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
    private Comparator<Map.Entry<NewNode, Double>> getNodeOrderingComparator(final Map<NewNode, Integer> lengthyNodes) {
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
    private void fillGapsWithResults(final Map<NewNode, Double> sortedNonLengthy,
                                     final Map<Integer, NewNode> lengthy,
                                     final List<@Nullable NewNode> result) {
        int resultPosition = 0;
        for (final Map.Entry<NewNode, Double> entry : sortedNonLengthy.entrySet()) {

            // Make sure that nodes are correctly aligned between/around lengthy nodes
            for (final Map.Entry<Integer, NewNode> lengthyEntry : lengthy.entrySet()) {
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
     * @param newLayer2      the result of the current iteration
     * @param layer2Index the index of the second layer
     * @param layers      the layers
     */
    @SuppressWarnings("squid:ForLoopCounterChangedCheck") // For this specific implementation this is not a problem
    private void addDummyNodesBetweenWideChildrenNodesAndLengthyNodes(final List<@Nullable NewNode> newLayer2,
                                                                      final int layer2Index, final NewNode[][] layers) {
        for (int i = 0; i < newLayer2.size() - 1; i++) {
            final NewNode node = newLayer2.get(i);
            final NewNode rightNeighbour = newLayer2.get(i + 1);

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
    private void enlargeList(final List<@Nullable NewNode> result, final int newSize) {
        while (result.size() < newSize) {
            result.add(null);
        }
    }

    /**
     * Returns the maximum number of children with the same distance to the given node.
     *
     * @param node a {@link NewNode}
     * @return the maximum number of children with the same distance to the given node
     */
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

    /**
     * Fixes jumping edges ( e.g. _/\_ ) between dummy nodes.
     *
     * @param layers      the array of layers
     * @param layer2Index the index of layer 2
     * @return a new version of layer 2
     */
    @SuppressWarnings({"squid:S135", "squid:S3776", "squid:S134", "nullness", "PMD.AvoidInstantiatingObjectsInLoops"})
    // It is not feasible to reduce the complexity at this stage
    private NewNode[] fixJumpingEdges(final NewNode[][] layers, final int layer2Index) {
        final NewNode[] layer1 = layers[layer2Index - 1];
        @Nullable NewNode[] layer2 = layers[layer2Index];
        final NewNode[] layer3 = layers[layer2Index + 1];

        for (int i = layer2.length - 1; i >= 0; i--) {
            final NewNode node = layer2[i];

            if (!(node instanceof DummyNode)) {
                continue;
            }

            final Optional<Edge> possibleLayer1Parent = node.getIncomingEdges().stream().findFirst();
            final Optional<Edge> possibleLayer3Child = node.getOutgoingEdges().stream().findFirst();

            if (!possibleLayer1Parent.isPresent() || !possibleLayer3Child.isPresent()) {
                continue;
            }

            final NewNode layer1Parent = possibleLayer1Parent.get().getFrom();
            final NewNode layer3Child = possibleLayer3Child.get().getTo();

            final int positionOfNodeInLayer1 = ArrayUtils.indexOf(layer1, layer1Parent);
            final int positionOfNodeInLayer2 = i;
            final int positionOfNodeInLayer3 = ArrayUtils.indexOf(layer3, layer3Child);

            if (positionOfNodeInLayer1 == -1 || positionOfNodeInLayer3 == -1) {
                continue;
            }

            if (positionOfNodeInLayer1 == positionOfNodeInLayer3 && positionOfNodeInLayer1 != positionOfNodeInLayer2) {
                if (positionOfNodeInLayer1 >= layer2.length) {
                    // Fill
                    layer2 = Arrays.copyOf(layer2, positionOfNodeInLayer1 + 1);
                    for (int j = i; j < positionOfNodeInLayer1; j++) {
                        layer2[i] = new FillNode();
                    }
                    layer2[positionOfNodeInLayer1] = node;
                } else if (layer2[positionOfNodeInLayer1] instanceof FillNode) {
                    // Swap
                    final NewNode fillNode = layer2[positionOfNodeInLayer1];
                    layer2[positionOfNodeInLayer1] = node;
                    layer2[i] = fillNode;
                }
            }
        }

        return layer2;
    }
}
