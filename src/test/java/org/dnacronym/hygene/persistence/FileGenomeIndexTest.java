package org.dnacronym.hygene.persistence;

import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FileGenomeIndex} class.
 */
final class FileGenomeIndexTest extends FileDatabaseTestBase {
    private FileGenomeIndex fileGenomeIndex;
    private FileDatabase fileDatabase;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabase = new FileDatabase(GFA_FILE_NAME);
        fileGenomeIndex = new FileGenomeIndex(fileDatabase);
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();
        super.tearDown();
    }


    @Test
    void testGetTable() {
        assertThat(fileGenomeIndex.getTable().getName()).isEqualTo(FileGenomeIndex.TABLE_NAME);
    }

    @Test
    void testStoreAndRetrieve() throws SQLException {
        fileGenomeIndex.addGenomeIndexPoint(new GenomePoint(0, 1, 1));

        assertThat(fileGenomeIndex.getGenomePoint(0, 2)).isEqualTo(new GenomePoint(0, 2, 1, 1));
    }

    @Test
    void testStoreAndRetrieveMultipleOptions() throws SQLException {
        fileGenomeIndex.addGenomeIndexPoint(new GenomePoint(0, 1, 1));
        fileGenomeIndex.addGenomeIndexPoint(new GenomePoint(0, 5, 2));

        assertThat(fileGenomeIndex.getGenomePoint(0, 4)).isEqualTo(new GenomePoint(0, 4, 1, 3));
    }
}
