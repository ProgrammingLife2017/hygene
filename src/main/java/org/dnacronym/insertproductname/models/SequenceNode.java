package org.dnacronym.insertproductname.models;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@code SequenceNode} represents a node in a DNA Sequence Alignment Graph.
 */
public final class SequenceNode {
    private final String sequence;
    private final List<String> readIdentifiers;
    private final List<SequenceNode> previousNodes;
    private final List<SequenceNode> nextNodes;


    /**
     * Constructs a new {@code SequenceNode}, with empty lists of read-IDs and adjacent nodes.
     *
     * @param sequence the sequence of bases this node contains
     */
    public SequenceNode(final String sequence) {
        this.sequence = sequence;
        this.readIdentifiers = new ArrayList<>();
        this.previousNodes = new ArrayList<>();
        this.nextNodes = new ArrayList<>();
    }


    /**
     * Returns the sequence.
     *
     * @return the sequence.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Returns the read identifiers.
     *
     * @return the read identifiers.
     */
    public List<String> getReadIdentifiers() {
        return readIdentifiers;
    }

    /**
     * Adds the given {@code identifier} string to the list of read identifiers of this node.
     *
     * @param identifier the identifier to be added
     */
    public void addReadIdentifier(final String identifier) {
        readIdentifiers.add(identifier);
    }

    /**
     * Returns the previous nodes.
     *
     * @return the previous nodes.
     */
    public List<SequenceNode> getPreviousNodes() {
        return previousNodes;
    }

    /**
     * Adds the given {@code sequenceNode} to the list of previous nodes.
     *
     * @param sequenceNode the node to be added as a previous node
     */
    public void addPreviousNode(final SequenceNode sequenceNode) {
        previousNodes.add(sequenceNode);
    }

    /**
     * Returns the next nodes.
     *
     * @return the next nodes.
     */
    public List<SequenceNode> getNextNodes() {
        return nextNodes;
    }

    /**
     * Adds the given {@code sequenceNode} to the list of next nodes.
     *
     * @param sequenceNode the node to be added as a next node
     */
    public void addNextNode(final SequenceNode sequenceNode) {
        nextNodes.add(sequenceNode);
    }
}
