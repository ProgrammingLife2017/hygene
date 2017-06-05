package org.dnacronym.hygene.graph;

import java.util.Set;


/**
 * Class representing an artificial node, to be used for graph layout.
 * <p>
 * This is a 'dummy node', used to create space in complex graph scenarios. As a consequence, it is not intended to be
 * drawn and serves as a connector of {@link DummyEdge}s. One or more of these {@link DummyNode}s forms a 'diversion',
 * which is a path starting from one non-artificial node, followed by one or more {@link DummyNode}s (connected by
 * {@link DummyEdge}s). The diversion reaches its end when it connects to the first non-artificial node again.
 * <p>
 * An example of such a diversion is illustrated below:
 * <p>
 * <pre>
 * A ----- B
 * *       *
 * X * Y * Z
 * </pre>
 * <i>
 * A and B are {@link Node}s and the connection between them is an {@link Edge}. The diversion in this scenario
 * consists of the 'stars' (representing {@link DummyEdge}s) and the {@link DummyNode}s they connect (X, Y, and Z).
 * </i>
 */
public final class DummyNode extends GenericNode {
    private final GenericNode diversionSource;
    private final GenericNode diversionDestination;


    /**
     * Constructs a new {@link DummyNode} instance.
     *
     * @param incomingEdges        the incoming edges
     * @param outgoingEdges        the outgoing edges
     * @param diversionSource      the original source node this artificial node is (indirectly) connected to
     * @param diversionDestination the original destination node this artificial node is (indirectly) connected to
     */
    public DummyNode(final Set<GenericEdge> incomingEdges, final Set<GenericEdge> outgoingEdges,
                     final GenericNode diversionSource, final GenericNode diversionDestination) {
        super(incomingEdges, outgoingEdges);
        this.diversionSource = diversionSource;
        this.diversionDestination = diversionDestination;
    }


    /**
     * Returns the diversion source node.
     *
     * @return the diversion source node
     */
    public GenericNode getDiversionSource() {
        return diversionSource;
    }

    /**
     * Returns the diversion destination node.
     *
     * @return the diversion destination node
     */
    public GenericNode getDiversionDestination() {
        return diversionDestination;
    }
}
