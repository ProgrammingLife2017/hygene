package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link Edge} class.
 */
abstract class EdgeTest {
    private Edge edge;
    private NewNode from;
    private NewNode to;
    private Set<String> genomes;


    @BeforeEach
    void setUp() {
        from = mock(NewNode.class);
        to = mock(NewNode.class);
        genomes = new HashSet<>(Arrays.asList("a", "b", "c"));
    }


    @Test
    final void testGetFrom() {
        assertThat(edge.getFrom()).isEqualTo(from);
    }

    @Test
    final void testGetTo() {
        assertThat(edge.getTo()).isEqualTo(to);
    }

    @Test
    void testGetSetGenomes() {
        edge.setGenomes(genomes);
        assertThat(edge.getGenomes()).isEqualTo(genomes);
    }

    @Test
    void testGetImportance() {
        edge.setGenomes(genomes);
        assertThat(edge.getImportance()).isEqualTo(genomes.size());
    }

    @Test
    void testGetImportantNullCase() {
        edge.setGenomes(null);
        assertThat(edge.getImportance()).isEqualTo(1);
    }

    @Test
    void testGetImportantEmptyCase() {
        edge.setGenomes(new HashSet<>());
        assertThat(edge.getImportance()).isEqualTo(1);
    }

    @Test
    void testGetInGenome() {
        edge.setGenomes(genomes);
        assertThat(edge.inGenome("a")).isTrue();
    }

    @Test
    void testGetInGenomeNullCase() {
        edge.setGenomes(null);
        assertThat(edge.inGenome("a")).isFalse();
    }

    /**
     * Returns the source node.
     *
     * @return the source node
     */
    final NewNode getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    final NewNode getTo() {
        return to;
    }

    /**
     * Sets the {@link Edge} instance to be tested.
     *
     * @param edge the {@link Edge} instance
     */
    final void setEdge(final Edge edge) {
        this.edge = edge;
    }
}
