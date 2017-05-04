package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;

import java.util.HashMap;
import java.util.Map;


/**
 * Parses a {@code SequenceAlignmentGraph} to a {@code SequenceGraph}.
 */
public final class SequenceAlignmentGraphParser {
    /**
     * Translates a {@code SequenceAlignmentGraph} into a {@code GraphSequence}.
     *
     * @param sag a {@code SequenceAlignmentGraph}
     * @return a {@code GraphSequence}
     * @throws ParseException if the sag is not according to the specification
     */
    public SequenceGraph parse(final SequenceAlignmentGraph sag) throws ParseException {
        final Map<String, SequenceNode> nodes = new HashMap<>();

        for (final Segment segment : sag.getSegments()) {
            getNode(nodes, sag.getSegment(segment.getName()));
        }

        for (final Link link : sag.getLinks()) {
            final SequenceNode fromNode = getNode(nodes, sag.getSegment(link.getFrom()));
            final SequenceNode toNode = getNode(nodes, sag.getSegment(link.getTo()));

            fromNode.addRightNeighbour(toNode);
            toNode.addLeftNeighbour(fromNode);
        }

        return nodes.values().stream().findFirst()
                .map(node -> new SequenceGraph(node.getLeftMostNeighbour(), node.getRightMostNeighbour()))
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
