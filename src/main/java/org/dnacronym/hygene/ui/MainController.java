package org.dnacronym.hygene.ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller of the main application.
 */
public final class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

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
    private TitledPane nodeProperties;
    @FXML
    private ScrollPane rightPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            Hygene.getInstance().getPrimaryStage().setMinHeight(650.0);
            Hygene.getInstance().getPrimaryStage().setMinWidth(920.0);
        } catch (final UIInitialisationException e) {
            LOGGER.error("Hygene instance not initialised.");
        }

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
                nodeProperties.setExpanded(true);
            }
        });
    }
}
