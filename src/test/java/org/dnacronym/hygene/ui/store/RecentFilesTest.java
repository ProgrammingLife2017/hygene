package org.dnacronym.hygene.ui.store;

import org.dnacronym.hygene.core.Files;
import org.junit.jupiter.api.AfterAll;
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

    @AfterAll
    static void tearDown() throws IOException {
        final File file = Files.getInstance().getAppDataFile(RecentFiles.DATA_FILE_NAME);

        java.nio.file.Files.deleteIfExists(file.toPath());
    }


    @Test
    void testAddAndGetSimple() throws IOException {
        final File testFile = new File("Path\\to\\test.gfa");
        RecentFiles.add(testFile);

        final List<File> files = RecentFiles.getAll();
        assertThat(files).contains(testFile);
    }

    @Test
    void testAddAndGetWithTrailingWhitespace() throws IOException {
        final File testFile = new File("Path\\to\\test.txt      \n");
        RecentFiles.add(testFile);

        final List<File> files = RecentFiles.getAll();
        assertThat(files.size()).isEqualTo(1);
        assertThat(files.get(0).getPath()).isEqualTo(testFile.getPath().trim());
    }

    @Test
    void testAddAndGetAddToFront() throws IOException {
        final File testFile1 = new File("Path\\to\\test1.gfa");
        final File testFile2 = new File("Path\\to\\test2.gfa");
        RecentFiles.add(testFile1);
        RecentFiles.add(testFile2);

        final List<File> files = RecentFiles.getAll();
        assertThat(files.indexOf(testFile1)).isGreaterThan(files.indexOf(testFile2));
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
        final File testFile = new File("Path\\to\\test.gfa");

        if (!getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        RecentFiles.add(testFile);
        assertThat(RecentFiles.getAll()).contains(testFile);
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
