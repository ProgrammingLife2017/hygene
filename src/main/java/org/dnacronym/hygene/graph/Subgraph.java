package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.models.SequenceDirection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Class representing a subgraph.
 */
public final class Subgraph {
    private final Set<Node> nodes;
    private final Set<Node> sourceNeighbours;
    private final Set<Node> sinkNeighbours;


    /**
     * Constructs a new, empty {@link Subgraph} instance.
     */
    public Subgraph() {
        this.nodes = new HashSet<>();
        this.sourceNeighbours = new HashSet<>();
        this.sinkNeighbours = new HashSet<>();
    }

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
     * Returns a {@link Collection} of all the {@link Node}s in this {@link Subgraph} in breadth-first order.
     *
     * @param direction the direction to traverse in
     * @return a {@link Collection} of all the {@link Node}s in this {@link Subgraph} in breadth-first order
     */
    public Collection<Node> getNodesBFS(final SequenceDirection direction) {
        final Queue<Node> queue = new LinkedList<>();
        queue.addAll(direction.ternary(sinkNeighbours, sourceNeighbours));

        final Set<Node> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            final Node head = queue.remove();
            if (visited.contains(head)) {
                continue;
            }

            visited.add(head);

            getNeighbours(head, direction).forEach(neighbour -> {
                if (!visited.contains(neighbour) && nodes.contains(neighbour)) {
                    queue.add(neighbour);
                }
            });
        }

        return visited;
    }

    /**
     * Returns a {@link Set} of the given node's neighbours.
     *
     * @param node      a {@link Node}
     * @param direction the direction of the neighbours
     * @return a {@link Set} of the given node's neighbours.
     */
    public Set<Node> getNeighbours(final Node node, final SequenceDirection direction) {
        final Predicate<Edge> filter = direction.ternary(
                edge -> nodes.contains(edge.getFrom()),
                edge -> nodes.contains(edge.getTo()));
        final Function<Edge, Node> mapper = direction.ternary(
                edge -> edge.getFrom(),
                edge -> edge.getTo());
        return direction.ternary(node.getIncomingEdges(), node.getOutgoingEdges()).stream()
                .filter(filter)
                .map(mapper)
                .collect(Collectors.toSet());
    }

    /**
     * Adds the given node to the set of nodes.
     *
     * @param node the node to be added
     * @return {@code true} iff. the subgraph was changed
     */
    public boolean addNode(final Node node) {
        final boolean changed = nodes.add(node);
        if (!changed) {
            return false;
        }

        if (isSourceNeighbour(node)) {
            sourceNeighbours.add(node);
        }
        if (isSinkNeighbour(node)) {
            sinkNeighbours.add(node);
        }
        sourceNeighbours.removeAll(node.getOutgoingEdges().stream()
                .map(Edge::getTo).collect(Collectors.toList()));
        sinkNeighbours.removeAll(node.getIncomingEdges().stream()
                .map(Edge::getFrom).collect(Collectors.toList()));

        return true;
    }

    /**
     * Adds the given nodes.
     *
     * @param nodes the nodes to be added
     * @return {@code true} iff. the subgraph was changed
     */
    public boolean addNodes(final Collection<Node> nodes) {
        final boolean changed = this.nodes.addAll(nodes);
        if (!changed) {
            return false;
        }

        detectSourceAndSinkNeighbours();
        return true;
    }

    /**
     * Clears all nodes from this subgraph.
     */
    public void clear() {
        nodes.clear();
        sourceNeighbours.clear();
        sinkNeighbours.clear();
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
     * Checks whether the given node is a neighbour of the subgraph source.
     *
     * @param node the node to be checked
     * @return {@code true} iff. the node is a neighbour of the subgraph source
     */
    private boolean isSourceNeighbour(final Node node) {
        return getNeighbours(node, SequenceDirection.LEFT).isEmpty();
    }

    /**
     * Checks whether the given node is a neighbour of the subgraph sink.
     *
     * @param node the node to be checked
     * @return {@code true} iff. the node is a neighbour of the subgraph sink
     */
    private boolean isSinkNeighbour(final Node node) {
        return getNeighbours(node, SequenceDirection.RIGHT).isEmpty();
    }
}
