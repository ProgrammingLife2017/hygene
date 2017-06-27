package org.dnacronym.hygene.ui.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.dnacronym.hygene.ui.MainController;
import org.dnacronym.hygene.ui.graph.GraphStore;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the navigate menu.
 */
public final class NavigateMenuController implements Initializable {
    @Inject
    private MainController mainController;
    @Inject
    private GraphStore graphStore;

    @FXML
    private MenuItem openGenomeNavigation;
    @FXML
    private MenuItem openQuery;
    @FXML
    private MenuItem openAnnotationSearch;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        openGenomeNavigation.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        openQuery.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        openAnnotationSearch.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
    }

    @FXML
    void openGenomeNavigationAction(final ActionEvent actionEvent) throws IOException {
        mainController.expandGenomeNavigationPane();
        actionEvent.consume();
    }

    @FXML
    void openQueryAction(final ActionEvent actionEvent) throws IOException {
        mainController.expandQueryPane();
        actionEvent.consume();
    }

    @FXML
    void openAnnotationSearchAction(final ActionEvent actionEvent) throws IOException {
        mainController.expandAnnotationSearchPane();
        actionEvent.consume();
    }
}
