package org.dnacronym.insertproductname.ui.controller;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.ui.runnable.DNAApplication;
import org.dnacronym.insertproductname.ui.store.GraphStore;
import org.dnacronym.insertproductname.ui.visualizer.GraphStreamVisualiser;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.JComponent;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public final class GraphController implements Initializable {
    private @MonotonicNonNull GraphStreamVisualiser visualiser;

    private @MonotonicNonNull GraphStore graphStore;

    private @MonotonicNonNull SwingNode swingNode;

    @FXML
    private @MonotonicNonNull Pane graphPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setGraphStore(DNAApplication.getGraphStore());

        swingNode = new SwingNode();
        visualiser = new GraphStreamVisualiser(true, true);

        if (graphPane != null && graphStore != null) {
            graphPane.getChildren().add(swingNode);

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
        if (graphPane == null || visualiser == null || swingNode == null) {
            return;
        }

        visualiser.populateGraph(sequenceGraph);

        final Viewer viewer = new Viewer(visualiser.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        final View view = viewer.addDefaultView(false);

        viewer.getDefaultView().setSize(
                (int) Math.round(graphPane.getWidth()),
                (int) Math.round(graphPane.getHeight()));

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        swingNode.setContent((JComponent) view);
    }
}
