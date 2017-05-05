package org.dnacronym.insertproductname.ui.controller;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.ui.runnable.DNAApplication;
import org.dnacronym.insertproductname.ui.store.GraphStore;
import org.dnacronym.insertproductname.ui.visualizer.GraphStreamVisualiser;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public final class GraphController implements Initializable {
    private GraphStreamVisualiser visualiser;

    private @MonotonicNonNull GraphStore graphStore;

    @FXML
    private Pane graphPane;

    private SwingNode swingNode;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setGraphStore(DNAApplication.getGraphStore());

        swingNode = new SwingNode();
        visualiser = new GraphStreamVisualiser(true, true);

        graphPane.getChildren().add(swingNode);

        graphStore.getSequenceGraphProperty().addListener((observable, oldValue, newValue) -> {
            updateGraphSwingNode(newValue);
        });

        updateGraphSwingNode(graphStore.getSequenceGraphProperty().get());
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
    protected void updateGraphSwingNode(final SequenceGraph sequenceGraph) {
        visualiser.populateGraph(sequenceGraph);

        Viewer viewer = new Viewer(visualiser.getGraph(), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        View view = viewer.addDefaultView(false);

        viewer.getDefaultView().resizeFrame(
                (int) Math.round(graphPane.getWidth()),
                (int) Math.round(graphPane.getHeight()));

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        swingNode.setContent((JComponent) view);
    }
}
