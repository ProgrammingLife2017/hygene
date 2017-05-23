package org.dnacronym.hygene.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link SequenceNode}.
 */
final class SequenceNodeTest {
    private static final String SEQUENCE_ID = "1";
    private static final String SEQUENCE_STRING = "ACTG";

    private SequenceNode sequenceNode;


    @BeforeEach
    void setUp() {
        sequenceNode = new SequenceNode(SEQUENCE_ID, SEQUENCE_STRING);
    }


    @Test
    void testGetId() {
        assertThat(sequenceNode.getId()).isEqualTo(SEQUENCE_ID);
    }

    @Test
    void testGetSequence() {
        assertThat(sequenceNode.getSequence()).isEqualTo(SEQUENCE_STRING);
    }

    @Test
    void testGetLength() {
        assertThat(sequenceNode.getLength()).isEqualTo(4);
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
    void testAddLeftNeighbour() {
        final SequenceNode newNode = new SequenceNode("1", "ATAT");
        sequenceNode.addLeftNeighbour(newNode);

        assertThat(sequenceNode.getLeftNeighbours()).hasSize(1);
        assertThat(sequenceNode.getLeftNeighbours()).contains(newNode);
    }

    @Test
    void testLinkToLeftNeighbour() {
        final SequenceNode newNode = new SequenceNode("1", "CTG");
        sequenceNode.linkToLeftNeighbour(newNode);

        assertThat(sequenceNode.getLeftNeighbours()).hasSize(1);
        assertThat(sequenceNode.getLeftNeighbours()).contains(newNode);

        // Check whether the new node correctly links back to the main node
        assertThat(newNode.getRightNeighbours()).hasSize(1);
        assertThat(newNode.getRightNeighbours()).contains(sequenceNode);
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
    void testAddRightNeighbour() {
        final SequenceNode newNode = new SequenceNode("1", "ATAT");
        sequenceNode.addRightNeighbour(newNode);

        assertThat(sequenceNode.getRightNeighbours()).hasSize(1);
        assertThat(sequenceNode.getRightNeighbours()).contains(newNode);
    }

    @Test
    void testLinkToRightNeighbour() {
        final SequenceNode newNode = new SequenceNode("1", "ATC");
        sequenceNode.linkToRightNeighbour(newNode);

        assertThat(sequenceNode.getRightNeighbours()).hasSize(1);
        assertThat(sequenceNode.getRightNeighbours()).contains(newNode);

        // Check whether the new node correctly links back to the main node
        assertThat(newNode.getLeftNeighbours()).hasSize(1);
        assertThat(newNode.getLeftNeighbours()).contains(sequenceNode);
    }
}
