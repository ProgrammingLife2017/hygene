package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.runnable.DNAApplication;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.visualizer.GraphPane;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public final class GraphController implements Initializable {
    private @MonotonicNonNull GraphPane visualiser;

    private @MonotonicNonNull GraphStore graphStore;

    @FXML
    private @MonotonicNonNull Pane graphPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphStore(DNAApplication.getInstance().getGraphStore());
            visualiser = new GraphPane();
        } catch (UIInitialisationException e) {
            e.printStackTrace();
        }

        if (graphPane != null && graphStore != null) {
            graphStore.getGfaFileProperty().addListener((observable, oldFile, newFile) -> updateGraph(newFile));
        }
    }

    /**
     * Set the {@link GraphStore} in the controller.
     *
     * @param graphStore {@link GraphStore} to store in the {@link GraphController}.
     */
    protected void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Update the swing node to display graph of the given {@link GfaFile}.
     *
     * @param gfaFile with internal graph to display.
     * @see GfaFile#getGraph()
     */
    protected void updateGraph(final GfaFile gfaFile) {
        if (graphPane == null || visualiser == null) {
            return;
        }

        try {
            visualiser.draw(gfaFile.getGraph());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
