package org.dnacronym.hygene.ui.store;

import org.dnacronym.hygene.core.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for the {@code RecentFiles} class.
 */
class RecentFilesTest {
    @BeforeEach
    void setUp() throws IOException {
        RecentFiles.reset();
    }


    @Test
    void testAddAndGetSimple() throws IOException {
        final String testFilePath = "Path\\to\\test.txt";
        RecentFiles.add(testFilePath);

        final List<String> filePaths = RecentFiles.getAll();
        assertThat(filePaths).contains(testFilePath);
    }

    @Test
    void testAddAndGetAddToFront() throws IOException {
        final String testFilePath1 = "Path\\to\\test1.txt";
        final String testFilePath2 = "Path\\to\\test2.txt";
        RecentFiles.add(testFilePath1);
        RecentFiles.add(testFilePath2);

        final List<String> filePaths = RecentFiles.getAll();
        assertThat(filePaths.indexOf(testFilePath1)).isGreaterThan(filePaths.indexOf(testFilePath2));
    }

    @Test
    void testGetWithNonExistingFile() throws IOException {
        if (!getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        assertThat(RecentFiles.getAll()).isEmpty();
    }

    @Test
    void testAddWithNonExistingFile() throws IOException {
        final String testFilePath = "Path\\to\\test.txt";

        if (!getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        RecentFiles.add(testFilePath);
        assertThat(RecentFiles.getAll()).contains(testFilePath);
    }

    @Test
    void testReset() throws IOException {
        assertThat(getDataFile()).exists();
        assertThat(RecentFiles.getAll()).isEmpty();
    }


    /**
     * Gets a {@code File} instance representing the data file under test.
     *
     * @return the data file.
     */
    private File getDataFile() {
        return Files.getInstance().getAppDataFile(RecentFiles.DATA_FILE_NAME);
    }
}
