package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the node properties window. Shows the properties of the selected node.
 */
public class NodePropertiesController implements Initializable {
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(NodePropertiesController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull TextField sequence;

    @FXML
    private @MonotonicNonNull Canvas neighbourVisualizer;
    @FXML
    private @MonotonicNonNull TextField leftNeighbours;
    @FXML
    private @MonotonicNonNull TextField rightNeighbours;

    @FXML
    private @MonotonicNonNull TextField position;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
        } catch (UIInitialisationException e) {
            logger.error("Failed to initialize NodePropertiesController.", e);
            return;
        }
    }

    final void setGraphVisualiser(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }
}
