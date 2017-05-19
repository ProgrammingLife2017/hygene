package org.dnacronym.hygene.models;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;


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
        fafospY();
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
     * @return the start node
     */
    public SequenceNode getSourceNode() {
        return sourceNode;
    }

    /**
     * Returns the end node.
     *
     * @return the end node
     */
    public SequenceNode getSinkNode() {
        return sinkNode;
    }

    /**
     * Returns the size of the graph, measured in terms of the number of nodes.
     *
     * @return the number of nodes in the graph
     */
    public int size() {
        return nodeCount;
    }


    /**
     * Returns a breadth-first {@code Iterator} that traverses from left to right.
     *
     * @return a breadth-first {@code Iterator} that traverses from left to right
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
     * @return a breadth-first {@code Iterator} with custom duplicate detection that traverses from left to right
     */
    public Iterator<SequenceNode> iterator(final Function<SequenceNode, Boolean> duplicateDetector) {
        return new BreadthFirstIterator(sourceNode, SequenceDirection.RIGHT, duplicateDetector);
    }

    /**
     * Returns a breadth-first {@code Iterator} that traverses from right to left.
     *
     * @return a breadth-first {@code Iterator} that traverses from right to left
     */
    public Iterator<SequenceNode> reverseIterator() {
        return new BreadthFirstIterator(sinkNode, SequenceDirection.LEFT);
    }

    /**
     * Returns a breadth-first {@code Iterator} with custom duplicate detection that traverses from right to left.
     *
     * @param duplicateDetector a {@code Function} that returns {@code true} iff. the {@code SequenceNode} has been
     *                          visited
     * @return a breadth-first {@code Iterator} with custom duplicate detection that traverses from right to left
     */
    public Iterator<SequenceNode> reverseIterator(final Function<SequenceNode, Boolean> duplicateDetector) {
        return new BreadthFirstIterator(sinkNode, SequenceDirection.LEFT, duplicateDetector);
    }


    /**
     * Calculates the optimal horizontal position of each {@code SequenceNode} using FAFOSP; the nodes are visited in
     * breadth-first search order.
     */
    private void fafospX() {
        final Queue<SequenceNode> queue = new LinkedList<>();
        queue.addAll(sourceNode.getRightNeighbours());
        sourceNode.setHorizontalRightEnd(0);

        while (!queue.isEmpty()) {
            final SequenceNode head = queue.remove();

            // Horizontal position may have been set since it was added to the queue
            if (head.getHorizontalRightEnd() < 0) {
                head.fafospX();

                // Horizontal position cannot always be determined by FAFOSP-X
                if (head.getHorizontalRightEnd() >= 0) {
                    // Add neighbours of which horizontal position was not set
                    head.getRightNeighbours().stream()
                            .filter(node -> node.getHorizontalRightEnd() < 0)
                            .collect(Collectors.toCollection(() -> queue));
                }
            }
        }
    }

    /**
     * Calculates the optimal vertical position of each {@code SequenceNode} using FAFOSP; the nodes are visited in
     * breadth-first search order.
     */
    private void fafospY() {
        fafospYInit();
        fafospYCalculate();
    }

    /**
     * Calculates the {@code leftHeight}, {@code rightHeight}, and {@code maximumHeight} properties for all
     * {@code SequenceNode}s (including the sentinels).
     */
    private void fafospYInit() {
        iterator(node -> node.getLeftHeight() >= 0)
                .forEachRemaining(node -> node.fafospYInit(SequenceDirection.LEFT));
        reverseIterator(node -> node.getRightHeight() >= 0)
                .forEachRemaining(node -> node.fafospYInit(SequenceDirection.RIGHT));
    }

    /**
     * Calculates the vertical positions for all {@code SequenceNode}s (including the sentinels).
     */
    private void fafospYCalculate() {
        final Queue<SequenceNode> queue = new LinkedList<>();
        queue.add(sourceNode);

        while (!queue.isEmpty()) {
            final SequenceNode head = queue.remove();

            // Do not revisit visited nodes
            head.getRightNeighbours().stream()
                    .filter(neighbour -> neighbour.getVerticalPosition() < 0)
                    .collect(Collectors.toCollection(() -> queue));

            // Calculate vertical position
            head.fafospYCalculate(sourceNode.getRightHeight() / 2);
        }
    }

    /**
     * Finds the first node whose vertical and horizontal positions match.
     * <p>
     * This gives the {@link SequenceNode} where the node's x is in bounds and the band is equal to the given band, or
     * {@code null} if no such band is found
     *
     * @param horizontalPosition x position of the node
     * @param verticalPosition   band the node is in
     * @return the {@link SequenceNode} which satisfies these conditions
     */
    public @Nullable SequenceNode getNode(final int horizontalPosition, final int verticalPosition) {
        SequenceNode foundNode = null;

        iterator(n -> !n.isVisited()).forEachRemaining(n -> n.setVisited(false));

        for (final Iterator<SequenceNode> it = iterator(SequenceNode::isVisited); it.hasNext(); ) {
            final SequenceNode node = it.next();
            node.setVisited(true);
            if (node.inBounds(horizontalPosition, verticalPosition)) {
                foundNode = node;
                break;
            }
        }

        return foundNode;
    }
}
