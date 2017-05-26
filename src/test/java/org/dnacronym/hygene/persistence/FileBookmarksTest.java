package org.dnacronym.hygene.persistence;

import org.dnacronym.hygene.models.Bookmark;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FileBookmarks} class.
 */
final class FileBookmarksTest extends FileDatabaseBaseTest {
    private FileBookmarks fileBookmarks;
    private FileDatabase fileDatabase;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabase = new FileDatabase(GFA_FILE_NAME);
        fileBookmarks = fileDatabase.getFileBookmarks();
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();
        super.tearDown();
    }


    @Test
    void testGetTable() {
        assertThat(fileBookmarks.getTable().getName()).isEqualTo(FileBookmarks.TABLE_NAME);
    }

    @Test
    void testGetAllEmpty() throws SQLException {
        assertThat(fileBookmarks.getAll()).isEmpty();
    }

    @Test
    void testStoreAndRetrieve() throws SQLException {
        final Bookmark bookmark = new Bookmark(1, 2, 3, "description");

        fileBookmarks.storeAll(Collections.singletonList(bookmark));

        final List<Bookmark> retrievedBookmarks = fileBookmarks.getAll();
        assertThat(retrievedBookmarks).hasSize(1);
        assertThat(retrievedBookmarks.get(0).getNodeId()).isEqualTo(1);
    }
}
