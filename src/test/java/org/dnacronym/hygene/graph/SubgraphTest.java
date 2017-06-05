package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link Subgraph} class.
 */
final class SubgraphTest {
    private Subgraph subgraph;
    private Set<Node> nodes;


    @BeforeEach
    void setUp() {
        nodes = new HashSet<>();
        subgraph = new Subgraph(nodes);
    }


    @Test
    void testGetNodes() {
        assertThat(subgraph.getNodes()).isEqualTo(nodes);
    }

    @Test
    void testAddNode() {
        final Node node = mock(Node.class);
        subgraph.addNode(node);
        assertThat(subgraph.getNodes()).containsExactly(node);
    }

    @Test
    void testRemoveNode() {
        final Node node = mock(Node.class);
        subgraph.addNode(node);
        assertThat(subgraph.getNodes()).containsExactly(node);

        subgraph.removeNode(node);
        assertThat(subgraph.getNodes()).isEmpty();
    }

    @Test
    void testGetSourceNeighbours() {
        assertThat(subgraph.getSourceNeighbours()).isEmpty();
    }

    @Test
    void testGetSinkNeighbours() {
        assertThat(subgraph.getSinkNeighbours()).isEmpty();
    }

    @Test
    void testSourceAndSinkNeighboursSingleNode() {
        final Segment segment = new Segment(87, 14, 1, Collections.emptySet(), Collections.emptySet());
        subgraph.addNode(segment);

        assertThat(subgraph.getSourceNeighbours()).containsExactly(segment);
        assertThat(subgraph.getSinkNeighbours()).containsExactly(segment);
    }

    @Test
    void testSourceAndSinkNeighboursTwoNodes() {
        final Segment segment1 = new Segment(52, 16, 28, new HashSet<>(), new HashSet<>());
        final Segment segment2 = new Segment(83, 1, 50, new HashSet<>(), new HashSet<>());
        final Link link = new Link(segment1, segment2, 58);
        segment1.getOutgoingEdges().add(link);
        segment2.getIncomingEdges().add(link);
        subgraph.addNode(segment1);
        subgraph.addNode(segment2);

        assertThat(subgraph.getSourceNeighbours()).containsExactly(segment1);
        assertThat(subgraph.getSinkNeighbours()).containsExactly(segment2);
    }
}
