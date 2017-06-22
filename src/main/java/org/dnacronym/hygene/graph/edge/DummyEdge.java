package org.dnacronym.hygene.graph.edge;


import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Node;

import java.util.Set;


/**
 * Class representing a dummy edge, to be used for graph layout.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
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
        setGenomes(originalEdge.getGenomes());
    }


    @Override
    public GfaNode getFromSegment() {
        return originalEdge.getFromSegment();
    }

    @Override
    public GfaNode getToSegment() {
        return originalEdge.getToSegment();
    }

    @Override
    public @Nullable Set<String> getGenomes() {
        return originalEdge.getGenomes();
    }

    @Override
    public void setGenomes(@Nullable final Set<String> genomes) {
        originalEdge.setGenomes(genomes);
    }

    @Override
    public int getImportance() {
        return originalEdge.getImportance();
    }

    @Override
    public boolean inGenome(final String genome) {
        return originalEdge.inGenome(genome);
    }

    /**
     * Returns the original edge.
     *
     * @return the original edge
     */
    public Edge getOriginalEdge() {
        return originalEdge;
    }
}
