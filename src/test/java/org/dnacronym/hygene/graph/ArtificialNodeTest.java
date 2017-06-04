package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link ArtificialNode} class.
 */
class ArtificialNodeTest extends AbstractNodeTest {
    static final AbstractNode ORIGINAL_SOURCE = mock(AbstractNode.class);
    static final AbstractNode ORIGINAL_DESTINATION = mock(AbstractNode.class);

    private ArtificialNode artificialNode;


    @BeforeEach
    void setUp() {
        artificialNode = new ArtificialNode(SEQUENCE_LENGTH, INCOMING_EDGES, OUTGOING_EDGES, ORIGINAL_SOURCE,
                ORIGINAL_DESTINATION);
        setAbstractNode(artificialNode);
    }


    @Test
    void testGetOriginalSource() {
        assertThat(artificialNode.getOriginalSource()).isEqualTo(ORIGINAL_SOURCE);
    }

    @Test
    void testGetOriginalDestination() {
        assertThat(artificialNode.getOriginalDestination()).isEqualTo(ORIGINAL_DESTINATION);
    }
}
