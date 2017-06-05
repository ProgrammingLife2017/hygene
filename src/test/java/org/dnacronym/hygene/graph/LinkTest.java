package org.dnacronym.hygene.graph;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link Link} class.
 */
final class LinkTest extends EdgeTest {
    private static final int LINE_NUMBER = 29;

    private Link link;


    @BeforeEach
    void setUp() {
        super.setUp();

        link = new Link(getFrom(), getTo(), LINE_NUMBER);
        setEdge(link);
    }


    @Test
    void testGetLineNumber() {
        assertThat(link.getLineNumber()).isEqualTo(LINE_NUMBER);
    }

    @Test
    void testEquals() {
        EqualsVerifier.forClass(Link.class)
                .withRedefinedSuperclass()
                .withPrefabValues(Node.class,
                        new DummyNode(new HashSet<>(), new HashSet<>(), mock(Node.class), mock(Node.class)),
                        new DummyNode(new HashSet<>(), new HashSet<>(), mock(Node.class), mock(Node.class)))
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }
}
