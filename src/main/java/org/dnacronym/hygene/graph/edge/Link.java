package org.dnacronym.hygene.graph.edge;

import org.dnacronym.hygene.graph.node.GfaNode;


/**
 * Class representing a single, non-dummy edge.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Link extends Edge {
    private final GfaNode from;
    private final GfaNode to;
    private final int byteOffset;


    /**
     * Constructs a new {@link Link} instance.
     *
     * @param from       the source of the edge
     * @param to         the destination of the edge
     * @param byteOffset the byte offset of the corresponding link in the GFA file this edge was defined in
     */
    public Link(final GfaNode from, final GfaNode to, final int byteOffset) {
        super(from, to);

        this.from = from;
        this.to = to;
        this.byteOffset = byteOffset;
    }


    @Override
    public GfaNode getFromSegment() {
        return from;
    }

    @Override
    public GfaNode getToSegment() {
        return to;
    }

    /**
     * Returns the byte offset.
     *
     * @return the byte offset
     */
    public int getByteOffset() {
        return byteOffset;
    }
}
