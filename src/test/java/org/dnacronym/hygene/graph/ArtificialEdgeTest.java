package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test suite for the {@link ArtificialEdge} class.
 */
final class ArtificialEdgeTest extends GenericEdgeTest {
    private static final GenericEdge ORIGINAL_EDGE = mock(GenericEdge.class);

    private ArtificialEdge artificialEdge;


    @BeforeEach
    void setUp() {
        artificialEdge = new ArtificialEdge(FROM, TO, ORIGINAL_EDGE);
        setGenericEdge(artificialEdge);
    }


    @Test
    void testGetOriginalEdge() {
        assertThat(artificialEdge.getOriginalEdge()).isEqualTo(ORIGINAL_EDGE);
    }
}
