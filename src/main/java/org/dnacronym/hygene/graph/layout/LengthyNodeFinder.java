package org.dnacronym.hygene.graph.layout;

import org.apache.commons.lang3.ArrayUtils;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.node.Node;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


/**
 * Finds lengthy nodes and indicates how many lengthy children a node has.
 */
public final class LengthyNodeFinder {
    private final Map<LayoutableNode, Integer> lengthyNodes;
    private final Map<LayoutableNode, LayoutableNode> parentOf;


    /**
     * Constructs and initializes {@link LengthyNodeFinder}.
     */
    public LengthyNodeFinder() {
        lengthyNodes = new HashMap<>();
        parentOf = new HashMap<>();
    }


    /**
     * Finds lengthy nodes and indicates how long the lengthy children of a node are.
     * <p>
     * A DFS is performed to find children of the node that are lengthy. The DFS is started from every node in the
     * first layer.
     *
     * @param layers an array of layers
     * @return a map mapping nodes to the summed length of all its lengthy children
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    // New instances of stack elements can only be created during the loop
    public Map<LayoutableNode, Integer> findInLayers(final LayoutableNode[][] layers) {
        for (int i = 0; i < layers[0].length; i++) {
            final LayoutableNode rootNode = layers[0][i];

            final Set<LayoutableNode> visited = new HashSet<>();
            final Deque<StackElement> stack = new LinkedList<>();

            stack.add(new StackElement(rootNode, 0));

            while (!stack.isEmpty()) {
                final StackElement element = stack.pop();

                if (visited.contains(element.node)) {
                    continue;
                }
                visited.add(element.node);

                final int length = computeNodeLength(element.node, element.layer, layers);

                lengthyNodes.put(element.node, length);

                updateParents(element.node, length);

                for (final Edge edge : element.node.getOutgoingEdges()) {
                    parentOf.put(edge.getTo(), element.node);

                    // Skip layers if lengthy node, because there are no edges between the same node on different layers
                    stack.add(new StackElement(edge.getTo(), element.layer + (length > 0 ? length : 1)));
                }
            }
        }

        return lengthyNodes;
    }

    /**
     * Determines if a node is lengthy.
     *
     * @param node   the node to check
     * @param layer  the number of the layer that contains the given node
     * @param layers the array of all layers
     * @return true if a node is lengthy
     */
    public boolean isLengthy(final LayoutableNode node, final int layer, final LayoutableNode[][] layers) {
        if (layers.length <= layer + 1) {
            return false;
        }

        return ArrayUtils.contains(layers[layer + 1], node);
    }

    /**
     * Finds the number of layers in which the node is lengthy, if it is lengthy.
     *
     * @param node   the node for which the length needs to be computed
     * @param layer  the number of the layer the given node is on
     * @param layers the array of layers
     * @return 0 if node is not lengthy, otherwise the number of layers that the node uses
     */
    private int computeNodeLength(final LayoutableNode node, final int layer, final LayoutableNode[][] layers) {
        int lengthyLayer = layer;
        int length = 0;
        while (isLengthy(node, lengthyLayer, layers)) {
            if (length == 0) {
                length = 1;
            }

            length++;
            lengthyLayer++;
        }

        return length;
    }

    /**
     * Updates the lengthy value of all parents of a node.
     *
     * @param node   the node for which the parents need to be updated
     * @param length the length of the node
     */
    private void updateParents(final LayoutableNode node, final int length) {
        LayoutableNode parent = parentOf.get(node);
        while (parent != null) {
            final Integer parentLengthyNodeLength = lengthyNodes.get(parent);
            if (parentLengthyNodeLength != null) {
                lengthyNodes.put(parent, parentLengthyNodeLength + length);
            }
            parent = parentOf.get(parent);
        }
    }


    /**
     * Represents an element of the stack used in the DFS.
     */
    private static class StackElement {
        private final LayoutableNode node;
        private final int layer;

        /**
         * Constructs and initializes {@link StackElement}.
         *
         * @param node  a node
         * @param layer the layer number of the given node
         */
        StackElement(final LayoutableNode node, final int layer) {
            this.node = node;
            this.layer = layer;
        }
    }
}
