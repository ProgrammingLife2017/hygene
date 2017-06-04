package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing a generic node.
 */
public abstract class AbstractNode {
    private final int sequenceLength;
    private final int xPosition;
    private final int yPosition;
    private final Set<AbstractEdge> incomingEdges;
    private final Set<AbstractEdge> outgoingEdges;


    public AbstractNode(final int sequenceLength, final int xPosition, final int yPosition,
                        final Set<AbstractEdge> incomingEdges, final Set<AbstractEdge> outgoingEdges) {
        this.sequenceLength = sequenceLength;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.incomingEdges = incomingEdges;
        this.outgoingEdges = outgoingEdges;
    }


    /**
     * Returns the sequence length.
     *
     * @return the sequence length
     */
    public int getSequenceLength() {
        return sequenceLength;
    }

    /**
     * Returns the X position.
     *
     * @return the X position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Returns the Y position.
     *
     * @return the X position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Returns the incoming edges.
     *
     * @return the incoming edges
     */
    public Set<AbstractEdge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    public Set<AbstractEdge> getOutgoingEdges() {
        return outgoingEdges;
    }
}
