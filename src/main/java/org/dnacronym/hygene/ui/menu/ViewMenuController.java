package org.dnacronym.hygene.ui.menu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.dnacronym.hygene.ui.console.ConsoleView;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the view menu.
 */
public final class ViewMenuController implements Initializable {
    @Inject
    private SequenceVisualizer sequenceVisualizer;
    @Inject
    private GraphStore graphStore;
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private ConsoleView consoleView;

    @FXML
    private MenuItem toggleSequenceVisualizer;
    @FXML
    private MenuItem openBookmarks;
    @FXML
    private MenuItem openGenomePaths;
    @FXML
    private MenuItem toggleAggregation;

    private final BooleanProperty aggregationProperty;


    /**
     * Creates a new {@link ViewMenuController} instance.
     */
    public ViewMenuController() {
        aggregationProperty = new SimpleBooleanProperty(true);
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        toggleSequenceVisualizer.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        openBookmarks.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        openGenomePaths.disableProperty().bind(graphStore.getGfaFileProperty().isNull());
        toggleAggregation.disableProperty().bind(graphStore.getGfaFileProperty().isNull());

        aggregationProperty.addListener((observable, oldValue, newValue) -> {
            graphDimensionsCalculator.getCenterPointQuery().setAggregateNodes(newValue);
            graphDimensionsCalculator.getCenterPointQuery().query(
                    graphDimensionsCalculator.getCenterPointQuery().getCentre(),
                    graphDimensionsCalculator.getCenterPointQuery().getRadius()
            );
        });

        toggleSequenceVisualizer.textProperty().bind(Bindings.when(sequenceVisualizer.getVisibleProperty())
                .then("Hide node s_equence")
                .otherwise("Show node s_equence"));
    }

    /**
     * Sets the {@link SequenceVisualizer} for use by the controller.
     *
     * @param sequenceVisualizer the {@link SequenceVisualizer} for use by the controller
     */
    void setSequenceVisualizer(final SequenceVisualizer sequenceVisualizer) {
        this.sequenceVisualizer = sequenceVisualizer;
    }

    /**
     * When the user wants to toggle the visibility of the sequence visualizer pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleSequenceVisualizerAction(final ActionEvent actionEvent) {
        sequenceVisualizer.getVisibleProperty().set(!sequenceVisualizer.getVisibleProperty().get());
        actionEvent.consume();
    }

    @FXML
    void openBookmarksAction(final ActionEvent actionEvent) {
        // TODO
        actionEvent.consume();
    }

    @FXML
    void openGenomePathsAction(final ActionEvent actionEvent) {
        // TODO
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the aggregation of nodes.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleAggregationAction(final ActionEvent actionEvent) {
        aggregationProperty.set(!aggregationProperty.get());
        actionEvent.consume();
    }

    /**
     * Opens an independent stage showing the console window.
     *
     * @param actionEvent {@link ActionEvent} associated with the event
     * @throws IOException if unable to located the FXML resource
     */
    @FXML
    void openConsoleAction(final ActionEvent actionEvent) throws IOException {
        consoleView.bringToFront();

        actionEvent.consume();
    }

    /**
     * Returns the {@link ConsoleView} attached to this menu.
     *
     * @return the {@link ConsoleView}
     */
    public ConsoleView getConsoleView() {
        return consoleView;
    }
}
