package org.dnacronym.hygene.graph;

import com.google.common.collect.HashMultimap;
import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;

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
        final HashMultimap<Segment, Edge> incomingEdges = buildEdgeMap(subgraph, EdgeType.INCOMING);

        final HashMultimap<Segment, Edge> outgoingEdges = buildEdgeMap(subgraph, EdgeType.OUTGOING);

        final Map<Node, Set<String>> genomeStore = new HashMap<>();

        final List<Node> topologicalOrder = computeTopologicalOrder(subgraph, genomeStore);

        final Map<Edge, Set<String>> paths = topologicalPathGeneration(topologicalOrder, genomeStore);

        addPathsToEdges(paths);
    }

    /**
     * Builds a map of {@link Segment}s and their edges in the {@link Subgraph} for either the incoming or
     * outgoing {@link Edge}s.
     * <p>
     * {@link DummyEdge}s will not be added but their original edge, for which they for a diversion will be added.
     *
     * @param subgraph the {@link Subgraph}
     * @param edgeType the edge type {@link EdgeType}
     * @return the edge map
     */
    HashMultimap<Segment, Edge> buildEdgeMap(final Subgraph subgraph, final EdgeType edgeType) {
        HashMultimap<Segment, Edge> edgeMap = HashMultimap.create();

        subgraph.getSegments().forEach(s -> {
            final Set<Edge> edges = (edgeType == EdgeType.INCOMING) ? s.getIncomingEdges() : s.getOutgoingEdges();

            edges.stream().forEach(e -> {
                if (e instanceof DummyEdge) {
                    edgeMap.put(s, ((DummyEdge) e).getOriginalEdge());
                } else {
                    edgeMap.put(s, e);
                }
            });
        });

        return edgeMap;
    }

    /**
     * Computes a topological ordering for iterating the given {@link Subgraph}.
     *
     * @param subgraph    the {@link Subgraph}
     * @param genomeStore the genome store
     * @return a topologically sorted list of the {@link Node}s in the given {@link Subgraph}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<Node> computeTopologicalOrder(final Subgraph subgraph, final Map<Node, Set<String>> genomeStore) {
        final Queue<Edge> toVisit = new LinkedList<>();

        final Node origin = new Segment(-1, -1, 0);
        genomeStore.put(origin, new HashSet<>());

        final List<Node> sourceConnectedNodes = getNodesWithNoIncomingEdges(subgraph);

        sourceConnectedNodes.forEach(sourceConnectedNode -> {
            toVisit.add(new Edge(origin, sourceConnectedNode));
            Optional.ofNullable(genomeStore.get(origin)).ifPresent(g ->
                    g.addAll(sourceConnectedNode.getMetadata().getGenomes()));
        });

        final List<Node> topologicalOrder = new LinkedList<>();

        final HashSet<Edge> visitedEdges = new HashSet<>();
        final HashSet<Node> visitedNodes = new HashSet<>();

        while (!toVisit.isEmpty()) {
            final Node active = toVisit.remove().getTo();
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
     * Determines which {@link Node}s do not have any incoming edges.
     * <p>
     * These {@link Node}s are considered to be connected to the theoretical source node of the {@link Graph}.
     *
     * @param subgraph the subgraph
     * @return a list of nodes with no incoming edges
     */
    List<Node> getNodesWithNoIncomingEdges(final Subgraph subgraph) {
        return subgraph.getNodes().stream()
                .filter(node -> subgraph.getNeighbours(node, SequenceDirection.LEFT).isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Uses a topologically ordered set of {@link Node}s to compute the edges' paths.
     *
     * @param topologicalOrder a topologically ordered list of {@link Node}s
     * @param genomeStore      a map mapping each {@link Node} to the genomes it is in
     * @return a mapping from {@link Edge}s to each of the genomes they're in
     */
    Map<Edge, Set<String>> topologicalPathGeneration(final List<Node> topologicalOrder,
                                                     final Map<Node, Set<String>> genomeStore) {
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

    enum EdgeType {
        INCOMING,
        OUTGOING
    }
}
