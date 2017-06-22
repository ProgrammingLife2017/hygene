package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.MetadataParseException;
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
    void testInsertion() throws MetadataParseException, IOException, IOException {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "g1.fasta");
        dynamicGenomeIndex.buildIndex();

        assertThat(dynamicGenomeIndex.getNodeByBase(3)).isEqualTo(1);
        assertThat(dynamicGenomeIndex.getNodeByBase(4)).isEqualTo(2);
        assertThat(dynamicGenomeIndex.getNodeByBase(6)).isEqualTo(2);
        assertThat(dynamicGenomeIndex.getNodeByBase(8)).isEqualTo(3);
        assertThat(dynamicGenomeIndex.getBaseOffsetWithinNode(8)).isEqualTo(1);
    }

    @Test
    void testSecondInsertion() throws MetadataParseException, IOException {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "g2.fasta");
        dynamicGenomeIndex.buildIndex();

        assertThat(dynamicGenomeIndex.getNodeByBase(3)).isEqualTo(1);
        assertThat(dynamicGenomeIndex.getNodeByBase(4)).isEqualTo(3);
        assertThat(dynamicGenomeIndex.getNodeByBase(7)).isEqualTo(4);
        assertThat(dynamicGenomeIndex.getNodeByBase(8)).isEqualTo(4);
        assertThat(dynamicGenomeIndex.getNodeByBase(10)).isEqualTo(5);
    }

    @Test
    void testNestedInsertion() throws MetadataParseException, IOException {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "g3.fasta");
        dynamicGenomeIndex.buildIndex();

        assertThat(dynamicGenomeIndex.getNodeByBase(3)).isEqualTo(1);
        assertThat(dynamicGenomeIndex.getNodeByBase(4)).isEqualTo(6);
        assertThat(dynamicGenomeIndex.getNodeByBase(6)).isEqualTo(6);
    }
}
