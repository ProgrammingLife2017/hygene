package org.dnacronym.hygene.models;

import java.util.LinkedList;
import java.util.Queue;


/**
 * A collection of methods for algorithmically determining the optimal position for the nodes in a {@link Graph}
 * using FAFOSP, the Felix Algorithm For Optimal Segment Positioning.
 */
public final class Fafosp {
    private final Graph graph;


    /**
     * Constructs a new {@link Fafosp}.
     *
     * @param graph the {@link Graph} to calculate positions for
     */
    public Fafosp(final Graph graph) {
        this.graph = graph;
    }


    /**
     * Calculates the optimal horizontal position of each node in the {@link Graph}.
     */
    public void horizontal() {
        final Queue<Integer> queue = new LinkedList<>();
        graph.iterator().visitDirectNeighbours(0, SequenceDirection.RIGHT, queue::add);
        graph.setUnscaledXPosition(0, 0);

        while (!queue.isEmpty()) {
            final Integer head = queue.remove();

            // Horizontal position may have been set since it was added to the queue
            if (graph.getUnscaledXPosition(head) >= 0) {
                continue;
            }

            horizontal(head);

            // Horizontal position cannot always be determined by FAFOSP-X
            if (graph.getUnscaledXPosition(head) >= 0) {
                // Add neighbours of which horizontal position was not set
                graph.iterator().visitDirectNeighbours(head, SequenceDirection.RIGHT, neighbour -> {
                    if (graph.getUnscaledXPosition(neighbour) < 0) {
                        queue.add(neighbour);
                    }
                });
            }
        }
    }

    /**
     * Calculates the optimal horizontal position relative to its left neighbours for the node with the given
     * identifier.
     *
     * @param id the node's identifier
     */
    private void horizontal(final int id) {
        final int[] width = {-1};
        graph.iterator().visitDirectNeighboursWhile(id, SequenceDirection.LEFT,
                neighbour -> graph.getUnscaledXPosition(neighbour) >= 0,
                ignored -> width[0] = -1,
                neighbour -> {
                    final int newWidth = graph.getUnscaledXPosition(neighbour) + 1;
                    if (newWidth > width[0]) {
                        width[0] = newWidth;
                    }
                }
        );

        if (width[0] >= 0) {
            graph.setUnscaledXPosition(id, width[0] + graph.getSequenceLength(id));
        }
    }
}
