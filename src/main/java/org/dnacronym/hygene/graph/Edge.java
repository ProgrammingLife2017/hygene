package org.dnacronym.hygene.graph;


import java.util.Objects;

/**
 * Class representing a generic edge.
 */
public class Edge {
    private final Node from;
    private final Node to;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    protected Edge(final Node from, final Node to) {
        this.from = from;
        this.to = to;
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    public final Node getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    public final Node getTo() {
        return to;
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Edge edge = (Edge) o;
        return Objects.equals(from, edge.from)
                && Objects.equals(to, edge.to);
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
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
