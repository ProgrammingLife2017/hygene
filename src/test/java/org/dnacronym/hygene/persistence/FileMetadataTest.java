package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FileMetadata} class.
 */
final class FileMetadataTest extends FileDatabaseTestBase {
    private FileMetadata fileMetadata;
    private FileDatabase fileDatabase;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabase = new FileDatabase(GFA_FILE_NAME);
        fileMetadata = new FileMetadata(fileDatabase);
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();
        super.tearDown();
    }


    @Test
    void testGetTable() {
        assertThat(fileMetadata.getTable().getName()).isEqualTo(FileMetadata.TABLE_NAME);
    }

    @Test
    void testStoreMetadata() throws IOException, SQLException {
        fileMetadata.storeMetadata();

        assertThat(fileMetadata.getMetadataValue(FileMetadata.VERSION_KEY_NAME))
                .isEqualTo(String.valueOf(FileDatabase.DB_VERSION));
    }

    @Test
    void testVerifyMetadata() throws IOException, SQLException {
        final String testFileName = "src/test/resources/persistence-test.gfa";

        writeStringToFile(testFileName, "Content before modification");
        final String hashBeforeModification = getStoredFileHash(testFileName);

        writeStringToFile(testFileName, "Content after modification");
        final String hashAfterModification = getStoredFileHash(testFileName);

        assertThat(hashBeforeModification).isNotEqualTo(hashAfterModification);

        if (!(new File(testFileName)).delete()
                || !(new File(testFileName + FileDatabaseDriver.DB_FILE_EXTENSION)).delete()) {
            throw new IOException("Failed to clean up test file and database.");
        }
    }

    /**
     * Writes the given content to a file.
     *
     * @param fileName the name of the file
     * @param content  the content
     * @throws IOException in case of an error during IO operations
     */
    private void writeStringToFile(final String fileName, final String content) throws IOException {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName),
                "UTF-8"))) {
            out.println(content);
        }
    }

    /**
     * Returns the stored hash digest of a certain file.
     *
     * @param fileName the file
     * @return the digest as it is stored in the database
     * @throws SQLException in the case of an error during SQL operations
     */
    private String getStoredFileHash(final String fileName) throws SQLException, IOException {
        try (FileDatabase testDatabase = new FileDatabase(fileName)) {
            return testDatabase.getFileMetadata().getMetadataValue(FileMetadata.DIGEST_KEY_NAME);
        }
    }
}
