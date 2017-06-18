package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.GraphIterator;
import org.dnacronym.hygene.graph.SequenceDirection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


/**
 * A collection of methods for algorithmically determining the optimal position for the nodes in a {@link Graph}
 * using FAFOSP, the Felix Algorithm For Optimal Segment Positioning.
 */
public final class Fafosp {
    private static final int COLUMN_WIDTH = 1000;

    private final Graph graph;
    private final int[][] nodeArrays;
    private final GraphIterator iterator;


    /**
     * Constructs a new {@link Fafosp}.
     *
     * @param graph the {@link Graph} to calculate positions for
     */
    public Fafosp(final Graph graph) {
        this.graph = graph;
        this.nodeArrays = graph.getNodeArrays();
        this.iterator = new GraphIterator(graph);
    }


    /**
     * Calculates the optimal horizontal position of each node in the {@link Graph}.
     */
    public void horizontal() {
        final long[] xPositions = new long[nodeArrays.length];
        Arrays.fill(xPositions, -1);

        final Queue<Integer> queue = new LinkedList<>();
        iterator.visitDirectNeighbours(0, SequenceDirection.RIGHT, queue::add);
        xPositions[0] = 0;

        while (!queue.isEmpty()) {
            final Integer head = queue.remove();

            // Horizontal position may have been set since it was added to the queue
            if (xPositions[head] >= 0) {
                continue;
            }

            horizontal(xPositions, head);

            // Horizontal position cannot always be determined by FAFOSP-X
            if (xPositions[head] >= 0) {
                // Add neighbours of which horizontal position was not set
                iterator.visitDirectNeighbours(head, SequenceDirection.RIGHT, neighbour -> {
                    if (xPositions[neighbour] < 0) {
                        queue.add(neighbour);
                    }
                });
            }
        }

        for (int i = 0; i < xPositions.length; i++) {
            graph.setUnscaledXPosition(i, (int) (xPositions[i] / COLUMN_WIDTH));
        }
    }

    /**
     * Calculates the optimal horizontal position relative to its left neighbours for the node with the given
     * identifier.
     *
     * @param id         the node's identifier
     * @param xPositions array of {@code long}s indicating x positions of nodes, indexed by node id
     */
    private void horizontal(final long[] xPositions, final int id) {
        final long[] width = {-1}; // Edge count, sequence length
        iterator.visitDirectNeighboursWhile(id, SequenceDirection.LEFT,
                neighbour -> xPositions[neighbour] >= 0,
                ignored -> width[0] = -1,
                neighbour -> width[0] = Math.max(
                        width[0],
                        xPositions[neighbour] + graph.getLength(neighbour)
                )
        );

        if (width[0] >= 0) {
            final long horizontalPosition = width[0] + COLUMN_WIDTH;
            xPositions[id] = ((horizontalPosition + COLUMN_WIDTH - 1) / COLUMN_WIDTH) * COLUMN_WIDTH;
        }
    }
}
