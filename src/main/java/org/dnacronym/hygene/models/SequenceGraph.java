package org.dnacronym.hygene.models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;


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
     * Returns a breadth-first {@code Iterator} over the {@code SequenceNode} from left to right.
     *
     * @return a breadth-first {@code Iterator} over the {@code SequenceNode} from left to right.
     */
    @Override
    public Iterator<SequenceNode> iterator() {
        return new BreadthFirstIterator(sourceNode, true);
    }

    /**
     * Returns a breadth-first {@code Iterator} over the {@code SequenceNode} from right to left.
     *
     * @return a breadth-first {@code Iterator} over the {@code SequenceNode} from right to left.
     */
    public Iterator<SequenceNode> reverseIterator() {
        return new BreadthFirstIterator(sinkNode, false);
    }


    /**
     * An iterator that iterates in breadth-first order over {@code SequenceNode}s.
     */
    private static class BreadthFirstIterator implements Iterator<SequenceNode> {
        private final Queue<SequenceNode> queue;
        private boolean direction;


        /**
         * Creates a new {@code BreadthFirstIterator} over the nodes connected to the given node.
         *
         * @param startNode the root {@code SequenceNode}
         * @param direction {@code true} if the iterator should go to the right, or {@code false} if the iterator should
         *                  go to the left
         */
        BreadthFirstIterator(final SequenceNode startNode, final boolean direction) {
            this.direction = direction;

            queue = new LinkedList<>();
            if (direction) {
                queue.addAll(startNode.getRightNeighbours());
            } else {
                queue.addAll(startNode.getLeftNeighbours());
            }
        }


        @Override
        public boolean hasNext() {
            if (queue.isEmpty()) {
                return false;
            }

            final SequenceNode head = queue.peek();
            return head != null && (direction ? head.hasRightNeighbours() : head.hasLeftNeighbours());

        }

        @Override
        public SequenceNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the iterator");
            }

            final SequenceNode head = queue.remove();
            queue.addAll(direction ? head.getRightNeighbours() : head.getLeftNeighbours());
            return head;
        }
    }
}
