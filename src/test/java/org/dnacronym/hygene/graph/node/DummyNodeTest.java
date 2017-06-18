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
    private NewNode diversionSource;
    private NewNode diversionDestination;


    @BeforeEach
    void setUp() {
        diversionSource = mock(NewNode.class);
        diversionDestination = mock(NewNode.class);
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
                .withPrefabValues(NewNode.class,
                        new DummyNode(mock(NewNode.class), mock(NewNode.class)),
                        new DummyNode(mock(NewNode.class), mock(NewNode.class)))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
