package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link DummyNode} class.
 */
final class DummyNodeTest extends NodeTest {
    private DummyNode dummyNode;
    private Node diversionSource;
    private Node diversionDestination;


    @BeforeEach
    void setUp() {
        super.setUp();

        diversionSource = mock(Node.class);
        diversionDestination = mock(Node.class);
        dummyNode = new DummyNode(getIncomingEdges(), getOutgoingEdges(), diversionSource,
                diversionDestination);
        setNode(dummyNode);
    }


    @Test
    void testGetOriginalSource() {
        assertThat(dummyNode.getDiversionSource()).isEqualTo(diversionSource);
    }

    @Test
    void testGetOriginalDestination() {
        assertThat(dummyNode.getDiversionDestination()).isEqualTo(diversionDestination);
    }
}
