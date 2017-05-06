package org.dnacronym.hygene.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for the {@code RecentFilesController} class.
 */
class RecentFilesControllerTest {
    private RecentFilesController recentFilesController;

    @BeforeEach
    void setUp() {
        recentFilesController = new RecentFilesController();
    }

    @Test
    void testResetRecentlyOpenedFiles() throws IOException {
        recentFilesController.resetRecentlyOpenedFiles();

        assertThat(recentFilesController.getDataFile()).exists();
        assertThat(recentFilesController.getRecentlyOpenedFiles()).isEmpty();
    }
}
