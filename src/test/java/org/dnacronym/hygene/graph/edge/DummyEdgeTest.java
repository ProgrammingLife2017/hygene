package org.dnacronym.hygene.graph.edge;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.node.Node;
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
                .withPrefabValues(Node.class,
                        new DummyNode(mock(Node.class), mock(Node.class)),
                        new DummyNode(mock(Node.class), mock(Node.class)))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
