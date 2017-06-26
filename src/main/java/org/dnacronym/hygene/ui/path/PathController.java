package org.dnacronym.hygene.ui.path;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
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
import java.util.regex.PatternSyntaxException;


/**
 * Controller for the path highlighting menu.
 */
public final class PathController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(PathController.class);

    @FXML
    private TitledPane pathPane;
    //
    @FXML
    private TableView<GenomePath> pathTable;
    //
    @FXML
    private TextField searchField;

    @FXML
    private Label pathsFound;

    @FXML
    private CheckBox matchCase;

    @FXML
    private CheckBox regex;

    @FXML
    private TableColumn<GenomePath, Color> colorColumn;

    @FXML
    private TableColumn<GenomePath, Boolean> selectedColumn;

    @FXML
    private TableColumn<GenomePath, String> nameColumn;

    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphStore graphStore;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nameColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getName() == null) {
                return new SimpleStringProperty("[unknown genome]");
            } else {
                return new SimpleStringProperty(cell.getValue().getName());
            }
        });

        colorColumn.setCellValueFactory(cell -> cell.getValue().getColor());

        colorColumn.setCellFactory(cell -> new TableCell<GenomePath, Color>() {
            @Override
            protected void updateItem(Color color, boolean empty) {
                super.updateItem(color, empty);

                if (color != null) {
                    setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    setBackground(Background.EMPTY);
                }
            }
        });

        selectedColumn.setCellValueFactory(cell -> cell.getValue().selectedProperty());

        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));

        final FilteredList<GenomePath> filteredList = new FilteredList<>(graphVisualizer.getGenomePathsProperty(),
                s -> s.getName().contains(searchField.textProperty().get()));

        pathTable.setItems(filteredList);

        pathTable.setEditable(true);

        pathPane.visibleProperty().bind(graphStore.getGfaFileProperty().isNotNull());

        addListeners();
    }

    /**
     * Adds event listeners to update the list of genomes when the user selects a specific genome.
     * <p>
     * Will update the
     */
    void addListeners() {
        final FilteredList<GenomePath> filteredList = new FilteredList<>(graphVisualizer.getGenomePathsProperty(),
                s -> s.getName().contains(searchField.textProperty().get()));

        pathTable.setItems(filteredList);

        // Updates the filtered list predicate on a search
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(getPredicate(newValue)));

        matchCase.selectedProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(getPredicate(searchField.getText())));

        regex.selectedProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(getPredicate(searchField.getText())));

        // Updates the label with the number of paths that are displayed
        filteredList.getSource().addListener((ListChangeListener<GenomePath>) c ->
                pathsFound.textProperty().setValue("Paths found: " + filteredList.size()));
    }

    /**
     * Gets the predicate that will be used to match genomes in the search list.
     * <p>
     * This method will return a {@link Predicate} that, depending on the search method selected in the GUI,
     * will either be substring case-insensitive, substring case-sensitive, or regex.
     *
     * @param query the search query
     * @return the predicate
     */
    Predicate<GenomePath> getPredicate(final String query) {
        if (regex.isSelected()) {
            try {
                Pattern.compile(query);
                return getRegexPredicate(query);
            } catch (PatternSyntaxException e) {
                LOGGER.debug("Invalid regex syntax", e);
            }
        } else if (matchCase.isSelected()) {
            return getSubstringPredicate(query, true);
        }

        return getSubstringPredicate(query, false);
    }

    /**
     * Return a substring predicate.
     * <p>
     * This predicate will evaluate to true if: the query is null or empty string (indicating there is no
     * user input available in the GUI), or a substring of the value being matched.
     *
     * @param query     the search query
     * @param matchCase to make the search case sensitive
     * @return the predicate
     */
    @SuppressWarnings("squid:S1067")
    Predicate<GenomePath> getSubstringPredicate(final String query, final boolean matchCase) {
        return genomePath -> query == null
                || query.length() == 0
                || genomePath.getName().toLowerCase(Locale.US).contains(query.toLowerCase(Locale.US)) && !matchCase
                || genomePath.getName().contains(query);
    }


    /**
     * Returns a regex predicate.
     * <p>
     * The predicate returned will preform a regex match on the input.
     *
     * @param query the search query
     * @return the predicate
     */
    Predicate<GenomePath> getRegexPredicate(final String query) {
        return genomePath -> query == null || query.length() == 0 || Pattern.matches(query, genomePath.getName());
    }

    /**
     * Causes the selected path to be cleared.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void onClearHighlight(final ActionEvent actionEvent) {
        LOGGER.info("Cleared the currently selected genome.");
        pathTable.itemsProperty().get().forEach(genomes -> genomes.selectedProperty().set(false));
        actionEvent.consume();
    }
}
