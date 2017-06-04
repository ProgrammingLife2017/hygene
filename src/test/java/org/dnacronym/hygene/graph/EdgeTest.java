package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Edge} class.
 */
final class EdgeTest extends GenericEdgeTest {
    private static final int LINE_NUMBER = 29;

    private Edge edge;


    @BeforeEach
    void setUp() {
        super.setUp();

        edge = new Edge(getFrom(), getTo(), LINE_NUMBER);
        setGenericEdge(edge);
    }


    @Test
    void testGetLineNumber() {
        assertThat(edge.getLineNumber()).isEqualTo(LINE_NUMBER);
    }
}
