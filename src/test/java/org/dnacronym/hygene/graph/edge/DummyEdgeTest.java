package org.dnacronym.hygene.graph.edge;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link DummyEdge} class.
 */
final class DummyEdgeTest extends EdgeTest {
    private GfaNode from;
    private GfaNode to;
    private DummyEdge dummyEdge;
    private Edge originalEdge;


    @BeforeEach
    void setUp() {
        super.setUp();

        from = mock(GfaNode.class);
        to = mock(GfaNode.class);
        originalEdge = new SimpleEdge(from, to);
        dummyEdge = new DummyEdge(getFrom(), getTo(), originalEdge);
        setEdge(dummyEdge);
    }


    @Test
    void testGetOriginalEdge() {
        assertThat(dummyEdge.getOriginalEdge()).isEqualTo(originalEdge);
    }

    @Test
    void testGetImportance() {
        Set<String> originalGenomes = new HashSet<>(Arrays.asList("a", "b", "c"));
        originalEdge.setGenomes(originalGenomes);
        assertThat(dummyEdge.getImportance()).isEqualTo(3);
    }

    @Test
    void testInGenome() {
        Set<String> originalGenomes = new HashSet<>(Arrays.asList("a", "b"));
        originalEdge.setGenomes(originalGenomes);
        assertThat(dummyEdge.inGenome("a")).isTrue();
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
