package org.dnacronym.hygene.graph;

import java.util.Objects;
import java.util.UUID;


/**
 * Class representing a generic edge.
 */
public class Edge {
    private final UUID uuid;
    private final Node from;
    private final Node to;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    protected Edge(final Node from, final Node to) {
        this.uuid = UUID.randomUUID();
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

    @Override
    public final boolean equals(final Object o) {
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
