package org.dnacronym.insertproductname.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class SequenceGraphTest {
    private SequenceNode startNode;
    private SequenceGraph sequenceGraph;


    @BeforeEach
    void setUp() {
        startNode = new SequenceNode("ATAT");
        sequenceGraph = new SequenceGraph(startNode);
    }


    @Test
    void testGetStartNode() {
        assertThat(sequenceGraph.getStartNode()).isEqualTo(startNode);
    }
}
