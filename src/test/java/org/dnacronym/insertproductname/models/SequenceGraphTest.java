package org.dnacronym.insertproductname.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class SequenceGraphTest {
    private static final SequenceNode START_NODE = new SequenceNode("ATAT");

    private SequenceGraph sequenceGraph;


    @BeforeEach
    void setUp() {
        sequenceGraph = new SequenceGraph(START_NODE);
    }


    @Test
    void getStartNode() {
        assertThat(sequenceGraph.getStartNode()).isEqualTo(START_NODE);
    }
}
