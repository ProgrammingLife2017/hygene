package org.dnacronym.insertproductname.models;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@code SequenceNode} represents a node in a DNA Sequence Alignment Graph.
 */
public final class SequenceNode {
    private final String sequence;
    private final List<String> readIdentifiers;
    private final List<SequenceNode> nextNodes;


    /**
     * Constructs a new {@code SequenceNode}, with empty lists of read-IDs and adjacent nodes.
     *
     * @param sequence the sequence of bases this node contains
     */
    public SequenceNode(final String sequence) {
        this.sequence = sequence;
        this.readIdentifiers = new ArrayList<>();
        this.nextNodes = new ArrayList<>();
    }

    /**
     * Constructs a new {@code SequenceNode}.
     *
     * @param sequence        the sequence of bases this node contains
     * @param readIdentifiers the list of read identifiers that contain this sequence
     * @param nextNodes       the list of adjacent nodes, linked by an outgoing edge from this node
     */
    public SequenceNode(final String sequence, final List<String> readIdentifiers, final List<SequenceNode> nextNodes) {
        this.sequence = sequence;
        this.readIdentifiers = readIdentifiers;
        this.nextNodes = nextNodes;
    }


    /**
     * Returns the sequence.
     *
     * @return the sequence.
     */
    public final String getSequence() {
        return sequence;
    }

    /**
     * Returns the read identifiers.
     *
     * @return the read identifiers.
     */
    public final List<String> getReadIdentifiers() {
        return readIdentifiers;
    }

    /**
     * Adds the given {@code identifier} string to the list of read identifiers of this node.
     *
     * @param identifier the identifier to be added
     */
    public final void addReadIdentifier(final String identifier) {
        readIdentifiers.add(identifier);
    }

    /**
     * Returns the next nodes.
     *
     * @return the next nodes.
     */
    public final List<SequenceNode> getNextNodes() {
        return nextNodes;
    }

    /**
     * Adds the given {@code sequenceNode} to the list of adjacent nodes.
     *
     * @param sequenceNode the node to be added as an adjacent node
     */
    public final void addNextNode(final SequenceNode sequenceNode) {
        nextNodes.add(sequenceNode);
    }
}
