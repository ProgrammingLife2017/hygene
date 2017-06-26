package org.dnacronym.hygene.ui.graph;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public final class GraphController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphController.class);

    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphMovementCalculator graphMovementCalculator;
    @Inject
    private GraphStore graphStore;

    @FXML
    private Canvas graphCanvas;

    @FXML
    private Pane graphPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        addGraphVisualizerListeners();

        graphCanvas.heightProperty().bind(graphPane.heightProperty());
        graphCanvas.widthProperty().bind(graphPane.widthProperty());

        graphCanvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (graphStore.getGfaFileProperty().get() != null) {
                return;
            }

            drawWelcomeImage();
        });


        graphVisualizer.setCanvas(graphCanvas);
    }

    /**
     * Adds the {@link GraphVisualizer} listeners.
     */
    void addGraphVisualizerListeners() {
        graphVisualizer.getSelectedSegmentProperty().addListener((observable, oldNode, newNode) -> {
            if (newNode == null) {
                return;
            }

            LOGGER.info("Selected node id: " + newNode.getSegmentIds().toString() + ".");
        });

        graphVisualizer.getSelectedEdgeProperty().addListener((observable, oldEdge, newEdge) -> {
            if (newEdge == null) {
                return;
            }

            LOGGER.info("Selected edge from node id: " + newEdge.getFrom() + " to node id: " + newEdge.getTo() + "\n");
        });
    }

    /**
     * When starting to drag on the graph pane.
     *
     * @param mouseEvent {@link MouseEvent} associated with the event
     */
    @FXML
    void onGraphPaneMouseDragEntered(final MouseEvent mouseEvent) {
        graphMovementCalculator.onMouseDragEntered(mouseEvent.getSceneX());
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
     * When the user starts scrolling on the graph.
     *
     * @param scrollEvent {@link ScrollEvent} associated with the event
     */
    @FXML
    void onScrollStarted(final ScrollEvent scrollEvent) {
        ((Node) scrollEvent.getSource()).getScene().setCursor(Cursor.CROSSHAIR);

        scrollEvent.consume();
    }

    /**
     * When the user scroll on the graph.
     *
     * @param scrollEvent {@link ScrollEvent} associated with the event
     */
    @FXML
    void onScroll(final ScrollEvent scrollEvent) {
        graphMovementCalculator.onScroll(-scrollEvent.getDeltaY());

        scrollEvent.consume();
    }

    /**
     * When the user finished scrolling.
     *
     * @param scrollEvent {@link ScrollEvent} associated with the event
     */
    @FXML
    void onScrollFinished(final ScrollEvent scrollEvent) {
        ((Node) scrollEvent.getSource()).getScene().setCursor(Cursor.DEFAULT);

        scrollEvent.consume();
    }

    /**
     * Displays a welcome image on the canvas.
     */
    private void drawWelcomeImage() {
        graphCanvas.getGraphicsContext2D().clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        final Image image = new Image(getClass().getResource("/icons/welcome.png").toString());
        graphCanvas.getGraphicsContext2D().drawImage(image,
                graphCanvas.getWidth() / 2 - image.getWidth() / 4,
                graphCanvas.getHeight() / 2 - image.getHeight() / 4,
                image.getWidth() / 2,
                image.getHeight() / 2);
    }
}
