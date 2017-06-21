package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link DynamicGenomeIndex}.
 */
class DynamicGenomeIndexTest {
    private static final String TEST_GFA_FILE_NAME = "C:\\Users\\gandr\\Downloads\\GFA Files\\chr19.hg38.w115.gfa";

    private DynamicGenomeIndex dynamicGenomeIndex;
    private GfaFile gfaFile;

    @BeforeEach
    void setUp() throws IOException, SQLException, GfaParseException {
        gfaFile = new GfaFile(TEST_GFA_FILE_NAME);
        gfaFile.parse(mock(ProgressUpdater.class));
    }

    @Test
    void testStuff() {
        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, "chr19.fa");
        System.out.println("BUILDING INDEX");
        dynamicGenomeIndex.buildIndex();
    }
}
