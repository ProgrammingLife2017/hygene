package org.dnacronym.hygene.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Class representing a subgraph.
 */
public final class Subgraph {
    private final Set<Node> nodes;
    private final Set<Node> sourceNeighbours;
    private final Set<Node> sinkNeighbours;


    /**
     * Constructs a new {@link Subgraph} instance.
     *
     * @param nodes the nodes of this subgraph
     */
    public Subgraph(final Set<Node> nodes) {
        this.nodes = nodes;

        sourceNeighbours = new HashSet<>();
        sinkNeighbours = new HashSet<>();

        detectSourceAndSinkNeighbours();
    }


    /**
     * Updates the lists of source and sink neighbours, based on the current node set.
     */
    private void detectSourceAndSinkNeighbours() {
        sourceNeighbours.clear();
        sinkNeighbours.clear();

        nodes.forEach(node -> {
            if (isSourceNeighbour(node)) {
                sourceNeighbours.add(node);
            }
            if (isSinkNeighbour(node)) {
                sinkNeighbours.add(node);
            }
        });
    }

    /**
     * Returns the nodes.
     * <p>
     * The returned set is an immutable view of the actual set.
     *
     * @return the nodes
     */
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    /**
     * Adds the given node to the set of nodes.
     *
     * @param node the node to be added
     */
    public void addNode(final Node node) {
        nodes.add(node);

        if (isSourceNeighbour(node)) {
            sourceNeighbours.add(node);
        }
        sourceNeighbours.removeAll(node.getOutgoingEdges().stream()
                .map(Edge::getTo).collect(Collectors.toList()));

        if (isSinkNeighbour(node)) {
            sinkNeighbours.add(node);
        }
        sinkNeighbours.removeAll(node.getIncomingEdges().stream()
                .map(Edge::getFrom).collect(Collectors.toList()));
    }

    /**
     * Removes the given node from the set of nodes.
     *
     * @param node the node to be removed
     */
    public void removeNode(final Node node) {
        nodes.remove(node);
        detectSourceAndSinkNeighbours();
    }

    /**
     * Checks whether the given node is a neighbour of the subgraph source.
     *
     * @param node the node to be checked
     * @return {@code true} iff. the node is a neighbour of the subgraph source
     */
    private boolean isSourceNeighbour(final Node node) {
        return node.getIncomingEdges().stream()
                .filter(edge -> nodes.contains(edge.getFrom()))
                .map(Edge::getFrom)
                .collect(Collectors.toSet())
                .isEmpty();
    }

    /**
     * Checks whether the given node is a neighbour of the subgraph sink.
     *
     * @param node the node to be checked
     * @return {@code true} iff. the node is a neighbour of the subgraph sink
     */
    private boolean isSinkNeighbour(final Node node) {
        return node.getOutgoingEdges().stream()
                .filter(edge -> nodes.contains(edge.getTo()))
                .map(Edge::getTo)
                .collect(Collectors.toSet())
                .isEmpty();
    }

    /**
     * Returns the neighbours of the source node.
     * <p>
     * The returned set is an immutable view of the actual set.
     *
     * @return the neighbours of the source node
     */
    Set<Node> getSourceNeighbours() {
        return Collections.unmodifiableSet(sourceNeighbours);
    }

    /**
     * Returns the neighbours of the sink node.
     * <p>
     * The returned set is an immutable view of the actual set.
     *
     * @return the neighbours of the sink node
     */
    Set<Node> getSinkNeighbours() {
        return Collections.unmodifiableSet(sinkNeighbours);
    }
}
