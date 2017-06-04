package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link GenericNode} class.
 */
abstract class GenericNodeTest {
    static final int X_POSITION = 1;
    static final int Y_POSITION = 2;
    static final Set<GenericEdge> INCOMING_EDGES = new HashSet<>();
    static final Set<GenericEdge> OUTGOING_EDGES = new HashSet<>();

    private GenericNode genericNode;


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
        assertThat(genericNode.getIncomingEdges()).isEqualTo(INCOMING_EDGES);
    }

    @Test
    final void testGetOutgoingEdges() {
        assertThat(genericNode.getOutgoingEdges()).isEqualTo(OUTGOING_EDGES);
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
