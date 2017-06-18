package org.dnacronym.hygene.graph.node;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
        diversionSource = mock(Node.class);
        diversionDestination = mock(Node.class);
        dummyNode = new DummyNode(diversionSource, diversionDestination);
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

    @Test
    void testEquals() {
        EqualsVerifier.forClass(DummyNode.class)
                .withRedefinedSuperclass()
                .withPrefabValues(Node.class,
                        new DummyNode(mock(Node.class), mock(Node.class)),
                        new DummyNode(mock(Node.class), mock(Node.class)))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
