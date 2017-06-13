package org.dnacronym.hygene.events;

import org.dnacronym.hygene.graph.Subgraph;


public final class LayoutDoneEvent {
    private final Subgraph subgraph;


    public LayoutDoneEvent(final Subgraph subgraph) {
        this.subgraph = subgraph;
    }


    public Subgraph getSubgraph() {
        return subgraph;
    }
}
