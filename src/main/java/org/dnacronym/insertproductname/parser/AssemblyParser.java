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

        return new SequenceGraph(getFirstNode(nodes), getLastNode(nodes));
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

    /**
     * Calls {@code getFirstNode} on some element in the given {@code Map}.
     *
     * @param nodes a {@code Map} of {@code SequenceNode}s
     * @return the indirect left neighbour of a {@code SequenceNode} that does not have a left neighbour
     */
    private SequenceNode getFirstNode(final Map<String, SequenceNode> nodes) {
        return nodes.values().stream()
                .findFirst()
                .map(this::getFirstNode)
                .orElse(null);
    }

    /**
     * Takes a {@code SequenceNode}'s left neighbour and takes that neighbour's left neighbour etc. until it finds a
     * {@code SequenceNode} without a left neighbour, and returns this {@code SequenceNode}.
     *
     * @param node a {@code Sequence Node}
     * @return the indirect left neighbour of a {@code SequenceNode} that does not have a left neighbour
     */
    private SequenceNode getFirstNode(final SequenceNode node) {
        SequenceNode first = node;

        while (!first.getLeftNeighbours().isEmpty()) {
            first = first.getLeftNeighbours().get(0);
        }

        return first;
    }

    /**
     * Calls {@code getLastNode} on some element in the given {@code Map}.
     *
     * @param nodes a {@code Map} of {@code SequenceNode}s
     * @return the indirect right neighbour of a {@code SequenceNode} that does not have a right neighbour
     */
    private SequenceNode getLastNode(final Map<String, SequenceNode> nodes) {
        return nodes.values().stream()
                .findFirst()
                .map(this::getLastNode)
                .orElse(null);
    }

    /**
     * Takes a {@code SequenceNode}'s right neighbour and takes that neighbour's right neighbour etc. until it finds a
     * {@code SequenceNode} without a right neighbour, and returns this {@code SequenceNode}.
     *
     * @param node a {@code Sequence Node}
     * @return the indirect right neighbour of a {@code SequenceNode} that does not have a right neighbour
     */
    private SequenceNode getLastNode(final SequenceNode node) {
        SequenceNode first = node;

        while (!first.getRightNeighbours().isEmpty()) {
            first = first.getRightNeighbours().get(0);
        }

        return first;
    }
}
