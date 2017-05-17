package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link DatabaseDriver} class.
 */
final class DatabaseDriverTest {
    private static final String GFA_FILE_NAME = "src/test/resources/gfa/simple.gfa";
    private static final File GFA_FILE_DB = new File(GFA_FILE_NAME + DatabaseDriver.DB_FILE_EXTENSION);

    private DatabaseDriver databaseDriver;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        deleteDatabaseFile();
        databaseDriver = new DatabaseDriver(GFA_FILE_NAME);
    }


    /**
     * Runs through the typical process of writing to and reading from the file database.
     * <p>
     * This test is rather smoky in nature, but that is due to the large number of side-effects happening in all of
     * these methods. In other words, it is quite cumbersome to test one without running all of the other.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    @Test
    void testWriteRead() throws SQLException {
        final String testTableName = "test";
        final DatabaseTable databaseTable;

        databaseTable = new DatabaseTable(testTableName);
        databaseTable.addColumn("col1", "string");
        databaseTable.addColumn("col2", "string");
        databaseDriver.setupTable(databaseTable);

        databaseDriver.insertRow(testTableName, Arrays.asList("1", "2"));
        assertThat(databaseDriver.getSingleValue(testTableName, "col1", "1", "col2")).isEqualTo("2");
    }


    @AfterEach
    void tearDown() throws IOException, SQLException {
        databaseDriver.close();
        deleteDatabaseFile();
    }


    /**
     * Deletes the database file belonging to the GFA source file.
     *
     * @throws IOException in case the file did exist and could not be deleted successfully
     */
    private void deleteDatabaseFile() throws IOException {
        if (GFA_FILE_DB.exists() && !GFA_FILE_DB.delete()) {
            throw new IOException("Failed to delete existing database file.");
        }
    }
}
