package org.dnacronym.hygene.ui.controller.graph;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.util.GraphMovementCalculator;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public final class GraphController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphController.class);

    private GraphVisualizer graphVisualizer;
    private GraphMovementCalculator graphMovementCalculator;

    @FXML
    private Canvas graphCanvas;

    @FXML
    private Pane graphPane;


    /**
     * Create a new instance of {@link GraphController}.
     */
    public GraphController() {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setGraphMovementCalculator(Hygene.getInstance().getGraphMovementCalculator());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize GraphController.", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphCanvas.heightProperty().bind(graphPane.heightProperty());
        graphCanvas.heightProperty().bind(graphPane.heightProperty());
        graphCanvas.widthProperty().bind(graphPane.widthProperty());

        graphVisualizer.setCanvas(graphCanvas);
    }

    /**
     * Sets the {@link GraphMovementCalculator} for use by the controller.
     *
     * @param graphMovementCalculator {@link GraphMovementCalculator} for use by the controller
     */
    void setGraphMovementCalculator(final GraphMovementCalculator graphMovementCalculator) {
        this.graphMovementCalculator = graphMovementCalculator;
    }

    /**
     * Sets the {@link GraphVisualizer} in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} to store in the {@link GraphController}
     */
    void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;

        graphVisualizer.getSelectedNodeProperty().addListener((observable, oldNode, node) -> {
            if (node == null) {
                return;
            }

            try {
                LOGGER.info("Selected node id: " + node.getId() + "\n"
                        + "Sequence: " + node.retrieveMetadata().getSequence() + "\n");
            } catch (final ParseException e) {
                LOGGER.error("Metadata of node " + node.getId() + " could not be loaded");
            }
        });

        graphVisualizer.getSelectedEdgeProperty().addListener((observable, oldEdge, edge) -> {
            if (edge == null) {
                return;
            }

            try {
                LOGGER.info("Selected edge from node id: " + edge.getFrom() + " to node id: " + edge.getTo() + "\n"
                        + "Overlap: " + edge.retrieveMetadata().getOverlap() + "\n");
            } catch (final ParseException e) {
                LOGGER.error("Metadata of edge from node id: " + edge.getFrom()
                        + " to node id: " + edge.getTo() + " could not be loaded");
            }
        });
    }

    /**
     * When starting to drag on the graph pane.
     *
     * @param mouseEvent {@link MouseEvent} associated with the event
     */
    @FXML
    void onGraphPaneMousePressed(final MouseEvent mouseEvent) {
        graphMovementCalculator.onMousePressed(mouseEvent.getSceneX());
        ((Node) mouseEvent.getSource()).getScene().setCursor(Cursor.OPEN_HAND);

        mouseEvent.consume();
    }

    /**
     * When dragging to drag on the graph pane.
     *
     * @param mouseEvent {@link MouseEvent} associated with the event
     */
    @FXML
    void onGraphPaneMouseDragged(final MouseEvent mouseEvent) {
        graphMovementCalculator.onMouseDragged(mouseEvent.getSceneX());
        ((Node) mouseEvent.getSource()).getScene().setCursor(Cursor.CLOSED_HAND);

        mouseEvent.consume();
    }

    /**
     * When finished dragging on the graph pane.
     *
     * @param mouseEvent {@link MouseEvent} associated with the event
     */
    @FXML
    void onGraphPaneMouseReleased(final MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).getScene().setCursor(Cursor.DEFAULT);

        mouseEvent.consume();
    }

    /**
     * When the suer starts scrolling on the graph.
     *
     * @param scrollEvent {@link ScrollEvent} associated with the event
     */
    @FXML
    void onScrollStarted(final ScrollEvent scrollEvent) {
        primaryStage.getScene().setCursor(Cursor.CROSSHAIR);

        scrollEvent.consume();
    }

    /**
     * When the user scroll on the graph.
     *
     * @param scrollEvent {@link ScrollEvent} associated with the event
     */
    @FXML
    void onScroll(final ScrollEvent scrollEvent) {
        graphMovementCalculator.onScroll(scrollEvent.getDeltaY());

        scrollEvent.consume();
    }

    /**
     * When the user finished scrolling.
     *
     * @param scrollEvent {@link ScrollEvent} associated with the event
     */
    @FXML
    void onScrollFinished(final ScrollEvent scrollEvent) {
        primaryStage.getScene().setCursor(Cursor.DEFAULT);

        scrollEvent.consume();
    }
}
