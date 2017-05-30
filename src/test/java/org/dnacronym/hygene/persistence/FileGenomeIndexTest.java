package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FileGenomeIndex} class.
 */
final class FileGenomeIndexTest extends FileDatabaseBaseTest {
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
        fileGenomeIndex.addGenomeIndexPoint(0, 1, 1);

        assertThat(fileGenomeIndex.getClosestNodeToBase(0, 2)).isEqualTo(1);
    }

    @Test
    void testStoreAndRetrieveMultipleOptions() throws SQLException {
        fileGenomeIndex.addGenomeIndexPoint(0, 1, 1);
        fileGenomeIndex.addGenomeIndexPoint(0, 5, 2);

        assertThat(fileGenomeIndex.getClosestNodeToBase(0, 2)).isEqualTo(1);
    }
}
