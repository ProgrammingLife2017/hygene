package org.dnacronym.hygene.ui.graph;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.event.SnapshotButtonWasPressed;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the buttons of graph navigation.
 */
public final class GraphNavigationController implements Initializable {
    private static final int GO_AMOUNT = 1000;
    private static final int JUMP_AMOUNT = 100;
    private static final int ZOOM_AMOUNT = 2000;
    private static final int GO_RIGHT_LEFT_INTERVAL = 45;
    private static final int GO_RIGHT_LEFT_INTERVAL_LARGE = 70;
    private static final int ZOOM_IN_OUT_INTERVAL = 45;

    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;

    @FXML
    private StackPane graphNavigationButtons;

    @FXML
    private Button goRight;

    @FXML
    private Button goLeft;

    @FXML
    private Button goRightLarge;

    @FXML
    private Button goLeftLarge;

    @FXML
    private Button zoomIn;

    @FXML
    private Button zoomOut;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphNavigationButtons.visibleProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        graphNavigationButtons.managedProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());


        addContinuousPressHandler(goLeft, Duration.millis(GO_RIGHT_LEFT_INTERVAL),
                event -> goLeftAction(new ActionEvent()));

        addContinuousPressHandler(goRight, Duration.millis(GO_RIGHT_LEFT_INTERVAL),
                event -> goRightAction(new ActionEvent()));

        addContinuousPressHandler(goLeftLarge, Duration.millis(GO_RIGHT_LEFT_INTERVAL_LARGE),
                event -> goLeftLargeAction(new ActionEvent()));

        addContinuousPressHandler(goRightLarge, Duration.millis(GO_RIGHT_LEFT_INTERVAL_LARGE),
                event -> goRightLargeAction(new ActionEvent()));

        addContinuousPressHandler(zoomIn, Duration.millis(ZOOM_IN_OUT_INTERVAL), event ->
                zoomInAction(new ActionEvent()));

        addContinuousPressHandler(zoomOut, Duration.millis(ZOOM_IN_OUT_INTERVAL), event ->
                zoomOutAction(new ActionEvent()));
    }

    /**
     * Add an event handler to a node will trigger continuously trigger at a given interval while the button is
     * being pressed.
     *
     * @param node the {@link Node}
     * @param holdTime interval time
     * @param handler the handler
     */
    private void addContinuousPressHandler(final Node node, final Duration holdTime,
                                           final EventHandler<MouseEvent> handler) {

        class Wrapper<T> {
            T content;
        }
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> {
            handler.handle(eventWrapper.content);
            holdTimer.playFromStart();
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    /**
     * When the user wants to go left by onscreen bases.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goLeftLargeAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getViewPointProperty().set(
                graphDimensionsCalculator.getViewPointProperty().get() - GO_AMOUNT * JUMP_AMOUNT);
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
                graphDimensionsCalculator.getViewPointProperty().get() - GO_AMOUNT);
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
                graphDimensionsCalculator.getViewPointProperty().get() + GO_AMOUNT);
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
                graphDimensionsCalculator.getViewPointProperty().get() + GO_AMOUNT * JUMP_AMOUNT);
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
