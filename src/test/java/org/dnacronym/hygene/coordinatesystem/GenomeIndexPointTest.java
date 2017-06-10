package org.dnacronym.hygene.coordinatesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link GenomeIndexPoint} class.
 */
class GenomeIndexPointTest {
    private static final int GENOME_ID = 24;
    private static final int BASE = 67;
    private static final int NODE_ID = 59;

    private GenomeIndexPoint genomeIndexPoint;


    @BeforeEach
    void setUp() {
        genomeIndexPoint = new GenomeIndexPoint(GENOME_ID, BASE, NODE_ID);
    }


    @Test
    void testGetGenomeId() {
        assertThat(genomeIndexPoint.getGenomeId()).isEqualTo(GENOME_ID);
    }

    @Test
    void testGetBase() {
        assertThat(genomeIndexPoint.getBase()).isEqualTo(BASE);
    }

    @Test
    void testGetNodeId() {
        assertThat(genomeIndexPoint.getNodeId()).isEqualTo(NODE_ID);
    }

}
