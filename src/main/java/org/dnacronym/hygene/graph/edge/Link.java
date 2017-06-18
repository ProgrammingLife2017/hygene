package org.dnacronym.hygene.graph.edge;


import org.dnacronym.hygene.graph.node.Node;

/**
 * Class representing a single, non-dummy edge.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Link extends Edge {
    private final int byteOffset;


    /**
     * Constructs a new {@link Link} instance.
     *
     * @param from       the source of the edge
     * @param to         the destination of the edge
     * @param byteOffset the byte offset of the corresponding link in the GFA file this edge was defined in
     */
    public Link(final Node from, final Node to, final int byteOffset) {
        super(from, to);
        this.byteOffset = byteOffset;
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
