package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Subgraph} class.
 */
class SubgraphTest {
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
    void testGetSourceNeighbours() {
        assertThat(subgraph.getSourceNeighbours()).isEmpty();
    }

    @Test
    void testGetSinkNeighbours() {
        assertThat(subgraph.getSinkNeighbours()).isEmpty();
    }
}
