package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Segment} class.
 */
final class SegmentTest extends NodeTest {
    private static final int ID = 39;
    private static final int LINE_NUMBER = 56;
    static final int SEQUENCE_LENGTH = 45;

    private Segment segment;


    @BeforeEach
    void setUp() {
        super.setUp();

        segment = new Segment(ID, LINE_NUMBER, SEQUENCE_LENGTH, getIncomingEdges(), getOutgoingEdges());
        setNode(segment);
    }


    @Test
    void testGetId() {
        assertThat(segment.getId()).isEqualTo(ID);
    }

    @Test
    void testGetLineNumber() {
        assertThat(segment.getLineNumber()).isEqualTo(LINE_NUMBER);
    }

    @Test
    void testGetSequenceLength() {
        assertThat(segment.getSequenceLength()).isEqualTo(SEQUENCE_LENGTH);
    }
}
