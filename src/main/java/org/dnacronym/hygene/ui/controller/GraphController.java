package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.ui.runnable.DNAApplication;
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
        setGraphStore(DNAApplication.getGraphStore());

        visualiser = new GraphPane();

        if (graphPane != null && graphStore != null) {
            graphStore.getSequenceGraphProperty().addListener((observable, oldGraph, newGraph) -> {
                if (newGraph != null) {
                    updateGraphSwingNode(newGraph);
                }
            });
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
     * Update the swing node to display the new {@link SequenceGraph}.
     *
     * @param sequenceGraph new {@link SequenceGraph} to display.
     */
    protected void updateGraphSwingNode(@NonNull final SequenceGraph sequenceGraph) {
        if (graphPane == null || visualiser == null) {
            return;
        }

        visualiser.visualise(sequenceGraph);
    }
}
