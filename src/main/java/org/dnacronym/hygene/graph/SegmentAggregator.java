package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.graph.edge.AggregateEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.AggregateSegment;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Aggregator of nodes to achieve semantic zooming.
 * <p>
 * Aggregation only happens when the given node has exactly two right neighbours, these neighbours have a sequence
 * length of {@code 1}, and these neighbours have exactly one right neighbour which is shared between them.
 */
public final class SegmentAggregator {
    private final Node startNode;
    private final List<Segment> neighbours;
    private @MonotonicNonNull Node endNode;


    /**
     * Constructs a new {@link SegmentAggregator} for a particular {@link Node}.
     *
     * @param node the {@link Node} to aggregate
     */
    private SegmentAggregator(final Node node) {
        this.startNode = node;
        this.neighbours = node.getOutgoingEdges().stream()
                .map(Edge::getTo)
                .filter(neighbour -> neighbour instanceof Segment)
                .map(neighbour -> (Segment) neighbour)
                .collect(Collectors.toList());
    }


    /**
     * Aggregates as many nodes as possible.
     *
     * @param subgraph a {@link Subgraph}
     */
    public static void aggregate(final Subgraph subgraph) {
        final List<AggregateSegment> aggregateSegments = subgraph.getNodes().stream()
                .map(SegmentAggregator::aggregate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        aggregateSegments.forEach(aggregateSegment -> {
            subgraph.removeAll(aggregateSegment.getSegments());
            subgraph.add(aggregateSegment);
        });
    }

    /**
     * Aggregates the given node's neighbours, if possible.
     *
     * @param node a node
     * @return the {@link AggregateSegment} the neighbours are now part of, or {@code null} if no aggregation occurred
     */
    public static Optional<AggregateSegment> aggregate(final Node node) {
        final SegmentAggregator aggregator = new SegmentAggregator(node);

        if (!aggregator.nodeHasValidNumberOfNeighbours()) {
            return Optional.empty();
        }
        if (!aggregator.neighboursHaveSequenceLengthOne()) {
            return Optional.empty();
        }
        if (!aggregator.neighboursHaveOneNeighbour()) {
            return Optional.empty();
        }
        if (!aggregator.neighboursHaveSameNeighbour()) {
            return Optional.empty();
        }
        if (!aggregator.neighboursAreOnlyNeighboursOfTheirNeighbour()) {
            return Optional.empty();
        }

        return Optional.of(aggregator.aggregate());
    }

    /**
     * Aggregates the node's neighbours into an {@link AggregateSegment}, and rewires the edges from and to that node.
     *
     * @return an {@link AggregateSegment}
     */
    private AggregateSegment aggregate() {
        final Collection<Segment> aggregatedNodes = new ArrayList<>();
        aggregatedNodes.add(neighbours.get(0));
        aggregatedNodes.add(neighbours.get(1));
        final AggregateSegment aggregateSegment = new AggregateSegment(aggregatedNodes);

        final Edge toAggregateSegment = new AggregateEdge(startNode, aggregateSegment, startNode.getOutgoingEdges());
        startNode.getOutgoingEdges().clear();
        startNode.getOutgoingEdges().add(toAggregateSegment);
        aggregateSegment.getIncomingEdges().add(toAggregateSegment);

        final Edge fromAggregateSegment = new AggregateEdge(aggregateSegment, getEndNode(),
                getEndNode().getIncomingEdges());
        getEndNode().getIncomingEdges().clear();
        getEndNode().getIncomingEdges().add(fromAggregateSegment);
        aggregateSegment.getOutgoingEdges().add(fromAggregateSegment);

        return aggregateSegment;
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
     * Returns {@code true} iff. both neighbours' sequences consist of one base.
     *
     * @return {@code true} iff. both neighbours' sequences consist of one base
     */
    private boolean neighboursHaveSequenceLengthOne() {
        return neighbours.get(0).getSequenceLength() == 1
                && neighbours.get(1).getSequenceLength() == 1;
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
