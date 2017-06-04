package org.dnacronym.hygene.graph;


/**
 * Class representing an artificial edge, to be used for graph layout.
 * <p>
 * Such an {@link ArtificialEdge} connects at least one {@link ArtificialNode} to at most one non-artificial
 * {@link GenericNode}.
 */
public final class ArtificialEdge extends GenericEdge {
    private final GenericEdge originalEdge;


    /**
     * Constructs a new {@link ArtificialEdge} instance.
     *
     * @param from         the source of the edge
     * @param to           the destination of the edge
     * @param originalEdge the original edge this artificial edge is replacing (cannot be an {@link ArtificialEdge})
     */
    public ArtificialEdge(final GenericNode from, final GenericNode to, final GenericEdge originalEdge) {
        super(from, to);

        assert !(originalEdge instanceof ArtificialEdge);
        this.originalEdge = originalEdge;
    }


    /**
     * Returns the original edge.
     *
     * @return the original edge
     */
    public GenericEdge getOriginalEdge() {
        return originalEdge;
    }
}
