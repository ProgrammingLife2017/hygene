package org.dnacronym.hygene.ui.query;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the query.
 */
public final class QueryController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(QueryController.class);

    private GraphStore graphStore;
    private Query query;

    @FXML
    private TitledPane queryPane;
    @FXML
    private TextField sequenceField;
    @FXML
    private Button queryButton;
    @FXML
    private TableView<NewNode> queriedTable;
    @FXML
    private TableColumn<NewNode, Integer> nodeColumn;
    @FXML
    private TableColumn<NewNode, String> sequenceColumn;
    @FXML
    private TableColumn<NewNode, Integer> sequenceLengthColumn;


    /**
     * Creates instance of a {@link QueryController}.
     */
    public QueryController() {
        try {
            setGraphStore(Hygene.getInstance().getGraphStore());
            setQuery(Hygene.getInstance().getQuery());
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
     * Sets the {@link Query} for use by the controller.
     *
     * @param query the {@link Query} for use by the controller
     */
    void setQuery(final Query query) {
        this.query = query;
    }

    @FXML
    void queryAction(final ActionEvent actionEvent) {
        query.query(sequenceField.getText());
        actionEvent.consume();
    }
}
