package org.dnacronym.insertproductname.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceNode}s.
 */
class SequenceNodeTest {
    private static final String SEQUENCE_STRING = "ACTG";

    private SequenceNode sequenceNode;


    @BeforeEach
    void setUp() {
        sequenceNode = new SequenceNode(SEQUENCE_STRING);
    }


    @Test
    void getSequence() {
        assertThat(sequenceNode.getSequence()).isEqualTo(SEQUENCE_STRING);
    }

    @Test
    void getReadIdentifiers() {
        assertThat(sequenceNode.getReadIdentifiers()).isEmpty();
    }

    @Test
    void addReadIdentifier() {
        final String newIdentifier = "123.fasta";
        sequenceNode.addReadIdentifier(newIdentifier);

        assertThat(sequenceNode.getReadIdentifiers()).hasSize(1);
        assertThat(sequenceNode.getReadIdentifiers().get(0)).isEqualTo(newIdentifier);
    }

    @Test
    void getPreviousNodes() {
        assertThat(sequenceNode.getPreviousNodes()).isEmpty();
    }

    @Test
    void addPreviousNode() {
        final SequenceNode newNode = new SequenceNode("ATAT");
        sequenceNode.addPreviousNode(newNode);

        assertThat(sequenceNode.getPreviousNodes()).hasSize(1);
        assertThat(sequenceNode.getPreviousNodes().get(0)).isEqualTo(newNode);
    }

    @Test
    void getNextNodes() {
        assertThat(sequenceNode.getNextNodes()).isEmpty();
    }

    @Test
    void addNextNode() {
        final SequenceNode newNode = new SequenceNode("ATAT");
        sequenceNode.addNextNode(newNode);

        assertThat(sequenceNode.getNextNodes()).hasSize(1);
        assertThat(sequenceNode.getNextNodes().get(0)).isEqualTo(newNode);
    }
}
