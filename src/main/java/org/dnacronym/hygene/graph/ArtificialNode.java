package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing an artificial node, to be used for graph layout.
 */
public final class ArtificialNode extends GenericNode {
    private final GenericNode originalSource;
    private final GenericNode originalDestination;


    /**
     * Constructs a new {@link ArtificialNode} instance.
     *
     * @param incomingEdges       the incoming edges
     * @param outgoingEdges       the outgoing edges
     * @param originalSource      the original source node this artificial node is (indirectly) connected to
     * @param originalDestination the original destination node this artificial node is (indirectly) connected to
     */
    public ArtificialNode(final Set<GenericEdge> incomingEdges, final Set<GenericEdge> outgoingEdges,
                          final GenericNode originalSource, final GenericNode originalDestination) {
        super(incomingEdges, outgoingEdges);
        this.originalSource = originalSource;
        this.originalDestination = originalDestination;
    }


    /**
     * Returns the original source node.
     *
     * @return the original source node
     */
    public GenericNode getOriginalSource() {
        return originalSource;
    }

    /**
     * Returns the original destination edge.
     *
     * @return the original destination edge
     */
    public GenericNode getOriginalDestination() {
        return originalDestination;
    }
}
