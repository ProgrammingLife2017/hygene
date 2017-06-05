package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link DummyEdge} class.
 */
final class DummyEdgeTest extends EdgeTest {
    private DummyEdge dummyEdge;
    private Edge originalEdge;


    @BeforeEach
    void setUp() {
        super.setUp();

        originalEdge = mock(Edge.class);
        dummyEdge = new DummyEdge(getFrom(), getTo(), originalEdge);
        setEdge(dummyEdge);
    }


    @Test
    void testGetOriginalEdge() {
        assertThat(dummyEdge.getOriginalEdge()).isEqualTo(originalEdge);
    }
}
