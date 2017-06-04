package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link AbstractNode} class.
 */
abstract class AbstractNodeTest {
    static final int SEQUENCE_LENGTH = 1;
    static final int X_POSITION = 2;
    static final int Y_POSITION = 3;
    static final Set<AbstractEdge> INCOMING_EDGES = new HashSet<>();
    static final Set<AbstractEdge> OUTGOING_EDGES = new HashSet<>();

    private AbstractNode abstractNode;


    @Test
    void testGetSequenceLength() {
        assertThat(abstractNode.getSequenceLength()).isEqualTo(SEQUENCE_LENGTH);
    }

    @Test
    void testGetXPosition() {
        abstractNode.setXPosition(X_POSITION);

        assertThat(abstractNode.getXPosition()).isEqualTo(X_POSITION);
    }

    @Test
    void testGetYPosition() {
        abstractNode.setYPosition(Y_POSITION);

        assertThat(abstractNode.getYPosition()).isEqualTo(Y_POSITION);
    }

    @Test
    void testGetIncomingEdges() {
        assertThat(abstractNode.getIncomingEdges()).isEqualTo(INCOMING_EDGES);
    }

    @Test
    void testGetOutgoingEdges() {
        assertThat(abstractNode.getOutgoingEdges()).isEqualTo(OUTGOING_EDGES);
    }


    /**
     * Sets the {@link AbstractNode} instance to be tested.
     *
     * @param abstractNode the {@link AbstractNode} instance
     */
    final void setAbstractNode(final AbstractNode abstractNode) {
        this.abstractNode = abstractNode;
    }
}
