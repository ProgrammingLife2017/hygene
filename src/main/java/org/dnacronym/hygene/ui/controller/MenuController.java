package org.dnacronym.hygene.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.DNAApplication;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
public final class MenuController implements Initializable {
    private @MonotonicNonNull FileChooser fileChooser;
    private @MonotonicNonNull GraphStore graphStore;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphStore(DNAApplication.getInstance().getGraphStore());

            final String chooserTitle = "Open GFA File";
            final FileChooser.ExtensionFilter gfaFilter =
                    new FileChooser.ExtensionFilter(
                            "GFA (*." + GraphStore.GFA_EXTENSION + ")",
                            "*." + GraphStore.GFA_EXTENSION);

            final FileChooser chooser = new FileChooser();
            chooser.setTitle(chooserTitle);
            chooser.getExtensionFilters().add(gfaFilter);

            setFileChooser(chooser);
        } catch (UIInitialisationException e) {
            e.printStackTrace();
        }
    }


    /**
     * Set the {@link GraphStore} in the controller. This gives the menu access to the {@link GraphStore} of the
     * application.
     *
     * @param graphStore {@link GraphStore} to store in the {@link MenuController}.
     */
    protected void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Set the {@link FileChooser} used by the menu. A {@link FileChooser} is used to select a {@link File}.
     *
     * @param fileChooser {@link FileChooser} for {@link MenuController}.
     */
    protected void setFileChooser(final FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    /**
     * Opens a {@link FileChooser} and sets the parent {@link javafx.stage.Window} as
     * {@link DNAApplication#getPrimaryStage()#getOwner()}.
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

        final File gfaFile = fileChooser.showOpenDialog(DNAApplication.getInstance().getPrimaryStage().getOwner());

        if (gfaFile != null) {
            graphStore.load(gfaFile);
        }
    }
}
