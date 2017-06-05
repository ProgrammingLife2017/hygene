package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Link} class.
 */
final class LinkTest extends EdgeTest {
    private static final int LINE_NUMBER = 29;

    private Link link;


    @BeforeEach
    void setUp() {
        super.setUp();

        link = new Link(getFrom(), getTo(), LINE_NUMBER);
        setEdge(link);
    }


    @Test
    void testGetLineNumber() {
        assertThat(link.getLineNumber()).isEqualTo(LINE_NUMBER);
    }
}
