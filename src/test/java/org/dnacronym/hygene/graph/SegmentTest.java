package org.dnacronym.hygene.graph;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

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

    @Test
    void testEquals() {
        EqualsVerifier.forClass(Segment.class)
                .withRedefinedSuperclass()
                .withPrefabValues(Node.class,
                        new Segment(59, 56, 15, new HashSet<>(), new HashSet<>()),
                        new Segment(21, 48, 88, new HashSet<>(), new HashSet<>()))
                .suppress(Warning.NONFINAL_FIELDS) // X and Y position need to be set dynamically during layout
                .verify();
    }
}
