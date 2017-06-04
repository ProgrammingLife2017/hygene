package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link ArtificialEdge} class.
 */
final class ArtificialEdgeTest extends GenericEdgeTest {
    private ArtificialEdge artificialEdge;
    private GenericEdge originalEdge;


    @BeforeEach
    void setUp() {
        super.setUp();

        originalEdge = mock(GenericEdge.class);
        artificialEdge = new ArtificialEdge(getFrom(), getTo(), originalEdge);
        setGenericEdge(artificialEdge);
    }


    @Test
    void testGetOriginalEdge() {
        assertThat(artificialEdge.getOriginalEdge()).isEqualTo(originalEdge);
    }
}
