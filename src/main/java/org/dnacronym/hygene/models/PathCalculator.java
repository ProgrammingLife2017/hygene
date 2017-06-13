package org.dnacronym.hygene.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The class with calculate the genome paths.
 */
public final class PathCalculator {
    private static final Logger LOGGER = LogManager.getLogger(PathCalculator.class);

    private Subgraph subgraph;
    private Map<NewNode, Set<String>> genomeStore;


    public PathCalculator(final Subgraph subgraph) {
        this.subgraph = subgraph;
        this.genomeStore = new HashMap<>();
    }


    public void computePaths(final Subgraph subgraph) {
        genomeStore.clear();

        // Determine start
        List<NewNode> sourceConnectedNodes = getNodesWithNoIncomingEdges(subgraph);

        final Queue<Edge> toVisit = new LinkedList<>();

        final NewNode origin = new Segment(-1, -1, 0);
        genomeStore.put(origin, new HashSet<>());

        sourceConnectedNodes.forEach(sourceConnectedNode -> {
            toVisit.add(new Edge(origin, sourceConnectedNode));
            genomeStore.get(origin).addAll(sourceConnectedNode.getMetadata().getGenomes());
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
                System.out.println("Visiting segment" + active);

                genomeStore.put(active, new HashSet<>(active.getMetadata().getGenomes()));
            } else if (active instanceof DummyNode) {
                System.out.println("Visiting dummy node " + active);

                List<String> genomes = ((DummyNode) active).getDiversionSource().getMetadata().getGenomes();
                genomeStore.put(active, new HashSet<>(genomes));
            } else {
                throw new IllegalStateException("Invalid node type");
            }
        }

        System.out.println(genomeStore);

        // Generate paths
        Map<Edge, Set<String>> paths = topologicalPathGeneration(topologicalOrder);

        // Add paths to edges
        addPathsToEdges(paths);
        System.out.println(paths);
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
     * @return map contains a set of genomes for each edge
     */
    public Map<Edge, Set<String>> topologicalPathGeneration(final List<NewNode> topologicalOrder) {
        // Create edges genome store
        Map<Edge, Set<String>> paths = new HashMap<>();

        // Go over topological order and assign importance
        topologicalOrder.forEach(node -> {
            node.getIncomingEdges().forEach(e -> {
                final Set<String> intersection = new HashSet<>(genomeStore.get(e.getFrom()));
                intersection.retainAll(genomeStore.get(node));

                paths.put(e, intersection);

                genomeStore.get(e.getFrom()).removeAll(intersection);
            });
        });

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
