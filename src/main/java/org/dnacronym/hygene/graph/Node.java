package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing a single, non-artificial segment node.
 */
public final class Node extends GenericNode {
    private final int id;
    private final int lineNumber;
    private final int sequenceLength;


    /**
     * Constructs a new {@link Node} instance.
     *
     * @param id             the internal ID of the node
     * @param lineNumber     the number of the corresponding segment in the GFA file this node was defined in
     * @param sequenceLength the length of the node
     * @param incomingEdges  the incoming edges
     * @param outgoingEdges  the outgoing edges
     */
    public Node(final int id, final int lineNumber, final int sequenceLength, final Set<GenericEdge> incomingEdges,
                final Set<GenericEdge> outgoingEdges) {
        super(incomingEdges, outgoingEdges);
        this.id = id;
        this.lineNumber = lineNumber;
        this.sequenceLength = sequenceLength;
    }


    /**
     * Returns the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the sequence length.
     *
     * @return the sequence length
     */
    public int getSequenceLength() {
        return sequenceLength;
    }
}
