package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test suite for the {@link ArtificialEdge} class.
 */
final class ArtificialEdgeTest extends AbstractEdgeTest {
    private static final AbstractEdge ORIGINAL_EDGE = mock(AbstractEdge.class);

    private ArtificialEdge artificialEdge;


    @BeforeEach
    void setUp() {
        artificialEdge = new ArtificialEdge(FROM, TO, ORIGINAL_EDGE);
        setAbstractEdge(artificialEdge);
    }


    @Test
    void testGetOriginalEdge() {
        assertThat(artificialEdge.getOriginalEdge()).isEqualTo(ORIGINAL_EDGE);
    }
}
