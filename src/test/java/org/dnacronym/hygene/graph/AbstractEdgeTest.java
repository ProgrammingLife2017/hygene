package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link AbstractEdge} class.
 */
abstract class AbstractEdgeTest {
    static final AbstractNode FROM = mock(AbstractNode.class);
    static final AbstractNode TO = mock(AbstractNode.class);

    private AbstractEdge abstractEdge;


    @Test
    final void testGetFrom() {
        assertThat(abstractEdge.getFrom()).isEqualTo(FROM);
    }

    @Test
    final void testGetTo() {
        assertThat(abstractEdge.getTo()).isEqualTo(TO);
    }


    /**
     * Sets the {@link AbstractEdge} instance to be tested.
     *
     * @param abstractEdge the {@link AbstractEdge} instance
     */
    final void setAbstractEdge(final AbstractEdge abstractEdge) {
        this.abstractEdge = abstractEdge;
    }
}
