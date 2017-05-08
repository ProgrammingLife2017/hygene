package org.dnacronym.hygene.models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Function;


/**
 * A {@code SequenceGraph} contains {@code SequenceNode}s.
 * <p>
 * This graph has all its nodes passed to it on construction. It then adds two sentinel nodes (a 'source' and a 'sink'),
 * to the start and end of the graph structure. The source node connects to all vertices without incoming edges on the
 * left side, while the sink node is connected to all vertices without incoming edges on the right side. These two nodes
 * allow for simplified and more unified graph processing.
 * <p>
 * The construction of a {@code SequenceGraph} may take some time as the entire structure is fed to FAFOSP, the Felix
 * Algorithm For Optimal Segment Positioning.
 */
public final class SequenceGraph implements Iterable<SequenceNode> {
    public static final String SOURCE_NODE_ID = "<SOURCE>";
    public static final String SINK_NODE_ID = "<SINK>";

    private final SequenceNode sourceNode;
    private final SequenceNode sinkNode;
    private final int nodeCount;


    /**
     * Constructs a new {@code SequenceGraph} with the given nodes.
     *
     * @param nodes the list of nodes
     */
    public SequenceGraph(final List<SequenceNode> nodes) {
        this.sourceNode = new SequenceNode(SOURCE_NODE_ID, "");
        this.sinkNode = new SequenceNode(SINK_NODE_ID, "");

        // Store the number of nodes in the graph, including the added source and sink nodes (+ 2)
        this.nodeCount = nodes.size() + 2;

        initEdgeNodes(nodes);
        fafospX();
    }


    /**
     * Finds the edge nodes of this graph and connects them to sentinels.
     *
     * @param nodes the list of nodes
     */
    private void initEdgeNodes(final List<SequenceNode> nodes) {
        if (nodes.isEmpty()) {
            sourceNode.linkToRightNeighbour(sinkNode);
            return;
        }

        nodes.forEach(node -> {
            if (!node.hasLeftNeighbours()) {
                sourceNode.linkToRightNeighbour(node);
            }

            if (!node.hasRightNeighbours()) {
                sinkNode.linkToLeftNeighbour(node);
            }
        });
    }

    /**
     * Returns the start node.
     *
     * @return the start node.
     */
    public SequenceNode getSourceNode() {
        return sourceNode;
    }

    /**
     * Returns the end node.
     *
     * @return the end node.
     */
    public SequenceNode getSinkNode() {
        return sinkNode;
    }

    /**
     * Returns the size of the graph, measured in terms of the number of nodes.
     *
     * @return the number of nodes in the graph.
     */
    public int size() {
        return nodeCount;
    }


    /**
     * Calculates the optimal horizontal position of each {@code SequenceNode} using FAFOSP. The nodes are visited in
     * breadth-first search order.
     */
    private void fafospX() {
        final Queue<SequenceNode> queue = new LinkedList<>();
        queue.addAll(sourceNode.getRightNeighbours());

        while (!queue.isEmpty()) {
            final SequenceNode node = queue.remove();
            node.fafospX();

            queue.addAll(node.getRightNeighbours());
        }
    }

    /**
     * Returns a breadth-first {@code Iterator} that traverses from left to right.
     *
     * @return a breadth-first {@code Iterator} that traverses from left to right.
     */
    @Override
    public Iterator<SequenceNode> iterator() {
        return new BreadthFirstIterator(sourceNode, SequenceDirection.RIGHT);
    }

    /**
     * Returns a breadth-first {@code Iterator} with custom duplicate detection that traverses from left to right.
     *
     * @param duplicateDetector a {@code Function} that returns {@code true} iff. the {@code SequenceNode} has been
     *                          visited
     * @return a breadth-first {@code Iterator} with custom duplicate detection that traverses from left to right.
     */
    public Iterator<SequenceNode> iterator(final Function<SequenceNode, Boolean> duplicateDetector) {
        return new BreadthFirstIterator(sourceNode, SequenceDirection.RIGHT, duplicateDetector);
    }

    /**
     * Returns a breadth-first {@code Iterator} that traverses from right to left.
     *
     * @return a breadth-first {@code Iterator} that traverses from right to left.
     */
    public Iterator<SequenceNode> reverseIterator() {
        return new BreadthFirstIterator(sinkNode, SequenceDirection.LEFT);
    }

    /**
     * Returns a breadth-first {@code Iterator} with custom duplicate detection that traverses from right to left.
     *
     * @param duplicateDetector a {@code Function} that returns {@code true} iff. the {@code SequenceNode} has been
     *                          visited
     * @return a breadth-first {@code Iterator} with custom duplicate detection that traverses from right to left.
     */
    public Iterator<SequenceNode> reverseIterator(final Function<SequenceNode, Boolean> duplicateDetector) {
        return new BreadthFirstIterator(sinkNode, SequenceDirection.LEFT, duplicateDetector);
    }


    /**
     * An iterator that iterates in breadth-first order over {@code SequenceNode}s.
     */
    static class BreadthFirstIterator implements Iterator<SequenceNode> {
        private final Queue<SequenceNode> queue;
        private final SequenceDirection direction;
        private final Function<SequenceNode, Boolean> duplicateDetector;


        /**
         * Creates a new {@code BreadthFirstIterator} over the nodes connected to the given node.
         *
         * @param startNode the root {@code SequenceNode}
         * @param direction {@code true} if the iterator should go to the right, or {@code false} if the iterator should
         *                  go to the left
         */
        BreadthFirstIterator(final SequenceNode startNode, final SequenceDirection direction) {
            this(startNode, direction, node -> false);
        }

        /**
         * Creates a new {@code BreadthFirstIterator} with custom duplicate detection over the nodes connected to the
         * given node.
         * <p>
         * The custom {@code duplicateDetector} can be used to indicate that a node has already been visited. This
         * can be used when you change the nodes while iterating, and this change results in a detectable property.
         *
         * @param startNode         the root {@code SequenceNode}
         * @param direction         {@code true} if the iterator should go to the right, or {@code false} if the
         *                          iterator should go to the left
         * @param duplicateDetector a {@code Function} that returns {@code true} iff. the {@code SequenceNode} has been
         *                          visited
         */
        BreadthFirstIterator(final SequenceNode startNode, final SequenceDirection direction,
                             final Function<SequenceNode, Boolean> duplicateDetector) {
            this.direction = direction;
            this.duplicateDetector = duplicateDetector;

            queue = new LinkedList<>();
            queue.addAll(direction.ternary(startNode.getLeftNeighbours(), startNode.getRightNeighbours()));
        }


        @Override
        public boolean hasNext() {
            SequenceNode head = queue.peek();
            if (head == null) {
                return false;
            }

            while (duplicateDetector.apply(head)) {
                // Head can never be null because there is always the sentinel
                // A sentinel can never have been visited before
                head = queue.remove();
            }

            return !isSentinel(head);
        }

        @Override
        public SequenceNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the iterator");
            }

            final SequenceNode head = queue.remove();
            queue.addAll(getNeighbours(head));
            return head;
        }


        /**
         * Returns true iff. the given {@code SequenceNode} is a sentinel node.
         *
         * @param node a {@code SequenceNode}
         * @return true iff. the given {@code SequenceNode} is a sentinel node.
         */
        private boolean isSentinel(final SequenceNode node) {
            return !node.hasLeftNeighbours() || !node.hasRightNeighbours();
        }

        /**
         * Returns the neighbours of the node relevant to this iterator.
         *
         * @param node a {@code SequenceNode}
         * @return the neighbours of the node relevant to this iterator.
         */
        private List<SequenceNode> getNeighbours(final SequenceNode node) {
            return direction.ternary(node.getLeftNeighbours(), node.getRightNeighbours());
        }
    }
}
