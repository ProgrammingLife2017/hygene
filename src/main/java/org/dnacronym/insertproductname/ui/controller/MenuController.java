package org.dnacronym.insertproductname.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.insertproductname.ui.runnable.DNAApplication;
import org.dnacronym.insertproductname.ui.runnable.UIInitialisationException;
import org.dnacronym.insertproductname.ui.store.GraphStore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
public class MenuController implements Initializable {
    @MonotonicNonNull
    private FileChooser fileChooser;
    @MonotonicNonNull
    private GraphStore graphStore;


    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        setGraphStore(DNAApplication.getGraphStore());

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
     * Opens a {@link FileChooser} and sets the parent {@link javafx.stage.Window} as
     * {@link DNAApplication#getStage()#getOwner()}.
     *
     * @param event {@link ActionEvent} associated with the event.
     */
    @FXML
    protected final void openFileAction(final ActionEvent event) throws IOException {
        if (fileChooser == null || graphStore == null) {
            return;
        }

        try {
            final File gfaFile = fileChooser.showOpenDialog(DNAApplication.getStage().getOwner());

            if (gfaFile != null) {
                graphStore.load(gfaFile);
            }
        } catch (UIInitialisationException | IOException e) {
            throw new IOException(e);
        }
    }
}
