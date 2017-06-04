package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing a single, non-artificial segment node.
 */
public final class Node extends AbstractNode {
    private final int id;
    private final int lineNumber;


    /**
     * Constructs a new {@link Node} instance.
     *
     * @param id             the internal ID of the node
     * @param lineNumber     the number of the corresponding segment in the GFA file this node was defined in
     * @param sequenceLength the length of the node
     * @param incomingEdges  the incoming edges
     * @param outgoingEdges  the outgoing edges
     */
    public Node(final int id, final int lineNumber, final int sequenceLength, final Set<AbstractEdge> incomingEdges,
                final Set<AbstractEdge> outgoingEdges) {
        super(sequenceLength, incomingEdges, outgoingEdges);
        this.id = id;
        this.lineNumber = lineNumber;
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
}
