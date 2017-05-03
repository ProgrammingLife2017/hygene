package org.dnacronym.insertproductname.ui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dnacronym.insertproductname.ui.runnable.DNAApplication;
import org.dnacronym.insertproductname.ui.store.GraphStore;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.GuiTest;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;


/**
 * Unit tests for {@link MenuController}s.
 */
public class MenuControllerTest extends GuiTest {
    @Override
    protected final Parent getRootNode() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(DNAApplication.class.getResource(DNAApplication.APPLICATION_VIEW));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }

    @Test
    public final void testFileOpenerAccept() throws IOException {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);
        final File file = mock(File.class);

        when(fileChooser.showOpenDialog(any(Stage.class))).thenReturn(file);

        click("#file");
        click("#fileOpen");
        closeCurrentWindow();

        verify(graphStore, times(1)).load(file);
    }

    @Test
    public final void testFileOpenerCancel() throws IOException {
        final GraphStore graphStore = mock(GraphStore.class);

        click("#file");
        click("#fileOpen");
        closeCurrentWindow();

        verify(graphStore, never()).load(any(File.class));
    }
}
