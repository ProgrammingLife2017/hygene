package org.dnacronym.hygene.ui.controller;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import org.dnacronym.hygene.ui.runnable.DNAApplication;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link MenuController}s.
 */
public class MenuControllerTest {
    private MenuController menuController;


    @BeforeAll
    static void beforeAll() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.showStage();
    }

    @BeforeEach
    final void beforeEach() throws IOException, TimeoutException {
        FxToolkit.setupApplication(DNAApplication.class);
        menuController = new MenuController();
    }

    @AfterEach
    final void afterEach() throws TimeoutException {
        FxToolkit.hideStage();
    }


    @Test
    public final void testFileOpenerAccept() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);
        final File file = mock(File.class);

        // Due to the internal structure of JavaFX, the FileChooser only returns the file the second time.
        when(fileChooser.showOpenDialog(DNAApplication.getStage().getOwner())).thenReturn(file, file);

        menuController.setFileChooser(fileChooser);
        menuController.setGraphStore(graphStore);
        menuController.openFileAction(mock(ActionEvent.class));

        verify(graphStore, times(1)).load(file);
    }

    @Test
    public final void testFileOpenerCancel() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);

        menuController.setFileChooser(fileChooser);
        menuController.setGraphStore(graphStore);
        menuController.openFileAction(mock(ActionEvent.class));

        verify(graphStore, never()).load(any(File.class));
    }
}
