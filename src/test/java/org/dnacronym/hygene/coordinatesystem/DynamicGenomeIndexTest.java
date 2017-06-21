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
 * Unit tests for {@link DynamicGenomeIndex}.
 */
class DynamicGenomeIndexTest {
    private static final String TEST_GFA_FILE_NAME = "src/test/resources/gfa/dynamic-index.gfa";

    private DynamicGenomeIndex dynamicGenomeIndex;
    private GfaFile gfaFile;


    @BeforeEach
    void setUp() throws IOException, SQLException, GfaParseException {
        gfaFile = new GfaFile(TEST_GFA_FILE_NAME);
        gfaFile.parse(mock(ProgressUpdater.class));
    }


    @Test
    void testInsertion() {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "g1.fasta");
        dynamicGenomeIndex.buildIndex();

        assertThat(dynamicGenomeIndex.getNodeByBase(2)).isEqualTo(1);
        assertThat(dynamicGenomeIndex.getNodeByBase(3)).isEqualTo(2);
        assertThat(dynamicGenomeIndex.getNodeByBase(5)).isEqualTo(2);
        assertThat(dynamicGenomeIndex.getNodeByBase(7)).isEqualTo(3);
    }

    @Test
    void testSecondInsertion() {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "g2.fasta");
        dynamicGenomeIndex.buildIndex();

        assertThat(dynamicGenomeIndex.getNodeByBase(2)).isEqualTo(1);
        assertThat(dynamicGenomeIndex.getNodeByBase(3)).isEqualTo(3);
        assertThat(dynamicGenomeIndex.getNodeByBase(6)).isEqualTo(4);
        assertThat(dynamicGenomeIndex.getNodeByBase(7)).isEqualTo(4);
        assertThat(dynamicGenomeIndex.getNodeByBase(9)).isEqualTo(5);
    }

    @Test
    void testNestedInsertion() {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "g3.fasta");
        dynamicGenomeIndex.buildIndex();

        assertThat(dynamicGenomeIndex.getNodeByBase(2)).isEqualTo(1);
        assertThat(dynamicGenomeIndex.getNodeByBase(3)).isEqualTo(6);
        assertThat(dynamicGenomeIndex.getNodeByBase(5)).isEqualTo(6);
    }
}
