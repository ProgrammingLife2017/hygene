package org.dnacronym.hygene.ui.path;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxListCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    @FXML
    private TextField searchField;

    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphStore graphStore;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        pathList.setCellFactory(CheckBoxListCell.forListView(GenomePath::selectedProperty));

        pathList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addListeners();

        pathPane.visibleProperty().bind(graphStore.getGfaFileProperty().isNotNull());
    }

    /**
     * Adds event listeners to update the list of genomes when the user selects a specific node.
     */
    void addListeners() {
        FilteredList<GenomePath> filteredList = new FilteredList<>(graphVisualizer.getGenomePathsProperty(),
                s -> s.getName().contains(searchField.textProperty().get()));

        pathList.setItems(filteredList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(s -> {
                if (newValue == null || newValue.length() == 0) {
                    return true;
                }

                return s.getName().contains(newValue);
            });
        });
    }

    /**
     * Causes the selected path to be cleared.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onClearHighlight(final ActionEvent actionEvent) {
        LOGGER.info("Cleared the currently selected genome.");
        pathList.itemsProperty().get().forEach(genomes -> genomes.selectedProperty().set(false));
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
    }
}
