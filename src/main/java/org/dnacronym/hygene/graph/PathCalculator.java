package org.dnacronym.hygene.graph;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.edge.SimpleEdge;
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
        final List<Segment> segments = subgraph.getGfaNodes().stream()
                .flatMap(gfaNode -> gfaNode.getSegments().stream())
                .collect(Collectors.toList());

        final Multimap<Segment, Edge> incomingEdges = buildEdgeMap(segments, SequenceDirection.LEFT);
        final Multimap<Segment, Edge> outgoingEdges = buildEdgeMap(segments, SequenceDirection.RIGHT);

        final Map<Segment, Set<String>> genomeStore = new HashMap<>();

        final List<Segment> topologicalOrder = computeTopologicalOrder(segments, genomeStore, incomingEdges,
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
     * @param segments          the {@link Segment}s
     * @param sequenceDirection the {@link EdgeDirection}
     * @return map of {@link Edge}s for each {@link Segment}
     */
    private Multimap<Segment, Edge> buildEdgeMap(final List<Segment> segments,
                                                 final SequenceDirection sequenceDirection) {
        final Multimap<Segment, Edge> edgeMap = HashMultimap.create();

        segments.forEach(segment -> {
            final Set<Edge> edges = sequenceDirection == SequenceDirection.LEFT
                    ? segment.getIncomingEdges()
                    : segment.getOutgoingEdges();

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
     * @param segments      the {@link Subgraph}
     * @param genomeStore   the genome store
     * @param incomingEdges map of incoming {@link Edge}s for each {@link Segment}
     * @param outgoingEdges map of outgoing {@link Edge}s for each {@link Segment}
     * @return a topologically sorted list of the {@link Segment}s in the given {@link Subgraph}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<Segment> computeTopologicalOrder(final List<Segment> segments,
                                                  final Map<Segment, Set<String>> genomeStore,
                                                  final Multimap<Segment, Edge> incomingEdges,
                                                  final Multimap<Segment, Edge> outgoingEdges) {
        final Queue<Edge> toVisit = new LinkedList<>();

        final Segment origin = new Segment(-1, -1, 0);
        genomeStore.put(origin, new HashSet<>());

        final List<Segment> sourceConnectedNodes = getNodesWithNoIncomingEdges(segments);

        sourceConnectedNodes.forEach(sourceConnectedNode -> {
            toVisit.add(new SimpleEdge(origin, sourceConnectedNode));
            Optional.ofNullable(genomeStore.get(origin)).ifPresent(g ->
                    g.addAll(sourceConnectedNode.getMetadata().getGenomes()));
        });

        final List<Segment> topologicalOrder = new LinkedList<>();

        final HashSet<Edge> visitedEdges = new HashSet<>();
        final HashSet<Segment> visitedNodes = new HashSet<>();

        while (!toVisit.isEmpty()) {
            final Segment active = (Segment) toVisit.remove().getTo();
            visitedNodes.add(active);
            topologicalOrder.add(active);

            visitedEdges.addAll(outgoingEdges.get(active));

            outgoingEdges.get(active).stream()
                    .filter(e -> !visitedNodes.contains(e.getTo()) && incomingEdges.get((Segment) e.getTo()).stream()
                            .filter(out -> !visitedEdges.contains(out)).count() == 0)
                    .forEach(toVisit::add);

            genomeStore.put(active, new HashSet<>(active.getMetadata().getGenomes()));
        }

        return topologicalOrder;
    }

    /**
     * Determines which {@link Segment}s do not have any incoming edges.
     * <p>
     * These {@link Segment}s are considered to be connected to the theoretical source node of the {@link Graph}.
     *
     * @param segments the subgraph
     * @return a list of nodes with no incoming edges
     */
    private List<Segment> getNodesWithNoIncomingEdges(final List<Segment> segments) {
        return segments.stream()
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
    private Map<Edge, Set<String>> topologicalPathGeneration(final List<Segment> topologicalOrder,
                                                             final Multimap<Segment, Edge> incomingEdges,
                                                             final Map<Segment, Set<String>> genomeStore) {
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
