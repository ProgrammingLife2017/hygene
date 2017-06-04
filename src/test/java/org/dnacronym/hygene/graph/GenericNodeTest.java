package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link GenericNode} class.
 */
abstract class GenericNodeTest {
    static final int X_POSITION = 31;
    static final int Y_POSITION = 64;

    private GenericNode genericNode;
    private Set<GenericEdge> incomingEdges;
    private Set<GenericEdge> outgoingEdges;


    @BeforeEach
    void setUp() {
        incomingEdges = new HashSet<>();
        outgoingEdges = new HashSet<>();
    }


    @Test
    final void testGetXPosition() {
        genericNode.setXPosition(X_POSITION);

        assertThat(genericNode.getXPosition()).isEqualTo(X_POSITION);
    }

    @Test
    final void testGetYPosition() {
        genericNode.setYPosition(Y_POSITION);

        assertThat(genericNode.getYPosition()).isEqualTo(Y_POSITION);
    }

    @Test
    void testGetIncomingEdges() {
        assertThat(genericNode.getIncomingEdges()).isEqualTo(getIncomingEdges());
    }

    @Test
    final void testGetOutgoingEdges() {
        assertThat(genericNode.getOutgoingEdges()).isEqualTo(outgoingEdges);
    }


    /**
     * Returns the incoming edges.
     *
     * @return the incoming edges
     */
    final Set<GenericEdge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    final Set<GenericEdge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Sets the {@link GenericNode} instance to be tested.
     *
     * @param genericNode the {@link GenericNode} instance
     */
    final void setGenericNode(final GenericNode genericNode) {
        this.genericNode = genericNode;
    }
}
