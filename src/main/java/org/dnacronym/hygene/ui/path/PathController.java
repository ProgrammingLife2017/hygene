package org.dnacronym.hygene.ui.path;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxListCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GenomePath;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
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
    private ListView<GenomePath> pathList;

    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphStore graphStore;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        pathList.setCellFactory(CheckBoxListCell.forListView(GenomePath::isSelectedProperty));

        addListeners();

        pathPane.visibleProperty().bind(graphStore.getGfaFileProperty().isNotNull());
    }

    /**
     * Adds event listeners to update the list of genomes when the user selects a specific node.
     */
    void addListeners() {
        pathList.itemsProperty().bindBidirectional(graphVisualizer.getSelectedPathsPropertyProperty());
//        graphVisualizer.getSelectedPathsPropertyProperty().bindBidirectional(pathList);
//        pathList.setSelectionModel();
//        graphVisualizer.getGenomeMappingProperty().addListener((s, oldValue, newValue) -> {
//            newValue.forEach((index, name) -> {
//                pathList.getItems().add(new PathListItem(name, false));
//            });
//        });


    }

    /**
     * Causes the selected path to be cleared.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onClearHighlight(final ActionEvent actionEvent) {
//        graphVisualizer.getSelectedPathProperty().set(null);
//        LOGGER.info("Cleared the currently selected genome.");
    }

    /**
     * Causes the currently selected genome, in the {@link ListView}, to be set as the selected path
     * in the {@link GraphVisualizer}.
     *
     * @param mouseEvent the {@link ActionEvent}
     */
    @FXML
    void onSetHighlight(final ActionEvent mouseEvent) {
        LOGGER.info(pathList.getSelectionModel().getSelectedItems());
//        LOGGER.info(pathList.getItems().filtered(i -> i.isOn()));
//        final String selectedGenome = pathList.getSelectionModel().getSelectedItem();
//        if (selectedGenome != null) {
//            graphVisualizer.getSelectedPathProperty().set(selectedGenome);
//            LOGGER.info("Set the currently selected genome to " + selectedGenome);
//        }
    }
}
