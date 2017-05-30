package org.dnacronym.hygene.ui.graph;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the buttons of graph navigation.
 */
public final class GraphNavigationController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphNavigationController.class);
    private static final int JUMP_AMOUNT = 100;

    private GraphDimensionsCalculator graphDimensionsCalculator;
    private GraphStore graphStore;

    @FXML
    private HBox graphButtonBar;


    /**
     * Create instance of {@link GraphNavigationController}.
     */
    public GraphNavigationController() {
        try {
            setGraphMovementCalculator(Hygene.getInstance().getGraphDimensionsCalculator());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to instantiate " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphButtonBar.visibleProperty().bind(graphStore.getGfaFileProperty().isNotNull());
        graphButtonBar.managedProperty().bind(graphStore.getGfaFileProperty().isNotNull());
    }

    /**
     * Sets the {@link GraphDimensionsCalculator} for use by the controller.
     *
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} for use by the controller
     */
    void setGraphMovementCalculator(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
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
     * When the user wants to go left by {@value JUMP_AMOUNT} nodes.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goLeftLargeAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getCenterNodeIdProperty().set(
                graphDimensionsCalculator.getCenterNodeIdProperty().get() - JUMP_AMOUNT
        );

        actionEvent.consume();
    }

    /**
     * When the user wants to go left by a single node.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goLeftAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getCenterNodeIdProperty().set(
                graphDimensionsCalculator.getCenterNodeIdProperty().get() - 1
        );

        actionEvent.consume();
    }

    /**
     * When the user wants to go right by a single node.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goRightAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getCenterNodeIdProperty().set(
                graphDimensionsCalculator.getCenterNodeIdProperty().get() + 1
        );

        actionEvent.consume();
    }

    /**
     * When the user wants to go right by {@value JUMP_AMOUNT} nodes.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void goRightLargeAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getCenterNodeIdProperty().set(
                graphDimensionsCalculator.getCenterNodeIdProperty().get() + JUMP_AMOUNT
        );

        actionEvent.consume();
    }

    /**
     * When the user wants to zoom in by a single hop.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void zoomInAction(final ActionEvent actionEvent) {
        graphDimensionsCalculator.getRadiusProperty().set(
                graphDimensionsCalculator.getRadiusProperty().get() - 1
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
        graphDimensionsCalculator.getRadiusProperty().set(
                graphDimensionsCalculator.getRadiusProperty().get() + 1
        );

        actionEvent.consume();
    }
}
