package org.dnacronym.hygene.ui.path;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller paths for the path highlighting menu.
 */
public final class PathController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(PathController.class);

    @FXML
    private TitledPane pathPane;

    @FXML
    private ListView<String> pathList;

    private GraphVisualizer graphVisualizer;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            graphVisualizer = Hygene.getInstance().getGraphVisualizer();
            addListeners();

            pathPane.visibleProperty().bind(Hygene.getInstance().getGraphStore().getGfaFileProperty().isNotNull());
        } catch (final UIInitialisationException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Adds event listeners to update the list of genomes when the user selects a specific node.
     */
    void addListeners() {
        graphVisualizer.getSelectedSegmentProperty().addListener((source, oldValue, newValue) -> {
            if (newValue != null && newValue.hasMetadata()) {
                pathList.getItems().clear();

                pathList.getItems().addAll(newValue.getMetadata().getGenomes());
            }
        });
    }

    /**
     * Will cause the selected path to be cleared.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onClearHighlight(final ActionEvent actionEvent) {
        graphVisualizer.getSelectedPathProperty().set(null);
        LOGGER.info("Cleared the currently selected genome.");
    }

    /**
     * Will cause the currently selected genome, in the {@link ListView}, to be set as the selected path
     * in the {@link GraphVisualizer}.
     *
     * @param mouseEvent the {@link ActionEvent}
     */
    @FXML
    void onSetHighlight(final ActionEvent mouseEvent) {
        final String selectedGenome = pathList.getSelectionModel().getSelectedItem();
        if (selectedGenome != null) {
            graphVisualizer.getSelectedPathProperty().set(selectedGenome);
            LOGGER.info("Set the currently selected genome to " + selectedGenome);
        }
    }
}
