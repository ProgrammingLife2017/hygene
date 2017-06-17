package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.node.NewNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link NewNode} class.
 */
abstract class NodeTest {
    static final int X_POSITION = 31;
    static final int Y_POSITION = 64;

    private NewNode node;


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
     * Sets the {@link NewNode} instance to be tested.
     *
     * @param node the {@link NewNode} instance
     */
    final void setNode(final NewNode node) {
        this.node = node;
    }
}
