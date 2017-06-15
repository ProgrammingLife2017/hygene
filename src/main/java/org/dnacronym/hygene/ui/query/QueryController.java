package org.dnacronym.hygene.ui.query;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the query.
 */
public final class QueryController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(QueryController.class);

    private Query query;

    @FXML
    private TextField sequenceField;


    /**
     * Creates instance of a {@link QueryController}.
     */
    public QueryController() {
        try {
            setQuery(Hygene.getInstance().getQuery());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to instantiate " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // Nothing to initialize
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
