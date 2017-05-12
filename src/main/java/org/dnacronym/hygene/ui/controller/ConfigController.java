package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the configuration window.
 */
public class ConfigController implements Initializable {
    private static Logger logger = LogManager.getLogger(ConfigController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull Slider nodeHeight;
    @FXML
    private @MonotonicNonNull Slider nodeWidth;
    @FXML
    private @MonotonicNonNull ColorPicker edgeColors;

    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            logger.error("Failed to initialise Configuration Controller.", e);
        }

        if (nodeHeight != null && nodeWidth != null && edgeColors != null && graphVisualizer != null) {
            nodeHeight.setValue(graphVisualizer.getNodeHeightProperty().get());
            nodeWidth.setValue(graphVisualizer.getNodeWidthProperty().get());
            edgeColors.setValue(graphVisualizer.getEdgeColorProperty().get());

            nodeHeight.valueProperty().bindBidirectional(graphVisualizer.getNodeHeightProperty());
            nodeWidth.valueProperty().bindBidirectional(graphVisualizer.getNodeWidthProperty());
            edgeColors.valueProperty().bindBidirectional(graphVisualizer.getEdgeColorProperty());

            nodeHeight.valueProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
            nodeWidth.valueProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
            edgeColors.valueProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
        }
    }

    /**
     * Redraw the {@link GraphVisualizer}.
     *
     * @param graphVisualizer {@link GraphVisualizer} to redraw
     */
    final void redrawGraphVisualiser(final @Nullable GraphVisualizer graphVisualizer) {
        if (graphVisualizer != null) {
            graphVisualizer.redraw();
        }
    }

    /**
     * Set the {@link GraphVisualizer}. This allows the sliders to change the properties of the {@link GraphVisualizer}.
     *
     * @param graphVisualiser graph pane to set in the controller.
     */
    final void setGraphVisualiser(final GraphVisualizer graphVisualiser) {
        this.graphVisualizer = graphVisualiser;
    }
}
