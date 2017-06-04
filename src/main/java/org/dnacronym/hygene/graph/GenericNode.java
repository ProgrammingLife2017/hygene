package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing a generic node.
 */
public class GenericNode {
    private final Set<GenericEdge> incomingEdges;
    private final Set<GenericEdge> outgoingEdges;
    private int xPosition;
    private int yPosition;


    /**
     * Constructs a new {@link GenericNode} instance.
     *
     * @param incomingEdges  the incoming edges
     * @param outgoingEdges  the outgoing edges
     */
    protected GenericNode(final Set<GenericEdge> incomingEdges, final Set<GenericEdge> outgoingEdges) {
        this.incomingEdges = incomingEdges;
        this.outgoingEdges = outgoingEdges;
    }


    /**
     * Returns the X position.
     *
     * @return the X position
     */
    public final int getXPosition() {
        return xPosition;
    }

    /**
     * Sets the X position.
     *
     * @param xPosition the X position
     */
    public final void setXPosition(final int xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * Returns the Y position.
     *
     * @return the Y position
     */
    public final int getYPosition() {
        return yPosition;
    }

    /**
     * Sets the Y position.
     *
     * @param yPosition the Y position
     */
    public final void setYPosition(final int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Returns the incoming edges.
     *
     * @return the incoming edges
     */
    public final Set<GenericEdge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    public final Set<GenericEdge> getOutgoingEdges() {
        return outgoingEdges;
    }
}
