package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link GenericEdge} class.
 */
abstract class GenericEdgeTest {
    private GenericEdge genericEdge;
    private GenericNode from;
    private GenericNode to;


    @BeforeEach
    void setUp() {
        from = mock(GenericNode.class);
        to = mock(GenericNode.class);
    }


    @Test
    final void testGetFrom() {
        assertThat(genericEdge.getFrom()).isEqualTo(from);
    }

    @Test
    final void testGetTo() {
        assertThat(genericEdge.getTo()).isEqualTo(to);
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    final GenericNode getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    final GenericNode getTo() {
        return to;
    }

    /**
     * Sets the {@link GenericEdge} instance to be tested.
     *
     * @param genericEdge the {@link GenericEdge} instance
     */
    final void setGenericEdge(final GenericEdge genericEdge) {
        this.genericEdge = genericEdge;
    }
}
