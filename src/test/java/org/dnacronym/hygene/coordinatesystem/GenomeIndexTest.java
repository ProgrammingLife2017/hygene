package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for {@link GenomeIndex}.
 */
class GenomeIndexTest {
    private static final String TEST_GFA_FILE_NAME = "src/test/resources/gfa/dynamic-index.gfa";

    private GenomeIndex genomeIndex;
    private GfaFile gfaFile;


    @BeforeEach
    void setUp() throws IOException, SQLException, GfaParseException {
        gfaFile = new GfaFile(TEST_GFA_FILE_NAME);
        gfaFile.parse(mock(ProgressUpdater.class));
    }


    @Test
    void testInsertion() throws IOException {
        genomeIndex = new GenomeIndex(gfaFile, "g1.fasta");
        genomeIndex.buildIndex(mock(ProgressUpdater.class));

        assertThat(genomeIndex.getNodeByBase(3)).isEqualTo(1);
        assertThat(genomeIndex.getNodeByBase(4)).isEqualTo(2);
        assertThat(genomeIndex.getNodeByBase(6)).isEqualTo(2);
        assertThat(genomeIndex.getNodeByBase(8)).isEqualTo(3);
        assertThat(genomeIndex.getBaseOffsetWithinNode(8)).isEqualTo(1);
    }

    @Test
    void testSecondInsertion() throws IOException {
        genomeIndex = new GenomeIndex(gfaFile, "g2.fasta");
        genomeIndex.buildIndex(mock(ProgressUpdater.class));

        assertThat(genomeIndex.getNodeByBase(3)).isEqualTo(1);
        assertThat(genomeIndex.getNodeByBase(4)).isEqualTo(3);
        assertThat(genomeIndex.getNodeByBase(7)).isEqualTo(4);
        assertThat(genomeIndex.getNodeByBase(8)).isEqualTo(4);
        assertThat(genomeIndex.getNodeByBase(10)).isEqualTo(5);
    }

    @Test
    void testNestedInsertion() throws IOException {
        genomeIndex = new GenomeIndex(gfaFile, "g3.fasta");
        genomeIndex.buildIndex(mock(ProgressUpdater.class));

        assertThat(genomeIndex.getNodeByBase(3)).isEqualTo(1);
        assertThat(genomeIndex.getNodeByBase(4)).isEqualTo(6);
        assertThat(genomeIndex.getNodeByBase(6)).isEqualTo(6);
    }

    @Test
    void testGetNodeByBaseNoResults() {
        genomeIndex = new GenomeIndex(gfaFile, "g3.fasta");

        assertThat(genomeIndex.getNodeByBase(2)).isEqualTo(-1);
    }

    @Test
    void testGetBaseOffsetWithinNodeNoResults() throws IOException {
        genomeIndex = new GenomeIndex(gfaFile, "g3.fasta");

        assertThat(genomeIndex.getBaseOffsetWithinNode(2)).isEqualTo(-1);
    }
}
