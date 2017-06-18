package org.dnacronym.hygene.ui.menu;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the view menu.
 */
public final class ViewMenuController implements Initializable {
    @Inject
    private SequenceVisualizer sequenceVisualizer;
    @Inject
    private GraphStore graphStore;

    @FXML
    private MenuItem toggleSequenceVisualizer;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        toggleSequenceVisualizer.disableProperty().bind(graphStore.getGfaFileProperty().isNull());

        toggleSequenceVisualizer.textProperty().bind(Bindings.when(sequenceVisualizer.getVisibleProperty())
                .then("Hide s_equence view")
                .otherwise("Show s_equence view"));
    }

    /**
     * Sets the {@link SequenceVisualizer} for use by the controller.
     *
     * @param sequenceVisualizer the {@link SequenceVisualizer} for use by the controller
     */
    void setSequenceVisualizer(final SequenceVisualizer sequenceVisualizer) {
        this.sequenceVisualizer = sequenceVisualizer;
    }

    /**
     * When the user wants to toggle the visibility of the sequence visualizer pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleSequenceVisualizerAction(final ActionEvent actionEvent) {
        sequenceVisualizer.getVisibleProperty().set(!sequenceVisualizer.getVisibleProperty().get());
        actionEvent.consume();
    }
}
