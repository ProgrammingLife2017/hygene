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
            if (node.getIncomingEdges().stream()
                    .filter(edge -> nodes.contains(edge.getFrom())).collect(Collectors.toList())
                    .isEmpty()) {
                sourceNeighbours.add(node);
            }
            if (node.getOutgoingEdges().stream()
                    .filter(edge -> nodes.contains(edge.getTo())).collect(Collectors.toList())
                    .isEmpty()) {
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

        if (node.getIncomingEdges().isEmpty()) {
            sourceNeighbours.add(node);
        }
        sourceNeighbours.removeAll(node.getOutgoingEdges().stream()
                .map(Edge::getTo).collect(Collectors.toList()));

        if (node.getOutgoingEdges().isEmpty()) {
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
