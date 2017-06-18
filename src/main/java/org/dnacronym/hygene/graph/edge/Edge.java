package org.dnacronym.hygene.graph.edge;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.node.NewNode;

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
    private @Nullable Set<String> genomes;


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
     * Returns the genome this {@link Edge} is in, or {@code null} if metadata has not been parsed yet.
     *
     * @return the genome this {@link Edge} is in, or {@code null} if metadata has not been parsed yet
     */
    public final @Nullable Set<String> getGenomes() {
        return genomes;
    }

    /**
     * Sets the genome set for this {@link Edge}.
     *
     * @param genomes the genomes
     */
    public final void setGenomes(@Nullable final Set<String> genomes) {
        this.genomes = genomes;
    }

    /**
     * Returns the edges importance.
     * <p>
     * The edge importance is defined as the number of genome paths that run through this {@link Edge}.
     *
     * @return the {@link Edge} importance
     */
    public final int getImportance() {
        if (genomes != null && !genomes.isEmpty()) {
            return genomes.size();
        }
        return 1;
    }

    /**
     * Returns {@code true} iff. this {@link Edge} is in the given genome.
     *
     * @param genome the name of a genome
     * @return {@code true} iff. this {@link Edge} is in the given genome
     */
    public final boolean inGenome(final String genome) {
        return genomes != null && genomes.contains(genome);
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
