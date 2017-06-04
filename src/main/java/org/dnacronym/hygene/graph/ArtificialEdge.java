package org.dnacronym.hygene.graph;


/**
 * Class representing an artificial edge, to be used graph layout.
 */
public final class ArtificialEdge extends AbstractEdge {
    private final AbstractEdge originalEdge;


    /**
     * Constructs a new {@link ArtificialEdge} instance.
     *
     * @param from         the source of the edge
     * @param to           the destination of the edge
     * @param originalEdge the original edge this artificial edge is replacing
     */
    public ArtificialEdge(final AbstractNode from, final AbstractNode to, final AbstractEdge originalEdge) {
        super(from, to);
        this.originalEdge = originalEdge;
    }


    /**
     * Returns the original edge.
     *
     * @return the original edge
     */
    public AbstractEdge getOriginalEdge() {
        return originalEdge;
    }
}
