package org.dnacronym.hygene.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller of the main application.
 */
public final class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    private GraphStore graphStore;

    @FXML
    private ToggleButton toggleLeftPane;
    @FXML
    private ScrollPane leftPane;
    @FXML
    private ToggleButton toggleRightPane;
    @FXML
    private ScrollPane rightPane;


    /**
     * Creates an instance of {@link MainController}.
     */
    public MainController() {
        try {
            graphStore = Hygene.getInstance().getGraphStore();
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        leftPane.visibleProperty().bind(toggleLeftPane.selectedProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));
        leftPane.managedProperty().bind(toggleLeftPane.selectedProperty()
                .and(graphStore.getGfaFileProperty().isNotNull()));

        rightPane.visibleProperty().bind(toggleRightPane.selectedProperty());
        rightPane.managedProperty().bind(toggleRightPane.selectedProperty());
    }
}
