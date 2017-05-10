package org.dnacronym.hygene.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.store.RecentFiles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
public final class MenuController implements Initializable {
    private @MonotonicNonNull FileChooser fileChooser;
    private @MonotonicNonNull GraphStore graphStore;

    @FXML
    private @MonotonicNonNull Menu recentFilesMenu;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphStore(Hygene.getInstance().getGraphStore());

            populateRecentFilesMenu();
            initFileChooser();
        } catch (UIInitialisationException e) {
            e.printStackTrace();
        }
    }


    /**
     * Opens a {@link FileChooser} and sets the parent {@link javafx.stage.Window} as
     * {@link Hygene#getPrimaryStage()#getOwner()}.
     *
     * @param event {@link ActionEvent} associated with the event.
     * @throws Exception if Unable to open the file, or parse the file.
     * @see GraphStore#load(File)
     */
    @FXML
    protected void openFileAction(final ActionEvent event) throws Exception {
        if (fileChooser == null || graphStore == null) {
            return;
        }

        final Stage primaryStage = Hygene.getInstance().getPrimaryStage();
        final File gfaFile = fileChooser.showOpenDialog(primaryStage.getOwner());

        if (gfaFile != null) {
            graphStore.load(gfaFile);
        }
    }


    /**
     * Initializes the file chooser dialog.
     */
    void initFileChooser() {
        final String chooserTitle = "Open GFA File";
        final FileChooser.ExtensionFilter gfaFilter =
                new FileChooser.ExtensionFilter(
                        "GFA (*." + GraphStore.GFA_EXTENSION + ")",
                        "*." + GraphStore.GFA_EXTENSION);

        final FileChooser chooser = new FileChooser();
        chooser.setTitle(chooserTitle);
        chooser.getExtensionFilters().add(gfaFilter);

        setFileChooser(chooser);
    }

    /**
     * Populates the sub-menu of recent files with items representing those files.
     *
     * @throws UIInitialisationException if initialisation of the UI fails
     */
    void populateRecentFilesMenu() throws UIInitialisationException {
        if (recentFilesMenu == null) {
            throw new UIInitialisationException("Recent files menu instance not initialised by framework.");
        }

        final List<File> recentFiles;
        try {
            recentFiles = RecentFiles.getAll();
        } catch (final IOException e) {
            throw new UIInitialisationException("Error while reading recent files from data file.");
        }

        if (!recentFiles.isEmpty()) {
            // Remove default information item (telling the user that there are no recent files)
            recentFilesMenu.getItems().clear();

            for (final File file : recentFiles) {
                final MenuItem menuItem = new MenuItem(file.getPath());
                recentFilesMenu.getItems().add(menuItem);
            }
        }
    }

    /**
     * Sets the {@link GraphStore} in the controller.
     *
     * @param graphStore the {@link GraphStore}.
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Returns the {@link FileChooser} used by the menu.
     *
     * @return the {@link FileChooser}.
     */
    @Nullable
    FileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Sets the {@link FileChooser} used by the menu.
     *
     * @param fileChooser the {@link FileChooser}.
     */
    void setFileChooser(final FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    /**
     * Sets the recentFilesMenu.
     *
     * @param recentFilesMenu the menu to be set
     */
    void setRecentFilesMenu(final Menu recentFilesMenu) {
        this.recentFilesMenu = recentFilesMenu;
    }
}
