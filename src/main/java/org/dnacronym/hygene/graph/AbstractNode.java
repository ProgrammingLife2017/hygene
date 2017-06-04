package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing a generic node.
 */
public abstract class AbstractNode {
    private final int sequenceLength;
    private final Set<AbstractEdge> incomingEdges;
    private final Set<AbstractEdge> outgoingEdges;
    private int xPosition;
    private int yPosition;


    /**
     * Constructs a new {@link AbstractNode} instance.
     *
     * @param sequenceLength the length of the node
     * @param incomingEdges  the incoming edges
     * @param outgoingEdges  the outgoing edges
     */
    public AbstractNode(final int sequenceLength, final Set<AbstractEdge> incomingEdges,
                        final Set<AbstractEdge> outgoingEdges) {
        this.sequenceLength = sequenceLength;
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
     * Sets the X position.
     *
     * @param xPosition the X position
     */
    public void setXPosition(final int xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * Returns the Y position.
     *
     * @return the Y position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Sets the Y position.
     *
     * @param yPosition the Y position
     */
    public void setYPosition(final int yPosition) {
        this.yPosition = yPosition;
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
