package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link DummyNode} class.
 */
final class DummyNodeTest extends GenericNodeTest {
    private DummyNode dummyNode;
    private GenericNode diversionSource;
    private GenericNode diversionDestination;


    @BeforeEach
    void setUp() {
        super.setUp();

        diversionSource = mock(GenericNode.class);
        diversionDestination = mock(GenericNode.class);
        dummyNode = new DummyNode(getIncomingEdges(), getOutgoingEdges(), diversionSource,
                diversionDestination);
        setGenericNode(dummyNode);
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
