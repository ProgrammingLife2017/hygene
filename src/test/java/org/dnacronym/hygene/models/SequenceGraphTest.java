package org.dnacronym.hygene.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link SequenceGraph}s.
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
    void testEmptyGraph() {
        sequenceGraph = new SequenceGraph(new ArrayList<>());

        assertThat(sequenceGraph.getSourceNode().hasRightNeighbours()).isTrue();
        assertThat(sequenceGraph.getSinkNode().hasLeftNeighbours()).isTrue();
    }

    @Test
    void testGetSourceNode() {
        assertThat(sequenceGraph.getSourceNode().getId()).isEqualTo(SequenceGraph.SOURCE_NODE_ID);
    }

    @Test
    void testSourceNodeLink() {
        assertThat(sequenceGraph.getSourceNode().getRightNeighbours().get(0)).isEqualTo(node1);
    }

    @Test
    void testGetSinkNode() {
        assertThat(sequenceGraph.getSinkNode().getId()).isEqualTo(SequenceGraph.SINK_NODE_ID);
    }

    @Test
    void testSinkNodeLink() {
        assertThat(sequenceGraph.getSinkNode().getLeftNeighbours().get(0)).isEqualTo(node2);
    }

    @Test
    void testSize() {
        assertThat(sequenceGraph.size()).isEqualTo(4);
    }
}
