package org.dnacronym.insertproductname.ui.controller;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.ui.visualizer.GraphStreamVisualiser;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public class GraphController implements Initializable {
    private static final GraphStreamVisualiser visualiser = new GraphStreamVisualiser(true, true);

    private static final SwingNode graphSwingNode = new SwingNode();

    @FXML
    private Pane graphPane;

    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        graphPane.getChildren().add(graphSwingNode);

//        final ObjectProperty<SequenceGraph> sequenceGraph = DNAApplication.getGraphStore().sequenceGraphProperty();
//        sequenceGraph.addListener((observable, oldValue, newValue) -> {
//            visualiser.populateGraph(sequenceGraph);
//        });
//
//        if (sequenceGraph.get() != null) {
//            visualiser.populateGraph(sequenceGraph);
//        }
    }

    /**
     * Update the swing node to display the new {@link SequenceGraph}.
     *
     * @param sequenceGraph new {@link SequenceGraph} to display.
     */
    protected final void updateGraphSwingNode(final SequenceGraph sequenceGraph) {
        visualiser.populateGraph(sequenceGraph);

//        Viewer view = v.sequenceGraph.display();
//        view.getDefaultView().resizeFrame(1200, 750);
//        view.getDefaultView().getCamera().setViewPercent(0.01);
    }
}
