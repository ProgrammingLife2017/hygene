package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing an artificial node, to be used graph layout.
 */
public final class ArtificialNode extends AbstractNode {
    private final AbstractNode originalSource;
    private final AbstractNode originalDestination;


    /**
     * Constructs a new {@link ArtificialNode} instance.
     *
     * @param sequenceLength      the length of the sequence
     * @param incomingEdges       the incoming edges
     * @param outgoingEdges       the outgoing edges
     * @param originalSource      the original source node this artificial node is (indirectly) connected to
     * @param originalDestination the original destination node this artificial node is (indirectly) connected to
     */
    public ArtificialNode(final int sequenceLength, final Set<AbstractEdge> incomingEdges,
                          final Set<AbstractEdge> outgoingEdges, final AbstractNode originalSource,
                          final AbstractNode originalDestination) {
        super(sequenceLength, incomingEdges, outgoingEdges);
        this.originalSource = originalSource;
        this.originalDestination = originalDestination;
    }


    /**
     * Returns the original source node.
     *
     * @return the original source node
     */
    public AbstractNode getOriginalSource() {
        return originalSource;
    }

    /**
     * Returns the original destination edge.
     *
     * @return the original destination edge
     */
    public AbstractNode getOriginalDestination() {
        return originalDestination;
    }
}
