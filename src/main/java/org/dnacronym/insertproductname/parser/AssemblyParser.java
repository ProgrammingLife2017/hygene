package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;

import java.util.HashMap;
import java.util.Map;


/**
 * Parses an {@code Assembly} to a {@code SequenceGraph}.
 */
public final class AssemblyParser {
    /**
     * Translates a {@code Assembly} into a {@code GraphSequence}.
     *
     * @param assembly an {@code Assembly}
     * @return a {@code GraphSequence}
     */
    public SequenceGraph parse(final Assembly assembly) {
        final Map<String, SequenceNode> nodes = new HashMap<>();

        for (final Segment segment : assembly.getSegments()) {
            getNode(nodes, assembly.getSegment(segment.getName()));
        }

        for (final Link link : assembly.getLinks()) {
            final SequenceNode fromNode = getNode(nodes, assembly.getSegment(link.getFrom()));
            final SequenceNode toNode = getNode(nodes, assembly.getSegment(link.getTo()));

            fromNode.addRightNeighbour(toNode);
            toNode.addLeftNeighbour(fromNode);
        }

        final SequenceNode someNode = nodes.values().stream().findFirst().orElse(null);
        if (someNode != null) {
            return new SequenceGraph(someNode.getLeftMostNeighbour(), someNode.getRightMostNeighbour());
        } else {
            return new SequenceGraph(null, null);
        }
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
