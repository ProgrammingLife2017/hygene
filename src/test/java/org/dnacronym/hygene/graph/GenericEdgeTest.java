package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link GenericEdge} class.
 */
abstract class GenericEdgeTest {
    static final GenericNode FROM = mock(GenericNode.class);
    static final GenericNode TO = mock(GenericNode.class);

    private GenericEdge genericEdge;


    @Test
    final void testGetFrom() {
        assertThat(genericEdge.getFrom()).isEqualTo(FROM);
    }

    @Test
    final void testGetTo() {
        assertThat(genericEdge.getTo()).isEqualTo(TO);
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
