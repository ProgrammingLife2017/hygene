package org.dnacronym.insertproductname.models;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@code SequenceNode} represents a node in a DNA Sequence Alignment Graph.
 *
 * Each node contains a base-sequence, held in the {@code sequence} field. Each node also is associated with one or more
 * reads (runs of DNA analysis). Finally, each node is linked with its adjacent nodes, either backward or forward (left
 * or right).
 */
public final class SequenceNode {
    private final String id;
    private final String sequence;
    private final List<String> readIdentifiers;
    private final List<SequenceNode> leftNeighbours;
    private final List<SequenceNode> rightNeighbours;


    /**
     * Constructs a new {@code SequenceNode}, with empty lists of read-IDs and adjacent nodes.
     *
     * @param id the ID of this sequence segment
     * @param sequence the sequence of bases this node contains
     */
    public SequenceNode(final String id, final String sequence) {
        this.id = id;
        this.sequence = sequence;
        this.readIdentifiers = new ArrayList<>();
        this.leftNeighbours = new ArrayList<>();
        this.rightNeighbours = new ArrayList<>();
    }


    /**
     * Returns the ID.
     *
     * @return the ID.
     */
    public final String getId() {
        return id;
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
     * Returns the left neighbours.
     *
     * @return the left neighbours.
     */
    public List<SequenceNode> getLeftNeighbours() {
        return leftNeighbours;
    }

    /**
     * Returns true iff. the node has 1 or more left neighbours.
     *
     * @return true iff. the node has 1 or more left neighbours.
     */
    public boolean hasLeftNeighbours() {
        return !leftNeighbours.isEmpty();
    }

    /**
     * Adds the given {@code sequenceNode} to the list of left neighbours.
     *
     * @param sequenceNode the node to be added as a left neighbour
     */
    public void addLeftNeighbour(final SequenceNode sequenceNode) {
        leftNeighbours.add(sequenceNode);
    }

    /**
     * Returns the right neighbours.
     *
     * @return the right neighbours.
     */
    public List<SequenceNode> getRightNeighbours() {
        return rightNeighbours;
    }

    /**
     * Returns true iff. the node has 1 or more right neighbours.
     *
     * @return true iff. the node has 1 or more right neighbours.
     */
    public boolean hasRightNeighbours() {
        return !rightNeighbours.isEmpty();
    }

    /**
     * Adds the given {@code sequenceNode} to the list of right neighbours.
     *
     * @param sequenceNode the node to be added as a right neighbour
     */
    public void addRightNeighbour(final SequenceNode sequenceNode) {
        rightNeighbours.add(sequenceNode);
    }
}
