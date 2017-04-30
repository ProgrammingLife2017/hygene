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
    void testGetSequence() {
        assertThat(sequenceNode.getSequence()).isEqualTo(SEQUENCE_STRING);
    }

    @Test
    void testGetReadIdentifiers() {
        assertThat(sequenceNode.getReadIdentifiers()).isEmpty();
    }

    @Test
    void addReadIdentifier() {
        final String newIdentifier = "123.fasta";
        sequenceNode.addReadIdentifier(newIdentifier);

        assertThat(sequenceNode.getReadIdentifiers()).hasSize(1);
        assertThat(sequenceNode.getReadIdentifiers()).contains(newIdentifier);
    }

    @Test
    void testGetLeftNeighbours() {
        assertThat(sequenceNode.getLeftNeighbours()).isEmpty();
    }

    @Test
    void testHasLeftNeighbours() {
        assertThat(sequenceNode.hasLeftNeighbours()).isFalse();
    }

    @Test
    void testAddPreviousNode() {
        final SequenceNode newNode = new SequenceNode("ATAT");
        sequenceNode.addLeftNeighbour(newNode);

        assertThat(sequenceNode.getLeftNeighbours()).hasSize(1);
        assertThat(sequenceNode.getLeftNeighbours()).contains(newNode);
    }

    @Test
    void testGetNextNodes() {
        assertThat(sequenceNode.getRightNeighbours()).isEmpty();
    }

    @Test
    void testHasRightNeighbours() {
        assertThat(sequenceNode.hasRightNeighbours()).isFalse();
    }

    @Test
    void testAddNextNode() {
        final SequenceNode newNode = new SequenceNode("ATAT");
        sequenceNode.addRightNeighbour(newNode);

        assertThat(sequenceNode.getRightNeighbours()).hasSize(1);
        assertThat(sequenceNode.getRightNeighbours()).contains(newNode);
    }
}
