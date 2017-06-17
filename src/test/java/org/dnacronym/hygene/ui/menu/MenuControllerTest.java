package org.dnacronym.hygene.ui.menu;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.recent.RecentFiles;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.settings.Settings;
import org.dnacronym.hygene.ui.settings.SettingsView;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link MenuController}.
 */
final class MenuControllerTest extends UITestBase {
    private MenuController menuController;


    @Override
    public void beforeEach() {
        menuController = new MenuController();
    }


    @Test
    void testGfaFileOpenerAccept() throws Exception {
        final CompletableFuture<Object> future = new CompletableFuture<>();

        interact(() -> {
            try {
                final GraphStore graphStore = spy(GraphStore.class);
                final FileChooser fileChooser = mock(FileChooser.class);
                final File file = mock(File.class);

                // Due to the internal structure of JavaFX, the FileChooser only returns the file the second time.
                final Window owner = Hygene.getInstance().getPrimaryStage().getScene().getWindow();
                when(fileChooser.showOpenDialog(owner)).thenReturn(file);
                menuController.setGfaFileChooser(fileChooser);
                menuController.setGraphStore(graphStore);
                menuController.openGfaFileAction(mock(ActionEvent.class));

                verify(file).getParentFile();

                future.complete(null);
            } catch (final Exception e) {
                future.complete(null);
                fail(e.getMessage());
            }
        });

        future.get();
    }

    @Test
    void testGfaFileOpenerCancel() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);

        menuController.setGfaFileChooser(fileChooser);
        menuController.setGraphStore(graphStore);
        menuController.openGfaFileAction(mock(ActionEvent.class));

        verify(graphStore, never()).loadGfaFile(any(File.class), any(ProgressUpdater.class));
    }

    @Test
    void testGffFileOpenerAccept() throws Exception {
        final CompletableFuture<Object> future = new CompletableFuture<>();

        interact(() -> {
            try {
                final GraphStore graphStore = mock(GraphStore.class);
                final FileChooser fileChooser = mock(FileChooser.class);
                final File file = mock(File.class);

                // Due to the internal structure of JavaFX, the FileChooser only returns the file the second time.
                final Window owner = Hygene.getInstance().getPrimaryStage().getScene().getWindow();
                when(fileChooser.showOpenDialog(owner)).thenReturn(file);
                menuController.setGffFileChooser(fileChooser);
                menuController.setGraphStore(graphStore);
                menuController.openGffFileAction(mock(ActionEvent.class));

                verify(file).getParentFile();

                future.complete(null);
            } catch (final Exception e) {
                future.complete(null);
                fail(e.getMessage());
            }
        });

        future.get();
    }

    @Test
    void testGffFileOpenerCancel() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);
        final FileChooser fileChooser = mock(FileChooser.class);

        menuController.setGffFileChooser(fileChooser);
        menuController.setGraphStore(graphStore);
        menuController.openGffFileAction(mock(ActionEvent.class));

        verify(graphStore, never()).loadGffFile(any(File.class), any(ProgressUpdater.class));
    }

    @Test
    void testInitFileChooser() {
        final FileChooser fileChooser = menuController.initFileChooser("Text", "txt");

        assertThat(fileChooser).isNotNull();
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

    @Test
    void testOpenSettingsWindowInit() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);

        final CompletableFuture<SettingsView> future = new CompletableFuture<>();

        interact(() -> {
            menuController.setSettings(new Settings(graphStore));
            menuController.settingsAction();
            future.complete(menuController.getSettingsView());
        });

        assertThat(future.get()).isNotNull();
    }

    @Test
    void testOpenSettingsWindowState() throws Exception {
        final GraphStore graphStore = mock(GraphStore.class);

        final CompletableFuture<SettingsView> future = new CompletableFuture<>();

        interact(() -> {
            menuController.setSettings(new Settings(graphStore));
            menuController.settingsAction();
            future.complete(menuController.getSettingsView());
        });

        assertThat(future.get().getStage().isShowing()).isTrue();
    }
}
