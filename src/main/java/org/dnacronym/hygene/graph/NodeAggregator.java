package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.edge.AggregateEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.AggregateNode;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Aggregator of nodes to achieve semantic zooming.
 */
public final class NodeAggregator {
    private final Node startNode;
    private final List<Node> neighbours;
    private @MonotonicNonNull Node endNode;


    /**
     * Constructs a new {@link NodeAggregator} for a particular {@link Node}.
     *
     * @param node the {@link Node} to aggregate
     */
    private NodeAggregator(final Node node) {
        this.startNode = node;
        this.neighbours = node.getOutgoingEdges().stream()
                .map(Edge::getTo)
                .collect(Collectors.toList());
    }


    /**
     * Aggregates the given node's neighbours, if possible.
     * <p>
     * Aggregation only happens when the given node has exactly two neighbours, these neighbours have a sequence length
     * of {@code 1}, and these neighbours have exactly one neighbour which is shared between them.
     *
     * @param node a node
     * @return the {@link AggregateNode} th neighbours are now part of, or {@code null} if no aggregation occurred
     */
    public static @Nullable AggregateNode aggregate(final Node node) {
        final NodeAggregator aggregator = new NodeAggregator(node);

        if (!aggregator.nodeHasValidNumberOfNeighbours()) {
            return null;
        }
        if (!aggregator.neighboursAreSegments()) {
            return null;
        }
        if (!aggregator.neighboursHaveSequenceLengthOne()) {
            return null;
        }
        if (!aggregator.neighboursHaveOneNeighbour()) {
            return null;
        }
        if (!aggregator.neighboursHaveSameNeighbour()) {
            return null;
        }
        if (!aggregator.neighboursAreOnlyNeighboursOfTheirNeighbour()) {
            return null;
        }

        return aggregator.aggregate();
    }

    /**
     * Aggregates the node's neighbours into an {@link AggregateNode}, and rewires the edges from and to that node.
     *
     * @return an {@link AggregateNode}
     */
    private AggregateNode aggregate() {
        final Collection<Node> aggregatedNodes = new ArrayList<>();
        aggregatedNodes.add(neighbours.get(0));
        aggregatedNodes.add(neighbours.get(1));
        final AggregateNode aggregateNode = new AggregateNode(aggregatedNodes);

        final Edge toAggregateNode = new AggregateEdge(startNode, aggregateNode, startNode.getOutgoingEdges());
        startNode.getOutgoingEdges().clear();
        startNode.getOutgoingEdges().add(toAggregateNode);
        aggregateNode.getIncomingEdges().add(toAggregateNode);

        final Edge fromAggregateNode = new AggregateEdge(aggregateNode, getEndNode(), getEndNode().getIncomingEdges());
        getEndNode().getIncomingEdges().clear();
        getEndNode().getIncomingEdges().add(fromAggregateNode);
        aggregateNode.getOutgoingEdges().add(fromAggregateNode);

        return aggregateNode;
    }


    /**
     * Returns {@code true} iff. the node has exactly two neighbours to its right.
     *
     * @return {@code true} iff. the node has exactly two neighbours to its right
     */
    private boolean nodeHasValidNumberOfNeighbours() {
        return neighbours.size() == 2;
    }

    /**
     * Returns {@code true} iff. both neighbours are {@link Segment}s.
     *
     * @return {@code true} iff. both neighbours are {@link Segment}s
     */
    private boolean neighboursAreSegments() {
        return neighbours.get(0) instanceof Segment
                && neighbours.get(1) instanceof Segment;
    }

    /**
     * Returns {@code true} iff. both neighbours' sequences consist of one base.
     *
     * @return {@code true} iff. both neighbours' sequences consist of one base
     */
    private boolean neighboursHaveSequenceLengthOne() {
        return ((Segment) neighbours.get(0)).getSequenceLength() == 1
                && ((Segment) neighbours.get(1)).getSequenceLength() == 1;
    }

    /**
     * Returns {@code true} iff. both neighbours each have exactly one neighbour to their right.
     *
     * @return {@code true} iff. both neighbours each have exactly one neighbour to their right
     */
    private boolean neighboursHaveOneNeighbour() {
        final Node neighbourA = neighbours.get(0);
        final Node neighbourB = neighbours.get(1);

        return neighbourA.getOutgoingEdges().size() == 1
                && neighbourB.getOutgoingEdges().size() == 1;
    }

    /**
     * Returns {@code true} iff. the neighbour of both neighbours is the same node.
     *
     * @return {@code true} iff. the neighbour of both neighbours is the same node
     */
    private boolean neighboursHaveSameNeighbour() {
        final Node neighbourA = neighbours.get(0);
        final Node neighbourB = neighbours.get(1);

        final Node neighbourANeighbour = neighbourA.getOutgoingEdges().iterator().next().getTo();
        final Node neighbourBNeighbour = neighbourB.getOutgoingEdges().iterator().next().getTo();

        return neighbourANeighbour.equals(neighbourBNeighbour);
    }

    /**
     * Returns {@code true} iff. the neighbours are the only neighbours of their shared neighbours.
     *
     * @return {@code true} iff. the neighbours are the only neighbours of their shared neighbours
     */
    private boolean neighboursAreOnlyNeighboursOfTheirNeighbour() {
        return getEndNode().getIncomingEdges().size() == 2;
    }

    /**
     * Returns the node's first neighbour's first neighbour.
     *
     * @return the node's first neighbour's first neighbour
     */
    @EnsuresNonNull("endNode")
    private Node getEndNode() {
        if (endNode == null) {
            endNode = neighbours.get(0).getOutgoingEdges().iterator().next().getTo();
        }

        return endNode;
    }
}
