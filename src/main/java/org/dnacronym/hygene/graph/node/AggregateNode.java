package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.metadata.NodeMetadata;

import java.util.Collection;
import java.util.LinkedHashSet;


/**
 * Aggregates multiple nodes into a single node.
 */
public final class AggregateNode extends Node {
    private final Collection<Node> nodes;
    private final int length;


    /**
     * Constructs a new {@link AggregateNode}.
     *
     * @param nodes the nodes to aggregate
     */
    public AggregateNode(final Collection<Node> nodes) {
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException("AggregateNode cannot aggregate an empty collection.");
        }

        this.nodes = new LinkedHashSet<>(nodes);
        this.length = nodes.stream()
                .map(node -> node.getLength())
                .max(Integer::compare)
                .get();
    }


    /**
     * Returns the aggregated nodes.
     *
     * @return the aggregated nodes
     */
    public Collection<Node> getNodes() {
        return nodes;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public NodeMetadata getMetadata() {
        throw new UnsupportedOperationException("AggregateNodes cannot have metadata.");
    }

    @Override
    public void setMetadata(final NodeMetadata metadata) {
        throw new UnsupportedOperationException("AggregateNodes cannot have metadata.");
    }

    @Override
    public boolean hasMetadata() {
        return false;
    }
}
