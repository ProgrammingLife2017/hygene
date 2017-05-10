package org.dnacronym.hygene.ui.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.runnable.DNAApplication;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.store.RecentFiles;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link MenuController}s.
 */
public final class MenuControllerTest extends UITest {
    private MenuController menuController;


    @Override
    public void beforeEach() {
        menuController = new MenuController();
    }


    @Test
    public void testFileOpenerAccept() throws Exception {
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
    public void testFileOpenerCancel() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);

        menuController.setFileChooser(fileChooser);
        menuController.setGraphStore(graphStore);
        menuController.openFileAction(mock(ActionEvent.class));

        verify(graphStore, never()).load(any(File.class));
    }

    @Test
    void testInitFileChooser() {
        menuController.initFileChooser();

        assertThat(menuController.getFileChooser()).isNotNull();
    }

    @SuppressWarnings("unchecked") // needed for the mocking of ObservableList<MenuItem>
    @Test
    void testPopulateRecentFilesMenu() throws UIInitialisationException, IOException {
        // Clean up file history (not in a global @BeforeEach, due to this test being the only test requiring it)
        RecentFiles.reset();

        final Menu recentFilesMenuMock = mock(Menu.class);
        when(recentFilesMenuMock.getItems()).thenReturn((ObservableList<MenuItem>) mock(ObservableList.class));

        // Add one test-file
        RecentFiles.add(new File("test.gfa"));

        menuController.setRecentFilesMenu(recentFilesMenuMock);
        menuController.populateRecentFilesMenu();

        verify(recentFilesMenuMock.getItems()).add(any(MenuItem.class));

        // Clean up file history
        RecentFiles.reset();
    }
}
