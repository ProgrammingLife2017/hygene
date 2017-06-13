package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * Class representing a generic edge.
 */
public class Edge {
    private final UUID uuid;
    private final NewNode from;
    private final NewNode to;
    private Set<String> genomes;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    public Edge(final NewNode from, final NewNode to) {
        this.uuid = UUID.randomUUID();
        this.from = from;
        this.to = to;
        this.genomes = null;
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

    /**
     * Get the genome set for this {@link Edge}.
     *
     * @return the genomes
     */
    @Nullable
    public Set<String> getGenomes() {
        return genomes;
    }

    /**
     * Sets the genome set for this {@link Edge}.
     *
     * @param genomes the genomes
     */
    public void setGenomes(final Set<String> genomes) {
        this.genomes = genomes;
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

    @Override
    public final String toString() {
        return "Edge{" + "uuid=" + uuid
                + ",\n    from=" + from
                + ", to=" + to + "}\n";
    }
}
