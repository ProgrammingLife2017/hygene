package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link Edge} class.
 */
abstract class EdgeTest {
    private Edge edge;
    private NewNode from;
    private NewNode to;


    @BeforeEach
    void setUp() {
        from = mock(NewNode.class);
        to = mock(NewNode.class);
    }


    @Test
    final void testGetFrom() {
        assertThat(edge.getFrom()).isEqualTo(from);
    }

    @Test
    final void testGetTo() {
        assertThat(edge.getTo()).isEqualTo(to);
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    final NewNode getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    final NewNode getTo() {
        return to;
    }

    /**
     * Sets the {@link Edge} instance to be tested.
     *
     * @param edge the {@link Edge} instance
     */
    final void setEdge(final Edge edge) {
        this.edge = edge;
    }
}
