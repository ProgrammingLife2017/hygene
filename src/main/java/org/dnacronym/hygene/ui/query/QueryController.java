package org.dnacronym.hygene.ui.query;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the query.
 */
public final class QueryController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(QueryController.class);

    private GraphVisualizer graphVisualizer;
    private GraphStore graphStore;
    private Query query;

    @FXML
    private TitledPane queryPane;
    @FXML
    private TextField sequenceField;
    @FXML
    private TableView<NewNode> queriedTable;


    /**
     * Creates instance of a {@link QueryController}.
     */
    public QueryController() {
        try {
            setGraphStore(Hygene.getInstance().getGraphStore());
            setQuery(Hygene.getInstance().getQuery());
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to instantiate " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        queryPane.managedProperty().bind(graphStore.getGfaFileProperty().isNotNull()
                .and(query.getVisibleProperty()));
        queryPane.visibleProperty().bind(graphStore.getGfaFileProperty().isNotNull()
                .and(query.getVisibleProperty()));

        queriedTable.setRowFactory(tableView -> {
            final TableRow<NewNode> queriedTableRow = new TableRow<>();
            queriedTableRow.setOnMouseClicked(event -> {
                if (queriedTableRow.getItem() instanceof Segment && event.getClickCount() == 2
                        && !queriedTableRow.isEmpty()) {
                    graphVisualizer.setSelectedNode(((Segment) queriedTableRow.getItem()).getId());
                }
            });
            return queriedTableRow;
        });

        queriedTable.setItems(query.getQueriedNodes());
    }

    /**
     * Sets the {@link GraphStore} for use by the controller.
     *
     * @param graphStore the {@link GraphStore} for use by the controller
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Sets the {@link GraphVisualizer} for use by the controller.
     *
     * @param graphVisualizer the {@link GraphVisualizer} for use by the controller
     */
    void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Sets the {@link Query} for use by the controller.
     *
     * @param query the {@link Query} for use by the controller
     */
    void setQuery(final Query query) {
        this.query = query;
    }

    /**
     * When the user wants to perform a query.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void queryAction(final ActionEvent actionEvent) {
        try {
            query.query(sequenceField.getText());
        } catch (final ParseException e) {
            LOGGER.error("Unable to parse graph for querying.", e);
        }

        actionEvent.consume();
    }
}
