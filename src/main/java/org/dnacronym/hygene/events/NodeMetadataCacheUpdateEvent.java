package org.dnacronym.hygene.events;

import org.dnacronym.hygene.graph.Subgraph;
import org.dnacronym.hygene.graph.node.Segment;


/**
 * Indicates that the metadata for all {@link Segment}s has been retrieved.
 */
public final class NodeMetadataCacheUpdateEvent {
    private final Subgraph subgraph;


    /**
     * Constructs a new {@link NodeMetadataCacheUpdateEvent}.
     *
     * @param subgraph the {@link Subgraph} for which all {@link Segment}s have metadata
     */
    public NodeMetadataCacheUpdateEvent(final Subgraph subgraph) {
        this.subgraph = subgraph;
    }


    /**
     * Returns the {@link Subgraph} that has metadata.
     *
     * @return the {@link Subgraph} that has metadata
     */
    public Subgraph getSubgraph() {
        return subgraph;
    }
}
