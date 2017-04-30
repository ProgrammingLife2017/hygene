package org.dnacronym.insertproductname.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class SequenceGraphTest {
    private SequenceNode startNode;
    private SequenceNode endNode;
    private SequenceGraph sequenceGraph;


    @BeforeEach
    void setUp() {
        startNode = new SequenceNode("1", "ATAT");
        endNode = new SequenceNode("2", "CTCT");
        sequenceGraph = new SequenceGraph(startNode, endNode);
    }


    @Test
    void testGetStartNode() {
        assertThat(sequenceGraph.getStartNode()).isEqualTo(startNode);
    }


    @Test
    void testGetEndNode() {
        assertThat(sequenceGraph.getEndNode()).isEqualTo(endNode);
    }
}
