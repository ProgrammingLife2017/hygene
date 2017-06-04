package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Node} class.
 */
final class NodeTest extends GenericNodeTest {
    private static final int ID = 39;
    private static final int LINE_NUMBER = 56;
    static final int SEQUENCE_LENGTH = 45;

    private Node node;


    @BeforeEach
    void setUp() {
        super.setUp();

        node = new Node(ID, LINE_NUMBER, SEQUENCE_LENGTH, getIncomingEdges(), getOutgoingEdges());
        setGenericNode(node);
    }


    @Test
    void testGetId() {
        assertThat(node.getId()).isEqualTo(ID);
    }

    @Test
    void testGetLineNumber() {
        assertThat(node.getLineNumber()).isEqualTo(LINE_NUMBER);
    }

    @Test
    void testGetSequenceLength() {
        assertThat(node.getSequenceLength()).isEqualTo(SEQUENCE_LENGTH);
    }
}
