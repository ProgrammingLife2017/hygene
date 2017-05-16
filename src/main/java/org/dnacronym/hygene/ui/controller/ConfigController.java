package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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
public final class ConfigController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConfigController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull Slider nodeHeight;
    @FXML
    private @MonotonicNonNull ColorPicker edgeColors;

    @FXML
    private @MonotonicNonNull CheckBox showBorders;
    @FXML
    private @MonotonicNonNull Slider dashWidth;

    @Override
    @SuppressWarnings("squid:S1067") // Suppress complex if statements for CF
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialise Configuration Controller.", e);
        }

        if (nodeHeight != null && edgeColors != null
                && graphVisualizer != null && showBorders != null && dashWidth != null) {
            nodeHeight.valueProperty().bindBidirectional(graphVisualizer.getNodeHeightProperty());
            edgeColors.valueProperty().bindBidirectional(graphVisualizer.getEdgeColorProperty());
            showBorders.selectedProperty().bindBidirectional(graphVisualizer.getDisplayBordersProperty());
            dashWidth.valueProperty().bindBidirectional(graphVisualizer.getBorderDashLengthProperty());

            nodeHeight.valueProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
            edgeColors.valueProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
            showBorders.selectedProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
            dashWidth.valueProperty().addListener((ob, oldV, newV) -> redrawGraphVisualiser(graphVisualizer));
        }
    }

    /**
     * Redraw the {@link GraphVisualizer}.
     *
     * @param graphVisualizer {@link GraphVisualizer} to redraw
     */
    void redrawGraphVisualiser(final @Nullable GraphVisualizer graphVisualizer) {
        if (graphVisualizer != null) {
            graphVisualizer.redraw();
        }
    }

    /**
     * Set the {@link GraphVisualizer}. This allows the sliders to change the properties of the {@link GraphVisualizer}.
     *
     * @param graphVisualiser graph pane to set in the controller.
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualiser) {
        this.graphVisualizer = graphVisualiser;
    }
}
