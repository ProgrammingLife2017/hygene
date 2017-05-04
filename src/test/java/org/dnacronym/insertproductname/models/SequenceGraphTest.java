package org.dnacronym.insertproductname.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class SequenceGraphTest {
    private SequenceNode node1;
    private SequenceNode node2;
    private SequenceGraph sequenceGraph;


    @BeforeEach
    void setUp() {
        node1 = new SequenceNode("1", "ATAT");
        node2 = new SequenceNode("2", "CTCT");
        node1.linkToRightNeighbour(node2);

        final List<SequenceNode> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);
        sequenceGraph = new SequenceGraph(nodes);
    }


    @Test
    void testGetSourceNode() {
        assertThat(sequenceGraph.getSourceNode().getRightNeighbours().get(0)).isEqualTo(node1);
    }

    @Test
    void testGetSinkNode() {
        assertThat(sequenceGraph.getSinkNode().getLeftNeighbours().get(0)).isEqualTo(node2);
    }

    @Test
    void testGetNodes() {
        assertThat(sequenceGraph.getNodes()).contains(node1);
    }

    @Test
    void testSize() {
        assertThat(sequenceGraph.size()).isEqualTo(4);
    }
}
