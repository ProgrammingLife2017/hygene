package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Node} class.
 */
class NodeTest extends AbstractNodeTest {
    static final int ID = 1;
    static final int LINE_NUMBER = 2;

    private Node node;


    @BeforeEach
    void setUp() {
        node = new Node(ID, LINE_NUMBER, SEQUENCE_LENGTH, INCOMING_EDGES, OUTGOING_EDGES);
        setAbstractNode(node);
    }

    @Test
    void testGetId() {
        assertThat(node.getId()).isEqualTo(ID);
    }

    @Test
    void testGetLineNumber() {
        assertThat(node.getLineNumber()).isEqualTo(LINE_NUMBER);
    }

}
