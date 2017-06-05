package org.dnacronym.hygene.graph;


/**
 * Class representing an artificial edge, to be used for graph layout.
 * <p>
 * Such an {@link DummyEdge} connects at least one {@link DummyNode} to at most one non-artificial
 * {@link GenericNode}.
 */
public final class DummyEdge extends GenericEdge {
    private final GenericEdge originalEdge;


    /**
     * Constructs a new {@link DummyEdge} instance.
     *
     * @param from         the source of the edge
     * @param to           the destination of the edge
     * @param originalEdge the original edge this artificial edge is replacing (cannot be an {@link DummyEdge})
     */
    public DummyEdge(final GenericNode from, final GenericNode to, final GenericEdge originalEdge) {
        super(from, to);

        assert !(originalEdge instanceof DummyEdge);
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
