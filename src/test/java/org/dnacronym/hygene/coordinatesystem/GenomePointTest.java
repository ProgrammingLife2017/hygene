package org.dnacronym.hygene.coordinatesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link GenomePoint} class.
 */
class GenomePointTest {
    private static final int GENOME_ID = 24;
    private static final int BASE = 67;
    private static final int NODE_ID = 59;
    private static final int BASE_OFFSET = 13;

    private GenomePoint genomePoint;


    @BeforeEach
    void setUp() {
        genomePoint = new GenomePoint(GENOME_ID, BASE, NODE_ID, BASE_OFFSET);
    }


    @Test
    void testGetGenomeId() {
        assertThat(genomePoint.getGenomeId()).isEqualTo(GENOME_ID);
    }

    @Test
    void testGetBase() {
        assertThat(genomePoint.getBase()).isEqualTo(BASE);
    }

    @Test
    void testGetNodeId() {
        assertThat(genomePoint.getNodeId()).isEqualTo(NODE_ID);
    }

    @Test
    void testGetBaseOffsetInNode() {
        assertThat(genomePoint.getBaseOffsetInNode()).isEqualTo(BASE_OFFSET);
    }
}
