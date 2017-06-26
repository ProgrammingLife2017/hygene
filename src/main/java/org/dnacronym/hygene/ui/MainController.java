package org.dnacronym.hygene.ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller of the main application.
 */
public final class MainController implements Initializable {
    @Inject
    private GraphStore graphStore;
    @Inject
    private GraphVisualizer graphVisualizer;

    @FXML
    private ToggleButton toggleLeftPane;
    @FXML
    private ScrollPane leftPane;
    @FXML
    private ToggleButton toggleRightPane;
    @FXML
    private ScrollPane rightPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        leftPane.visibleProperty().bind(toggleLeftPane.selectedProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
        leftPane.managedProperty().bind(toggleLeftPane.selectedProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
        toggleLeftPane.textProperty().bind(Bindings.when(toggleLeftPane.selectedProperty())
                .then("<")
                .otherwise(">"));

        rightPane.visibleProperty().bind(toggleRightPane.selectedProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
        rightPane.managedProperty().bind(toggleRightPane.selectedProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
        toggleRightPane.textProperty().bind(Bindings.when(toggleRightPane.selectedProperty())
                .then(">")
                .otherwise("<"));

        graphVisualizer.getSelectedSegmentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                toggleLeftPane.setSelected(true);
            }
        });
    }
}
