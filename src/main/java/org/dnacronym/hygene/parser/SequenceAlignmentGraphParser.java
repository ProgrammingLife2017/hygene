package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Parses a {@code SequenceAlignmentGraph} to a {@code SequenceGraph}.
 */
public final class SequenceAlignmentGraphParser {
    /**
     * Translates a {@code SequenceAlignmentGraph} into a {@code GraphSequence}.
     *
     * @param graph a {@code SequenceAlignmentGraph}
     * @return a {@code GraphSequence}
     * @throws ParseException if the graph is not according to the specification
     */
    public SequenceGraph parse(final SequenceAlignmentGraph graph) throws ParseException {
        final Map<String, SequenceNode> nodes = new HashMap<>();

        for (final Segment segment : graph.getSegments()) {
            getNode(nodes, graph.getSegment(segment.getName()));
        }

        for (final Link link : graph.getLinks()) {
            final SequenceNode fromNode = getNode(nodes, graph.getSegment(link.getFrom()));
            final SequenceNode toNode = getNode(nodes, graph.getSegment(link.getTo()));

            fromNode.addRightNeighbour(toNode);
            toNode.addLeftNeighbour(fromNode);
        }

        return nodes.values().stream().findFirst()
                .map(node -> new SequenceGraph(new ArrayList<>(nodes.values())))
                .orElseThrow(() -> new ParseException("Start and end node could not be determined"));
    }


    /**
     * Returns the {@code SequenceNode} corresponding to the given {@code Segment} from the {@code Map}, or adds it to
     * the {@code Map} if it does not exist.
     *
     * @param nodes   a {@code Map} of {@code SequenceNode}s
     * @param segment the {@code Segment} to look for
     * @return {@code SequenceNode} corresponding to the given {@code Segment}
     */
    private SequenceNode getNode(final Map<String, SequenceNode> nodes, final Segment segment) {
        final SequenceNode node = nodes.get(segment.getName());

        if (node != null) {
            return node;
        }

        final SequenceNode node2 = new SequenceNode(segment.getName(), segment.getSequence());
        nodes.put(segment.getName(), node2);
        return node2;
    }
}
