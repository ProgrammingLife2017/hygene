package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;


/**
 * Class representing a dummy edge, to be used for graph layout.
 */
public final class DummyEdge extends Edge {
    private final Edge originalEdge;


    /**
     * Constructs a new {@link DummyEdge} instance.
     *
     * @param from         the source of the edge
     * @param to           the destination of the edge
     * @param originalEdge the original edge this dummy edge is replacing (cannot be a {@link DummyEdge})
     */
    public DummyEdge(final Node from, final Node to, final Edge originalEdge) {
        super(from, to);

        assert !(originalEdge instanceof DummyEdge);
        this.originalEdge = originalEdge;
    }


    /**
     * Returns the original edge.
     *
     * @return the original edge
     */
    public Edge getOriginalEdge() {
        return originalEdge;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final DummyEdge dummyEdge = (DummyEdge) o;
        return Objects.equals(originalEdge, dummyEdge.originalEdge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), originalEdge);
    }
}
