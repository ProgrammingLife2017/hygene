package org.dnacronym.hygene.models;

import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.NewNode;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.graph.Subgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Calculates genome paths and edge thickness.
 */
public final class PathCalculator {
    /**
     * Computes the genomes in which each {@link Edge} in the given {@link Subgraph} is.
     *
     * @param subgraph the {@link Subgraph} for which to compute the paths
     */
    public void computePaths(final Subgraph subgraph) {
        final Map<NewNode, Set<String>> genomeStore = new HashMap<>();

        final List<NewNode> topologicalOrder = computeTopologicalOrder(subgraph, genomeStore);

        final Map<Edge, Set<String>> paths = topologicalPathGeneration(topologicalOrder, genomeStore);

        addPathsToEdges(paths);
    }

    /**
     * Computes a topological ordering for iterating the given {@link Subgraph}.
     *
     * @param subgraph    the {@link Subgraph}
     * @param genomeStore the genome store
     * @return a topologically sorted list of the {@link NewNode}s in the given {@link Subgraph}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<NewNode> computeTopologicalOrder(final Subgraph subgraph, final Map<NewNode, Set<String>> genomeStore) {
        final Queue<Edge> toVisit = new LinkedList<>();

        final NewNode origin = new Segment(-1, -1, 0);
        genomeStore.put(origin, new HashSet<>());

        final List<NewNode> sourceConnectedNodes = getNodesWithNoIncomingEdges(subgraph);

        sourceConnectedNodes.forEach(sourceConnectedNode -> {
            toVisit.add(new Edge(origin, sourceConnectedNode));
            Optional.ofNullable(genomeStore.get(origin)).ifPresent(g ->
                    g.addAll(sourceConnectedNode.getMetadata().getGenomes()));
        });

        final List<NewNode> topologicalOrder = new LinkedList<>();

        final HashSet<Edge> visitedEdges = new HashSet<>();
        final HashSet<NewNode> visitedNodes = new HashSet<>();

        while (!toVisit.isEmpty()) {
            final NewNode active = toVisit.remove().getTo();
            visitedNodes.add(active);
            topologicalOrder.add(active);

            visitedEdges.addAll(active.getOutgoingEdges());

            active.getOutgoingEdges().stream()
                    .filter(e -> !visitedNodes.contains(e.getTo()) && e.getTo().getIncomingEdges().stream()
                            .filter(out -> !visitedEdges.contains(out)).count() == 0)
                    .forEach(toVisit::add);

            if (active instanceof Segment) {
                genomeStore.put(active, new HashSet<>(active.getMetadata().getGenomes()));
            } else if (active instanceof DummyNode) {
                genomeStore.put(active, getDummyNodeGenomes((DummyNode) active));
            } else {
                throw new IllegalStateException("Invalid node type.");
            }
        }

        return topologicalOrder;
    }

    /**
     * Returns the list of genomes for the given {@link DummyNode}.
     *
     * @param dummyNode a {@link DummyNode}
     * @return the list of genomes for the given {@link DummyNode}
     */
    Set<String> getDummyNodeGenomes(final DummyNode dummyNode) {
        final List<String> diversionSourceGenomes = dummyNode.getDiversionSource().getMetadata().getGenomes();
        final List<String> diversionDestinationGenomes = dummyNode.getDiversionDestination().getMetadata().getGenomes();

        if (diversionDestinationGenomes.size() > diversionSourceGenomes.size()) {
            return new HashSet<>(diversionSourceGenomes);
        }

        return new HashSet<>(diversionDestinationGenomes);
    }

    /**
     * Determines which {@link NewNode}s do not have any incoming edges.
     * <p>
     * These {@link NewNode}s are considered to be connected to the theoretical source node of the {@link Graph}.
     *
     * @param subgraph the subgraph
     * @return a list of nodes with no incoming edges
     */
    List<NewNode> getNodesWithNoIncomingEdges(final Subgraph subgraph) {
        return subgraph.getNodes().stream()
                .filter(node -> subgraph.getNeighbours(node, SequenceDirection.LEFT).isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Uses a topologically ordered set of {@link NewNode}s to compute the edges' paths.
     *
     * @param topologicalOrder a topologically ordered list of {@link NewNode}s
     * @param genomeStore      a map mapping each {@link NewNode} to the genomes it is in
     * @return a mapping from {@link Edge}s to each of the genomes they're in
     */
    Map<Edge, Set<String>> topologicalPathGeneration(final List<NewNode> topologicalOrder,
                                                     final Map<NewNode, Set<String>> genomeStore) {
        // Create edges genome store
        final Map<Edge, Set<String>> paths = new HashMap<>();

        // Go over topological order and assign importance
        topologicalOrder.forEach(node -> node.getIncomingEdges().forEach(e -> {
            final Set<String> nodeGenomes = genomeStore.get(node);
            final Set<String> originGenomes = genomeStore.get(e.getFrom());

            if (originGenomes == null || nodeGenomes == null) {
                throw new IllegalStateException("Missing genome data");
            }

            final Set<String> intersection = new HashSet<>(originGenomes);
            intersection.retainAll(nodeGenomes);

            paths.put(e, intersection);

            originGenomes.removeAll(intersection);
        }));

        return paths;
    }

    /**
     * Adds a set of computed path to its corresponding {@link Edge}.
     *
     * @param paths the paths
     */
    void addPathsToEdges(final Map<Edge, Set<String>> paths) {
        paths.forEach(Edge::setGenomes);
    }
}
