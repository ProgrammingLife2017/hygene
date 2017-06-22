package org.dnacronym.hygene.ui.path;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Pattern;


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

    @FXML
    private Label pathsFound;

    @FXML
    private CheckBox matchCase;

    @FXML
    private CheckBox regex;

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
     * Adds event listeners to update the list of genomes when the user selects a specific genome.
     * <p>
     * Will update the
     */
    void addListeners() {
        final FilteredList<GenomePath> filteredList = new FilteredList<>(graphVisualizer.getGenomePathsProperty(),
                s -> s.getName().contains(searchField.textProperty().get()));

        pathList.setItems(filteredList);

        // Updates the filtered list predicate on a search
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(getPredicate(newValue)));

        pathsFound.textProperty().setValue("Paths found: " + filteredList.size());

        // Updates the label with the number of paths that are displayed
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                pathsFound.textProperty().setValue("Paths found: " + filteredList.size()));
    }

    /**
     * Gets the predicate that will be used to match genomes in the search list.
     * <p>
     * This method will return a {@link Predicate} will, depending on the search method selected in the GUI,
     * return either substring, substring case-sensitive, or regex.
     *
     * @param query the search query
     * @return the predicate
     */
    Predicate<GenomePath> getPredicate(final String query) {
        if (regex.isSelected()) {
            return getRegexPredicate(query);
        } else if (matchCase.isSelected()) {
            return getSubstringPredicate(query, true);
        }

        return getSubstringPredicate(query, false);
    }

    /**
     * Will return a substring predicate.
     * <p>
     * This predicate will evaluate to true if: the query is null, an empty string, or a substring of the value being
     * matches.
     *
     * @param query     the search query
     * @param matchCase to make the search case sensitive
     * @return the predicate
     */
    Predicate<GenomePath> getSubstringPredicate(final String query, final boolean matchCase) {
        return s -> query == null
                || query.length() == 0
                || s.getName().toLowerCase(Locale.US).contains(query.toLowerCase(Locale.US)) && !matchCase
                || s.getName().contains(query) && matchCase;
    }


    /**
     * Will return a regex predicate.
     * <p>
     * The predicate returned will preform a regex match on the input.
     *
     * @param query the search query
     * @return the predicate
     */
    Predicate<GenomePath> getRegexPredicate(final String query) {
        return s -> query == null || query.length() == 0 || Pattern.matches(query, s.getName());
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
}
