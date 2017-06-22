package org.dnacronym.hygene.graph.edge;

import org.dnacronym.hygene.graph.node.GfaNode;


/**
 * A simple edge between two {@link GfaNode}s, but without metadata.
 */
public final class SimpleEdge extends Edge {
    private final GfaNode from;
    private final GfaNode to;


    /**
     * Constructs a new {@link SimpleEdge}.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    public SimpleEdge(final GfaNode from, final GfaNode to) {
        super(from, to);

        this.from = from;
        this.to = to;
    }


    @Override
    public GfaNode getFromSegment() {
        return from;
    }

    @Override
    public GfaNode getToSegment() {
        return to;
    }
}
