package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.UUID;


/**
 * Class representing a generic edge.
 */
public class Edge {
    private final UUID uuid;
    private final NewNode from;
    private final NewNode to;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    protected Edge(final NewNode from, final NewNode to) {
        this.uuid = UUID.randomUUID();
        this.from = from;
        this.to = to;
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    public final NewNode getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    public final NewNode getTo() {
        return to;
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Edge edge = (Edge) o;
        return Objects.equals(uuid, edge.uuid);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(uuid);
    }
}
