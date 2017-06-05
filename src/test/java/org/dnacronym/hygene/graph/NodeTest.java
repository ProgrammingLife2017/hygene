package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Node} class.
 */
abstract class NodeTest {
    static final int X_POSITION = 31;
    static final int Y_POSITION = 64;

    private Node node;
    private Set<Edge> incomingEdges;
    private Set<Edge> outgoingEdges;


    @BeforeEach
    void setUp() {
        incomingEdges = new HashSet<>();
        outgoingEdges = new HashSet<>();
    }


    @Test
    final void testGetXPosition() {
        node.setXPosition(X_POSITION);

        assertThat(node.getXPosition()).isEqualTo(X_POSITION);
    }

    @Test
    final void testGetYPosition() {
        node.setYPosition(Y_POSITION);

        assertThat(node.getYPosition()).isEqualTo(Y_POSITION);
    }

    @Test
    final void testGetIncomingEdges() {
        assertThat(node.getIncomingEdges()).isEqualTo(getIncomingEdges());
    }

    @Test
    final void testGetOutgoingEdges() {
        assertThat(node.getOutgoingEdges()).isEqualTo(outgoingEdges);
    }


    /**
     * Returns the incoming edges.
     *
     * @return the incoming edges
     */
    final Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    final Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Sets the {@link Node} instance to be tested.
     *
     * @param node the {@link Node} instance
     */
    final void setNode(final Node node) {
        this.node = node;
    }
}
