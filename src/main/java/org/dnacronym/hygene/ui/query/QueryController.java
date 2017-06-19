package org.dnacronym.hygene.ui.query;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the query.
 */
public final class QueryController implements Initializable {
    @Inject
    private Query query;

    @FXML
    private TextField sequenceField;
    @FXML
    private Button queryButton;
    @FXML
    private ProgressIndicator queryProgress;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        queryProgress.visibleProperty().bind(query.getQueryingProperty());
        queryButton.disableProperty().bind(query.getQueryingProperty());
    }

    /**
     * When the user wants to perform a query.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void queryAction(final ActionEvent actionEvent) {
        query.query(sequenceField.getText());

        actionEvent.consume();
    }
}
