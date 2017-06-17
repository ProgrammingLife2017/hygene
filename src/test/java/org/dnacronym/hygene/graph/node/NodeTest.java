package org.dnacronym.hygene.graph.node;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Node} class.
 */
abstract class NodeTest {
    static final int X_POSITION = 31;
    static final int Y_POSITION = 64;

    private Node node;


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
        assertThat(node.getIncomingEdges()).isEmpty();
    }

    @Test
    final void testGetOutgoingEdges() {
        assertThat(node.getOutgoingEdges()).isEmpty();
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
