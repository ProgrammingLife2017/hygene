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
 * Test suite for the {@link Link} class.
 */
final class LinkTest extends EdgeTest {
    private static final int BYTE_OFFSET = 29;

    private Link link;


    @BeforeEach
    void setUp() {
        super.setUp();

        link = new Link(getFrom(), getTo(), BYTE_OFFSET);
        setEdge(link);
    }


    @Test
    void testGetByteOffset() {
        assertThat(link.getByteOffset()).isEqualTo(BYTE_OFFSET);
    }

    @Test
    void testEquals() {
        EqualsVerifier.forClass(Link.class)
                .withRedefinedSuperclass()
                .withPrefabValues(NewNode.class,
                        new DummyNode(mock(NewNode.class), mock(NewNode.class)),
                        new DummyNode(mock(NewNode.class), mock(NewNode.class)))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
