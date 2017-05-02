package org.dnacronym.insertproductname.ui.controller;

import javafx.fxml.Initializable;
import org.dnacronym.insertproductname.models.SequenceGraph;
import org.graphstream.ui.view.Viewer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public class GraphController implements Initializable {

    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        graphPane.getChildren().add(graphSwingNode);

//        final ObjectProperty<SequenceGraph> sequenceGraph = DNAApplication.getGraphStore().sequenceGraphProperty();
//        sequenceGraph.addListener((observable, oldValue, newValue) -> {
//            updateGraphSwingNode(newValue);
//        });
//
//        if (sequenceGraph.get() != null) {
//            updateGraphSwingNode(sequenceGraph.get());
//        }
    }

    /**
     * Update the swing node to display the new {@link SequenceGraph}.
     *
     * @param sequenceGraph new {@link SequenceGraph} to display.
     */
    protected final void updateGraphSwingNode(final SequenceGraph sequenceGraph) {

        GraphStreamVisualiser v = new GraphStreamVisualiser(sequenceGraph);

        Viewer view = v.sequenceGraph.display();
        view.getDefaultView().resizeFrame(1200, 750);
        view.getDefaultView().getCamera().setViewPercent(0.01);
    }
}
