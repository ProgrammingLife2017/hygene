package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;

import java.util.HashMap;
import java.util.Map;


/**
 * .
 */
public final class AssemblyParser {
    public SequenceGraph parse(final Assembly assembly) {
        final Map<String, SequenceNode> nodes = new HashMap<>();

        for (final Link link : assembly.getLinks()) {
            final SequenceNode fromNode = getNode(nodes, link.getFrom());
            final SequenceNode toNode = getNode(nodes, link.getTo());

            fromNode.addRightNeighbour(toNode);
            toNode.addLeftNeighbour(fromNode);
        }

        SequenceNode first = getNode(nodes, assembly.getLinks().get(0).getFrom());
        while (first.getLeftNeighbours().size() != 0) {
            first = first.getLeftNeighbours().get(0);
        }

        return new SequenceGraph(first);
    }


    private SequenceNode getNode(final Map<String, SequenceNode> nodes, final Segment segment) {
        final SequenceNode node = nodes.get(segment.getName());

        if (node != null) {
            return node;
        }

        final SequenceNode node2 = new SequenceNode(segment.getSequence());
        nodes.put(segment.getName(), node2);
        return node2;
    }
}
