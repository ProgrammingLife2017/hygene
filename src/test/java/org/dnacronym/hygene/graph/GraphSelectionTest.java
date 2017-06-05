package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link GraphSelection} class.
 */
class GraphSelectionTest {
    private GraphSelection graphSelection;
    private Set<Node> nodes;
    private Set<Edge> edges;


    @BeforeEach
    void setUp() {
        nodes = new HashSet<>();
        edges = new HashSet<>();
        graphSelection = new GraphSelection(nodes, edges);
    }


    @Test
    void testGetNodes() {
        assertThat(graphSelection.getNodes()).isEqualTo(nodes);
    }

    @Test
    void testGetEdges() {
        assertThat(graphSelection.getEdges()).isEqualTo(edges);
    }

    @Test
    void testEmptyConstructor() {
        final GraphSelection emptySelection = new GraphSelection();

        assertThat(emptySelection.getNodes()).isEmpty();
        assertThat(emptySelection.getEdges()).isEmpty();
    }

    @Test
    void testGetNodesWithAddedNodes() {
        final Node node = new Segment(62, 22, 19, new HashSet<>(), new HashSet<>());
        graphSelection.getNodes().add(node);

        assertThat(graphSelection.getNodes()).containsExactly(node);
    }

    @Test
    void testGetEdgesWithAddedEdges() {
        final Edge edge = new Link(mock(Node.class), mock(Node.class), 74);
        graphSelection.getEdges().add(edge);

        assertThat(graphSelection.getEdges()).containsExactly(edge);
    }
}
