package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Test suite for the {@link FileDatabase} class.
 */
final class FileDatabaseTest extends FileDatabaseBaseTest {
    private FileDatabase fileDatabase;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabase = new FileDatabase(GFA_FILE_NAME);
    }


    @Test
    void testExistingDatabase() throws SQLException, IOException {
        fileDatabase.close();
        final Throwable throwable = catchThrowable(() -> fileDatabase = new FileDatabase(GFA_FILE_NAME));

        assertThat(throwable).isNull();
    }

    @Test
    void testGetFileName() {
        assertThat(fileDatabase.getFileName()).isEqualTo(GFA_FILE_NAME);
    }

    @Test
    void testGetFileDatabaseDriver() {
        assertThat(fileDatabase.getFileDatabaseDriver()).isNotNull();
    }


    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();
        super.tearDown();
    }
}
