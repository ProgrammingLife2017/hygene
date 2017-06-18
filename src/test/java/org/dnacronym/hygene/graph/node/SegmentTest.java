package org.dnacronym.hygene.graph.node;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Segment} class.
 */
final class SegmentTest extends NodeTest {
    private static final int ID = 39;
    private static final int BYTE_OFFSET = 56;
    static final int SEQUENCE_LENGTH = 45;

    private Segment segment;


    @BeforeEach
    void setUp() {
        segment = new Segment(ID, BYTE_OFFSET, SEQUENCE_LENGTH);
        setNode(segment);
    }


    @Test
    void testGetId() {
        assertThat(segment.getId()).isEqualTo(ID);
    }

    @Test
    void testGetByteOffset() {
        assertThat(segment.getByteOffset()).isEqualTo(BYTE_OFFSET);
    }

    @Test
    void testGetSequenceLength() {
        assertThat(segment.getSequenceLength()).isEqualTo(SEQUENCE_LENGTH);
    }

    @Test
    void testEquals() {
        EqualsVerifier.forClass(Segment.class)
                .withRedefinedSuperclass()
                .withPrefabValues(NewNode.class,
                        new Segment(59, 56, 15),
                        new Segment(21, 48, 88))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
