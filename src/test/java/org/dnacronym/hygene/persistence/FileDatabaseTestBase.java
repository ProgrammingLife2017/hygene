package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Base test suite for database tests.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod") // Must be abstract because it contains no tests
abstract class FileDatabaseTestBase {
    static final String GFA_FILE_NAME = "src/test/resources/gfa/simple.gfa";
    static final File GFA_FILE_DB = new File(GFA_FILE_NAME + FileDatabaseDriver.DB_FILE_EXTENSION);


    @BeforeEach
    void setUp() throws IOException, SQLException {
        deleteDatabaseFile();
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
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
