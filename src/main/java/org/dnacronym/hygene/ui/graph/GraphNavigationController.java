package org.dnacronym.hygene.ui.graph;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.event.SnapshotButtonWasPressed;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the buttons of graph navigation.
 */
public final class GraphNavigationController implements Initializable {
    private static final int JUMP_AMOUNT = 100;
    private static final int ZOOM_AMOUNT = 2000;

    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;

    @FXML
    private StackPane graphNavigationButtons;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphNavigationButtons.visibleProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        graphNavigationButtons.managedProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
    }

    /**
     * When the user wants to go left by onscreen bases.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goLeftLargeAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewPointProperty().set(
                graphDimensionsCalculator.getViewPointProperty().get() - 100 * JUMP_AMOUNT);
        actionEvent.consume();
    }

    /**
     * When the user wants to go left by a single base.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goLeftAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewPointProperty().set(
                graphDimensionsCalculator.getViewPointProperty().get() - 100);
        actionEvent.consume();
    }

    /**
     * When the user wants to go right by a single base.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goRightAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewPointProperty().set(
                graphDimensionsCalculator.getViewPointProperty().get() + 100);
        actionEvent.consume();
    }

    /**
     * When the user wants to go right by amount of onscreen bases.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goRightLargeAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewPointProperty().set(
                graphDimensionsCalculator.getViewPointProperty().get() + 100 * JUMP_AMOUNT);
        actionEvent.consume();
    }

    /**
     * When the user wants to zoom in by a single hop.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void zoomInAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewRadiusProperty().set(
                graphDimensionsCalculator.getViewRadiusProperty().get() - ZOOM_AMOUNT
        );
        actionEvent.consume();
    }

    /**
     * When the user wants to zoom out by a single hop.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void zoomOutAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewRadiusProperty().set(
                graphDimensionsCalculator.getViewRadiusProperty().get() + ZOOM_AMOUNT
        );
        actionEvent.consume();
    }

    /**
     * When the user clicks the snapshot button.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void takeSnapshotAction(final ActionEvent actionEvent) {
        HygeneEventBus.getInstance().post(new SnapshotButtonWasPressed());
    }
}
