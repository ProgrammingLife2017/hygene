package org.dnacronym.hygene.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for the {@code RecentFiles} class.
 */
class RecentFilesTest {
    private RecentFiles recentFiles;

    @BeforeEach
    void setUp() throws IOException {
        recentFiles = new RecentFiles();
        recentFiles.reset();
    }

    @Test
    void testAddAndGetFilesSimple() throws IOException {
        final String testFilePath = "Path\\to\\test.txt";
        recentFiles.add(testFilePath);

        final List<String> filePaths = recentFiles.getAll();
        assertThat(filePaths).contains(testFilePath);
    }

    @Test
    void testAddAndGetFilesAddToFront() throws IOException {
        final String testFilePath1 = "Path\\to\\test1.txt";
        final String testFilePath2 = "Path\\to\\test2.txt";
        recentFiles.add(testFilePath1);
        recentFiles.add(testFilePath2);

        final List<String> filePaths = recentFiles.getAll();
        assertThat(filePaths.indexOf(testFilePath1)).isGreaterThan(filePaths.indexOf(testFilePath2));
    }

    @Test
    void testGetFilesWithNonExistingFile() throws IOException {
        if (!recentFiles.getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        assertThat(recentFiles.getAll()).isEmpty();
    }

    @Test
    void testAddFileWithNonExistingFile() throws IOException {
        final String testFilePath = "Path\\to\\test.txt";

        if (!recentFiles.getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        recentFiles.add(testFilePath);
        assertThat(recentFiles.getAll()).contains(testFilePath);
    }

    @Test
    void testResetFiles() throws IOException {
        assertThat(recentFiles.getDataFile()).exists();
        assertThat(recentFiles.getAll()).isEmpty();
    }
}
