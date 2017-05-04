package org.dnacronym.insertproductname.ui.controller;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.ui.visualizer.GraphStreamVisualiser;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public class GraphController implements Initializable {
    private static final GraphStreamVisualiser VISUALISER = new GraphStreamVisualiser(true, true);

    private static final SwingNode SWING_NODE = new SwingNode();

    @FXML
    private Pane graphPane;

    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        graphPane.getChildren().add(SWING_NODE);
    }

    /**
     * Update the swing node to display the new {@link SequenceGraph}.
     *
     * @param sequenceGraph new {@link SequenceGraph} to display.
     */
    protected final void updateGraphSwingNode(final SequenceGraph sequenceGraph) {
        VISUALISER.populateGraph(sequenceGraph);

        Viewer viewer = new Viewer(VISUALISER.getGraph(), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        View view = viewer.addDefaultView(false);

        viewer.getDefaultView().resizeFrame(
                (int) Math.round(graphPane.getWidth()),
                (int) Math.round(graphPane.getHeight()));

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        SWING_NODE.setContent((JComponent) view);
    }
}
