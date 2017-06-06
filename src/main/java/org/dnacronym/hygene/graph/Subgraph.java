package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.models.SequenceDirection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Class representing a subgraph.
 */
public final class Subgraph {
    private final Set<Node> nodes;
    private final Map<UUID, Node> segments;


    /**
     * Constructs a new, empty {@link Subgraph} instance.
     */
    public Subgraph() {
        this(new HashSet<>());
    }

    /**
     * Constructs a new {@link Subgraph} instance.
     *
     * @param nodes the nodes of this subgraph
     */
    public Subgraph(final Set<Node> nodes) {
        this.nodes = nodes;
        this.segments = nodes.stream().collect(Collectors.toMap(Node::getUuid, node -> node));
    }


    /**
     * Returns the {@link Node} with the given {@link UUID}, or {code null} if no such node exists.
     *
     * @param nodeId a {@link UUID}
     * @return the {@link Node} with the given {@link UUID}, or {code null} if no such node exists.
     */
    public @Nullable Node getNode(final UUID nodeId) {
        return segments.get(nodeId);
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
        nodes.forEach(node -> {
            if (direction.ternary(isSinkNeighbour(node), isSourceNeighbour(node))) {
                queue.add(node);
            }
        });

        final Set<Node> visited = new LinkedHashSet<>();
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
        final Function<Edge, Node> mapper = direction.ternary(Edge::getFrom, Edge::getTo);
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
        segments.put(node.getUuid(), node);
        return nodes.add(node);
    }

    /**
     * Adds the given nodes.
     *
     * @param nodes the nodes to be added
     * @return {@code true} iff. the subgraph was changed
     */
    public boolean addNodes(final Collection<Node> nodes) {
        return this.nodes.addAll(nodes);
    }

    /**
     * Returns {@code true} iff. this subgraph contains a {@link Node} with the given {@link UUID}.
     *
     * @param nodeId a {@link UUID}
     * @return {@code true} iff. this subgraph contains a {@link Node} with the given {@link UUID}.
     */
    public boolean contains(final UUID nodeId) {
        return getNode(nodeId) != null;
    }

    /**
     * Removes the given node from the set of nodes.
     *
     * @param node the node to be removed
     */
    public void removeNode(final Node node) {
        nodes.remove(node);
    }

    /**
     * Clears all nodes from this subgraph.
     */
    public void clear() {
        nodes.clear();
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
