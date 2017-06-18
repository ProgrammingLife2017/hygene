package org.dnacronym.hygene.event;

import org.dnacronym.hygene.graph.Subgraph;


/**
 * Indicates that the layout has been calculated completely, and is available in the provided {@link Subgraph}.
 */
public final class LayoutDoneEvent {
    private final Subgraph subgraph;


    /**
     * Constructs a new {@link LayoutDoneEvent}.
     *
     * @param subgraph the laid out {@link Subgraph}
     */
    public LayoutDoneEvent(final Subgraph subgraph) {
        this.subgraph = subgraph;
    }


    /**
     * Returns the laid out {@link Subgraph}.
     *
     * @return the laid out {@link Subgraph}
     */
    public Subgraph getSubgraph() {
        return subgraph;
    }
}
