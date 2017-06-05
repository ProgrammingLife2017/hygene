package org.dnacronym.hygene.graph;


import java.util.Objects;

/**
 * Class representing a single, non-dummy edge.
 */
public final class Link extends Edge {
    private final int lineNumber;


    /**
     * Constructs a new {@link Link} instance.
     *
     * @param from       the source of the edge
     * @param to         the destination of the edge
     * @param lineNumber the number of the corresponding link in the GFA file this edge was defined in
     */
    public Link(final Node from, final Node to, final int lineNumber) {
        super(from, to);
        this.lineNumber = lineNumber;
    }


    /**
     * Returns the line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Link link = (Link) o;
        return lineNumber == link.lineNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lineNumber);
    }
}
