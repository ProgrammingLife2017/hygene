package org.dnacronym.hygene.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for the {@code RecentFilesController} class.
 */
class RecentFilesControllerTest {
    private RecentFilesController recentFilesController;

    @BeforeEach
    void setUp() throws IOException {
        recentFilesController = new RecentFilesController();
        recentFilesController.resetFileList();
    }

    @Test
    void testAddAndGetFilesSimple() throws IOException {
        final String testFilePath = "Path\\to\\test.txt";
        recentFilesController.addFile(testFilePath);

        final List<String> filePaths = recentFilesController.getFiles();
        assertThat(filePaths).contains(testFilePath);
    }

    @Test
    void testAddAndGetFilesAddToFront() throws IOException {
        final String testFilePath1 = "Path\\to\\test1.txt";
        final String testFilePath2 = "Path\\to\\test2.txt";
        recentFilesController.addFile(testFilePath1);
        recentFilesController.addFile(testFilePath2);

        final List<String> filePaths = recentFilesController.getFiles();
        assertThat(filePaths.indexOf(testFilePath1)).isGreaterThan(filePaths.indexOf(testFilePath2));
    }

    @Test
    void testGetFilesWithNonExistingFile() throws IOException {
        if (!recentFilesController.getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        assertThat(recentFilesController.getFiles()).isEmpty();
    }

    @Test
    void testAddFileWithNonExistingFile() throws IOException {
        final String testFilePath = "Path\\to\\test.txt";

        if (!recentFilesController.getDataFile().delete()) {
            throw new IOException("Unable to delete file");
        }

        recentFilesController.addFile(testFilePath);
        assertThat(recentFilesController.getFiles()).contains(testFilePath);
    }

    @Test
    void testResetFiles() throws IOException {
        assertThat(recentFilesController.getDataFile()).exists();
        assertThat(recentFilesController.getFiles()).isEmpty();
    }
}
