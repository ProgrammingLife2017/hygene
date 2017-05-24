package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the graph window of the application. Handles user interaction with the graph.
 */
public final class GraphController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphController.class);

    private GraphVisualizer graphVisualizer;

    @FXML
    private Canvas graphCanvas;

    @FXML
    private Pane graphPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize GraphController.", e);
            return;
        }

        graphCanvas.heightProperty().bind(graphPane.heightProperty());
        graphCanvas.widthProperty().bind(graphPane.widthProperty());

        graphVisualizer.setCanvas(graphCanvas);
    }

    /**
     * Set the {@link GraphVisualizer} in the controller.
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
}
