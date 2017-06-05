package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Set;


/**
 * Class representing a generic node.
 */
public class Node {
    private final Set<Edge> incomingEdges;
    private final Set<Edge> outgoingEdges;
    private int xPosition;
    private int yPosition;


    /**
     * Constructs a new {@link Node} instance.
     * <p>
     * This class should not be instantiated for regular use, please use {@link Segment} instead.
     *
     * @param incomingEdges the incoming edges
     * @param outgoingEdges the outgoing edges
     */
    protected Node(final Set<Edge> incomingEdges, final Set<Edge> outgoingEdges) {
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
    public final Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    public final Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * This method should be overridden in subclasses. Please make use of the {@link #hashCode()} function of this class
     * for verification of the fields of this class.
     *
     * @param o the reference object with which to compare
     * @return {@code true} iff. this object is the same as {@code o}
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension") // to be overridden and used in subclasses
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Node node = (Node) o;
        return xPosition == node.xPosition
                && yPosition == node.yPosition
                && Objects.equals(incomingEdges, node.incomingEdges)
                && Objects.equals(outgoingEdges, node.outgoingEdges);
    }

    /**
     * Returns a hash code value for the object.
     * <p>
     * This method should be overridden in subclasses. It is encouraged to be used for verification of the equality of
     * state of this superclass.
     *
     * @return a hash code value for this object
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension") // to be overridden and used in subclasses
    public int hashCode() {
        return Objects.hash(incomingEdges, outgoingEdges, xPosition, yPosition);
    }
}
