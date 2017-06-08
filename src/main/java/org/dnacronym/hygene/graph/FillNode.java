package org.dnacronym.hygene.graph;


/**
 * Class representing a fill node, to be used for graph layout.
 * <p>
 * Similar to {@link DummyNode}, but used to fill empty vertical space. {@link FillNode}s have no edges, and no
 * diversion sources or destinations.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class FillNode extends NewNode {
    /**
     * Constructs a new {@link FillNode} instance.
     */
    public FillNode() {
       super();
    }


    @Override
    public int getLength() {
        return 0;
    }
}
