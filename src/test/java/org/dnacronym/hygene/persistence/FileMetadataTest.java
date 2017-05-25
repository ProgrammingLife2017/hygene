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
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Test suite for the {@link FileMetadata} class.
 */
final class FileMetadataTest extends FileDatabaseBaseTest {
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
        try (final PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(testFileName),
                "UTF-8"))) {
            out.println("Content before modification");
        }

        final FileDatabase testDatabase = new FileDatabase(testFileName);
        testDatabase.close();

        try (final PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(testFileName),
                "UTF-8"))) {
            out.println("Content after modification");
        }

        final Throwable throwable = catchThrowable(() -> new FileDatabase(testFileName));

        assertThat(throwable).isInstanceOf(FileDigestDatabaseException.class);

        if (!(new File(testFileName)).delete()
                || !(new File(testFileName + FileDatabaseDriver.DB_FILE_EXTENSION)).delete()) {
            throw new IOException("Failed to clean up test file and database.");
        }
    }
}
