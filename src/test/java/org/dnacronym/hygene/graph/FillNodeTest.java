package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FillNode} class.
 */
final class FillNodeTest extends NodeTest {
    private FillNode fillNode;


    @BeforeEach
    void setUp() {
        super.setUp();

        fillNode = new FillNode();
        setNode(fillNode);
    }

    @Test
    void testGetLength() {
        assertThat(fillNode.getLength()).isEqualTo(0);
    }
}
