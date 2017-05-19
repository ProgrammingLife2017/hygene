package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Parses a {@link SequenceAlignmentGraph} to a {@link SequenceGraph}.
 */
public final class SequenceAlignmentGraphParser {
    /**
     * Translates a {@link SequenceAlignmentGraph} into a {@link SequenceGraph}.
     *
     * @param graph a {@link SequenceAlignmentGraph}
     * @return a {@link SequenceGraph}
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
     * Returns the {@link SequenceNode} corresponding to the given {@link Segment} from the {@link Map}, or adds it to
     * the {@link Map} if it does not exist.
     *
     * @param nodes   a {@link Map} of {@link SequenceNode}s
     * @param segment the {@link Segment} to look for
     * @return {@link SequenceNode} corresponding to the given {@link Segment}
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
