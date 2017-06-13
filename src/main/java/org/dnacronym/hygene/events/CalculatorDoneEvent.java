package org.dnacronym.hygene.events;

import org.dnacronym.hygene.graph.Subgraph;


public final class CalculatorDoneEvent {
    private final Subgraph subgraph;


    public CalculatorDoneEvent(final Subgraph subgraph) {
        this.subgraph = subgraph;
    }


    public Subgraph getSubgraph() {
        return subgraph;
    }
}
