package org.dnacronym.hygene.graph.edge;


import org.checkerframework.checker.nullness.qual.Nullable;
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
    public @Nullable Set<String> getGenomes() {
        return originalEdge.getGenomes();
    }

    @Override
    public int getImportance() {
        final Set<String> originalGenomes = originalEdge.getGenomes();
        if (originalGenomes != null && !originalGenomes.isEmpty()) {
            return originalGenomes.size();
        }

        return 1;
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
