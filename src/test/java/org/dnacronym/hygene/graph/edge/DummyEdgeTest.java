package org.dnacronym.hygene.graph.edge;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.node.NewNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link DummyEdge} class.
 */
final class DummyEdgeTest extends EdgeTest {
    private DummyEdge dummyEdge;
    private Edge originalEdge;


    @BeforeEach
    void setUp() {
        super.setUp();

        originalEdge = mock(Edge.class);
        dummyEdge = new DummyEdge(getFrom(), getTo(), originalEdge);
        setEdge(dummyEdge);
    }


    @Test
    void testGetOriginalEdge() {
        assertThat(dummyEdge.getOriginalEdge()).isEqualTo(originalEdge);
    }

    @Test
    void testEquals() {
        EqualsVerifier.forClass(DummyEdge.class)
                .withRedefinedSuperclass()
                .withPrefabValues(NewNode.class,
                        new DummyNode(mock(NewNode.class), mock(NewNode.class)),
                        new DummyNode(mock(NewNode.class), mock(NewNode.class)))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
