package org.dnacronym.hygene.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.Edge;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Subgraph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The class with calculate the genome paths.
 */
public class PathCalculator {
    private static final Logger LOGGER = LogManager.getLogger(PathCalculator.class);

    private Subgraph subgraph;

    private Set<Edge> visited;


    public PathCalculator(Subgraph subgraph) {
        this.subgraph = subgraph;
        this.visited = new HashSet<>();
    }


    public void computePaths(final Subgraph subgraph) {
        // Determine start
        List<NewNode> sourceConnectedNodes = subgraph.getNodes().stream()
                .filter(n -> subgraph.getNeighbours(n, SequenceDirection.LEFT).isEmpty())
                .collect(Collectors.toList());

        // Compute topological ordering
        final Queue<NewNode> toVisit = new LinkedList<>();

        HashSet<NewNode> visited = new HashSet<>();

        toVisit.addAll(sourceConnectedNodes);

        while (!toVisit.isEmpty()) {
            final NewNode active = toVisit.remove();


        }

        // Compute paths


//        final List<NewNode> sourceConnectedNodes = subgraph.getSourceConnectedNodes();
//
//        final Collection<Segment> segments = subgraph.getSegments();
//
//        final Collection<NewNode> nodes = subgraph.getNodes();
//
//        List<NewNode> sourceConnectedSegments = segments.stream()
//                .filter(n -> subgraph.getNeighbours(n, SequenceDirection.LEFT).isEmpty())
//                .collect(Collectors.toList());
//
//
//
//
//
//        final NewNode source = new Segment(-1, -1, 0);
////        sourceConnectedNodes.forEach(sourceConnectedNode -> {
//            toVisit.add(new Edge(source, sourceConnectedNode));
//        });

//
//
//        // Build topological ordering
//        while (!toVisit.isEmpty()) {
//            NewNode active = toVisit.remove().getTo();
//
//            visited.add(active.getUuid());
//
//            if (active instanceof Segment) {
//                System.out.println("visiting a segement");
//
//                active.getOutgoingEdges().stream()
//                        .filter(e -> !visited.contains(e)).forEach(e -> );
//            } else {
//
//            }
//
//        }
//
//
//
//        System.out.println(sourceConnectedSegments);

//
//        final Set<NewNode> visited = new LinkedHashSet<>();
//        while (!queue.isEmpty()) {
//            final NewNode head = queue.remove();
//            if (visited.contains(head)) {
//                continue;
//            }
//
//            visited.add(head);
//
//            getNeighbours(head, direction).forEach(neighbour -> {
//                if (!visited.contains(neighbour) && nodes.containsValue(neighbour)) {
//                    queue.add(neighbour);
//                }
//            });
//        }
    }

//    public long numberOfUnvisitedEdges(NewNode node) {
//        node.getIncomingEdges().stream().filter(visited::contains).count();
//    }
//
//    public List<NewNode> getNeightboursWithoutIncomingEdges(NewNode node) {
//        node.getOutgoingEdges().stream().filter(e -> vi);
//
//        return neighbours.stream()
//                .filter(n -> n.getIncomingEdges().size() == 1)
//                .contains(node)).collect(Collectors.toList());
//    }
}
