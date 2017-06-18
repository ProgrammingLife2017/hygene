package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.edge.Edge;

import java.util.Set;


/**
 * Represents a node that can be laid out by a layout algorithm.
 */
public interface LayoutableNode {
    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    Set<Edge> getOutgoingEdges();

    /**
     * Returns the incoming edges.
     *
     * @return the incoming edges
     */
    Set<Edge> getIncomingEdges();

    /**
     * Returns the X position.
     *
     * @return the X position
     */
    long getXPosition();

    /**
     * Returns the length of the node when visualized.
     *
     * @return the length of the node when visualized
     */
    int getLength();

    /**
     * Sets the Y position.
     *
     * @param yPosition the Y position
     */
    void setYPosition(final int yPosition);
}
