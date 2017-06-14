package org.dnacronym.hygene.models;

import org.dnacronym.hygene.graph.DummyNode;
import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
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
 * The class with calculate the genome paths.
 */
public final class PathCalculator {
    /**
     * The edges compute the path for each {@link Edge}.
     *
     * @param subgraph the {@link Subgraph} for which to compute the paths
     */
    public void computePaths(final Subgraph subgraph) {
        Map<NewNode, Set<String>> genomeStore = new HashMap<>();

        // Determine start
        List<NewNode> sourceConnectedNodes = getNodesWithNoIncomingEdges(subgraph);

        final Queue<Edge> toVisit = new LinkedList<>();

        final NewNode origin = new Segment(-1, -1, 0);
        genomeStore.put(origin, new HashSet<>());

        sourceConnectedNodes.forEach(sourceConnectedNode -> {
            toVisit.add(new Edge(origin, sourceConnectedNode));
            Optional.ofNullable(genomeStore.get(origin)).ifPresent(g ->
                    g.addAll(sourceConnectedNode.getMetadata().getGenomes()));
        });

        List<NewNode> topologicalOrder = new LinkedList<>();

        HashSet<Edge> visitedEdges = new HashSet<>();
        HashSet<NewNode> visitedNodes = new HashSet<>();

        while (!toVisit.isEmpty()) {
            NewNode active = toVisit.remove().getTo();
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

        // Generate paths
        Map<Edge, Set<String>> paths = topologicalPathGeneration(topologicalOrder, genomeStore);

        // Add paths to edges
        addPathsToEdges(paths);
    }

    /**
     * Gets the list of genomes for a specific {@link DummyNode}.
     *
     * @param dummyNode the {@link DummyNode}
     * @return list of genomes
     */
    public Set<String> getDummyNodeGenomes(final DummyNode dummyNode) {
        List<String> diversionSourceGenomes = dummyNode.getDiversionSource().getMetadata().getGenomes();
        List<String> diversionDestinationGenomes = dummyNode.getDiversionDestination().getMetadata().getGenomes();

        if (diversionDestinationGenomes.size() > diversionSourceGenomes.size()) {
            return new HashSet<>(diversionSourceGenomes);
        }

        return new HashSet<>(diversionDestinationGenomes);
    }

    /**
     * Determines which {@link NewNode}s do not have any incoming edges.
     * <p>
     * These {@link NewNode}s are considered to be connected the theoretical source node of the Graph.
     *
     * @param subgraph the subgraph
     * @return list of nodes with no incoming edges.
     */
    public List<NewNode> getNodesWithNoIncomingEdges(final Subgraph subgraph) {
        return subgraph.getNodes().stream()
                .filter(n -> subgraph.getNeighbours(n, SequenceDirection.LEFT).isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Uses a topologically ordered set of {@link NewNode}s to compute the edges paths.
     *
     * @param topologicalOrder the topological ordering
     * @param genomeStore      map containing the genomes from each node
     * @return map contains a set of genomes for each edge
     */
    public Map<Edge, Set<String>> topologicalPathGeneration(final List<NewNode> topologicalOrder,
                                                            final Map<NewNode, Set<String>> genomeStore) {
        // Create edges genome store
        Map<Edge, Set<String>> paths = new HashMap<>();

        // Go over topological order and assign importance
        topologicalOrder.forEach(node -> node.getIncomingEdges().forEach(e -> {
            final Set<String> nodeGenomes = genomeStore.get(node);
            final Set<String> originGenomes = genomeStore.get(e.getFrom());

            if (originGenomes != null && nodeGenomes != null) {
                final Set<String> intersection = new HashSet<>(originGenomes);
                intersection.retainAll(nodeGenomes);

                paths.put(e, intersection);

                originGenomes.removeAll(intersection);
            } else {
                throw new IllegalStateException("Missing genome data");
            }
        }));

        return paths;
    }

    /**
     * Will add a set of computed path to its corresponding {@link Edge}.
     *
     * @param paths the paths
     */
    public void addPathsToEdges(final Map<Edge, Set<String>> paths) {
        paths.forEach(Edge::setGenomes);
    }
}
