package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link ArtificialNode} class.
 */
final class ArtificialNodeTest extends GenericNodeTest {
    private ArtificialNode artificialNode;
    private GenericNode originalSource;
    private GenericNode originalDestination;


    @BeforeEach
    void setUp() {
        super.setUp();

        originalSource = mock(GenericNode.class);
        originalDestination = mock(GenericNode.class);
        artificialNode = new ArtificialNode(getIncomingEdges(), getOutgoingEdges(), originalSource,
                originalDestination);
        setGenericNode(artificialNode);
    }


    @Test
    void testGetOriginalSource() {
        assertThat(artificialNode.getOriginalSource()).isEqualTo(originalSource);
    }

    @Test
    void testGetOriginalDestination() {
        assertThat(artificialNode.getOriginalDestination()).isEqualTo(originalDestination);
    }
}
