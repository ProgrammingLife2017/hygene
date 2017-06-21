package org.dnacronym.hygene.graph.edge;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Node;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * Class representing a generic edge.
 */
public abstract class Edge {
    private final UUID uuid;
    private final Node from;
    private final Node to;
    private @Nullable Set<String> genomes;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    public Edge(final Node from, final Node to) {
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
    public final Node getFrom() {
        return from;
    }

    /**
     * Returns the {@link GfaNode} from which this edge actually departs.
     *
     * @return the {@link GfaNode} from which this edge actually departs
     */
    public abstract GfaNode getFromSegment();

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    public final Node getTo() {
        return to;
    }

    /**
     * Returns the {@link GfaNode} to which this edge actually goes.
     *
     * @return the {@link GfaNode} to which this edge actually goes
     */
    public abstract GfaNode getToSegment();

    /**
     * Returns the genome this {@link Edge} is in, or {@code null} if metadata has not been parsed yet.
     *
     * @return the genome this {@link Edge} is in, or {@code null} if metadata has not been parsed yet
     */
    @SuppressWarnings("DesignForExtension")
    public @Nullable Set<String> getGenomes() {
        return genomes;
    }

    /**
     * Sets the genome set for this {@link Edge}.
     *
     * @param genomes the genomes
     */
    @SuppressWarnings("DesignForExtension")
    public void setGenomes(@Nullable final Set<String> genomes) {
        this.genomes = genomes;
    }

    /**
     * Returns the edges importance.
     * <p>
     * The edge importance is defined as the number of genome paths that run through this {@link Edge}.
     *
     * @return the {@link Edge} importance
     */
    @SuppressWarnings("DesignForExtension")
    public int getImportance() {
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
    @SuppressWarnings("DesignForExtension")
    public boolean inGenome(final String genome) {
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
