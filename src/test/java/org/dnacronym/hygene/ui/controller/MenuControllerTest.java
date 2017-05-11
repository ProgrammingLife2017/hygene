package org.dnacronym.hygene.ui.controller;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link MenuController}s.
 */
public class MenuControllerTest extends UITest {
    private MenuController menuController;


    @Override
    public final void beforeEach() {
        menuController = new MenuController();
    }


    @Test
    public final void testFileOpenerAccept() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);
        final File file = mock(File.class);

        // Due to the internal structure of JavaFX, the FileChooser only returns the file the second time.
        final Window owner = Hygene.getInstance().getPrimaryStage().getOwner();
        when(fileChooser.showOpenDialog(owner)).thenReturn(file, file);

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
