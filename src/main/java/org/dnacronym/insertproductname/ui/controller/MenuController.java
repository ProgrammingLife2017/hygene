package org.dnacronym.insertproductname.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.insertproductname.ui.runnable.DNAApplication;
import org.dnacronym.insertproductname.ui.store.GraphStore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
public class MenuController implements Initializable {
    private static final String FILE_CHOOSER_TITLE = "Open GFA File";

    private static final FileChooser.ExtensionFilter GFA_FILTER =
            new FileChooser.ExtensionFilter("GFA", "*." + GraphStore.GFA_EXTENSION);

    @MonotonicNonNull
    private FileChooser fileChooser;
    @MonotonicNonNull
    private GraphStore graphStore;

    @FXML
    @MonotonicNonNull
    private MenuItem fileOpen;


    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        setGraphStore(DNAApplication.getGraphStore());

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(FILE_CHOOSER_TITLE);
        fileChooser.getExtensionFilters().add(GFA_FILTER);

        setFileChooser(fileChooser);
    }


    /**
     * Set the {@link GraphStore} in the controller. This gives the menu access to the {@link GraphStore} of the
     * application.
     *
     * @param graphStore {@link GraphStore} to store in the {@link MenuController}.
     */
    protected final void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Set the {@link FileChooser} used by the menu. A {@link FileChooser} is used to select a {@link File}.
     *
     * @param fileChooser {@link FileChooser} for {@link MenuController}.
     */
    protected final void setFileChooser(final FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    /**
     * Opens a {@link FileChooser} and sets the parent {@link javafx.stage.Window} as that of the {@link #fileOpen}
     * {@link MenuItem}.
     *
     * @param event {@link ActionEvent} associated with the event.
     */
    @FXML
    protected final void openFileAction(final ActionEvent event) {
        if (fileChooser != null && fileOpen != null && graphStore != null) {
            final File gfaFile = fileChooser.showOpenDialog(fileOpen.getParentPopup().getOwnerWindow());

            if (gfaFile != null) {
                try {
                    graphStore.load(gfaFile);
                } catch (IOException e) {
                    // TODO show exception in ui
                    e.printStackTrace();
                }
            }
        }
    }
}
