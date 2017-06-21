package org.dnacronym.hygene.graph;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.edge.SimpleEdge;
import org.dnacronym.hygene.graph.node.GfaNode;
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
        final Multimap<GfaNode, Edge> incomingEdges = buildEdgeMap(subgraph, SequenceDirection.LEFT);
        final Multimap<GfaNode, Edge> outgoingEdges = buildEdgeMap(subgraph, SequenceDirection.RIGHT);

        final Map<GfaNode, Set<String>> genomeStore = new HashMap<>();

        final List<GfaNode> topologicalOrder = computeTopologicalOrder(subgraph, genomeStore, incomingEdges,
                outgoingEdges);

        final Map<Edge, Set<String>> paths = topologicalPathGeneration(topologicalOrder, incomingEdges, genomeStore);

        addPathsToEdges(paths);
    }

    /**
     * Builds a map of {@link Segment}s and their edges in the {@link Subgraph} for either the incoming or
     * outgoing {@link Edge}s.
     * <p>
     * {@link DummyEdge}s will not be added but their original edge, for which they are a diversion, will be added.
     *
     * @param subgraph          the {@link Subgraph}
     * @param sequenceDirection the {@link SequenceDirection}
     * @return map of {@link Edge}s for each {@link Segment}
     */
    private Multimap<GfaNode, Edge> buildEdgeMap(final Subgraph subgraph,
                                                 final SequenceDirection sequenceDirection) {
        final Multimap<GfaNode, Edge> edgeMap = HashMultimap.create();

        subgraph.getGfaNodes().forEach(segment -> {
            final Set<Edge> edges = sequenceDirection.ternary(segment.getIncomingEdges(), segment.getOutgoingEdges());

            edges.forEach(edge -> {
                if (edge instanceof DummyEdge) {
                    edgeMap.put(segment, ((DummyEdge) edge).getOriginalEdge());
                } else {
                    edgeMap.put(segment, edge);
                }
            });
        });

        return edgeMap;
    }

    /**
     * Computes a topological ordering for iterating the given {@link Subgraph}.
     *
     * @param subgraph      the {@link Subgraph}
     * @param genomeStore   the genome store
     * @param incomingEdges map of incoming {@link Edge}s for each {@link Segment}
     * @param outgoingEdges map of outgoing {@link Edge}s for each {@link Segment}
     * @return a topologically sorted list of the {@link Segment}s in the given {@link Subgraph}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<GfaNode> computeTopologicalOrder(final Subgraph subgraph, final Map<GfaNode, Set<String>> genomeStore,
                                                  final Multimap<GfaNode, Edge> incomingEdges,
                                                  final Multimap<GfaNode, Edge> outgoingEdges) {
        final Queue<Edge> toVisit = new LinkedList<>();

        final GfaNode origin = new Segment(-1, -1, 0);
        genomeStore.put(origin, new HashSet<>());

        final List<GfaNode> sourceConnectedNodes = getNodesWithNoIncomingEdges(subgraph);

        sourceConnectedNodes.forEach(sourceConnectedNode -> {
            toVisit.add(new SimpleEdge(origin, sourceConnectedNode));
            Optional.ofNullable(genomeStore.get(origin))
                    .ifPresent(originGenomes -> originGenomes.addAll(sourceConnectedNode.getMetadata().getGenomes()));
        });

        final List<GfaNode> topologicalOrder = new LinkedList<>();
        final HashSet<Edge> visitedEdges = new HashSet<>();
        final HashSet<GfaNode> visitedNodes = new HashSet<>();

        while (!toVisit.isEmpty()) {
            final GfaNode current = toVisit.remove().getToSegment();
            visitedNodes.add(current);
            topologicalOrder.add(current);

            visitedEdges.addAll(outgoingEdges.get(current));
            outgoingEdges.get(current).stream()
                    .filter(edge -> !visitedNodes.contains(edge.getTo()))
                    .filter(edge -> incomingEdges.get((GfaNode) edge.getTo()).stream()
                            .allMatch(visitedEdges::contains))
                    .forEach(toVisit::add);

            genomeStore.put(current, new HashSet<>(current.getMetadata().getGenomes()));
        }

        return topologicalOrder;
    }

    /**
     * Determines which {@link Segment}s do not have any incoming edges.
     * <p>
     * These {@link Segment}s are considered to be connected to the theoretical source node of the {@link Graph}.
     *
     * @param subgraph the subgraph
     * @return a list of nodes with no incoming edges
     */
    private List<GfaNode> getNodesWithNoIncomingEdges(final Subgraph subgraph) {
        return subgraph.getGfaNodes().stream()
                .filter(segment -> segment.getIncomingEdges().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Uses a topologically ordered set of {@link Segment}s to compute the edges' paths.
     *
     * @param topologicalOrder a topologically ordered list of {@link Segment}s
     * @param incomingEdges    map of incoming {@link Edge}s for each {@link Segment}
     * @param genomeStore      a map mapping each {@link Segment} to the genomes it is in
     * @return a mapping from {@link Edge}s to each of the genomes they're in
     */
    private Map<Edge, Set<String>> topologicalPathGeneration(final List<GfaNode> topologicalOrder,
                                                             final Multimap<GfaNode, Edge> incomingEdges,
                                                             final Map<GfaNode, Set<String>> genomeStore) {
        final Map<Edge, Set<String>> paths = new HashMap<>();

        // Go over topological order and assign genomes
        topologicalOrder.forEach(node -> incomingEdges.get(node).forEach(e -> {
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
    private void addPathsToEdges(final Map<Edge, Set<String>> paths) {
        paths.forEach(Edge::setGenomes);
    }
}
