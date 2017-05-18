package org.dnacronym.hygene.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public class GraphSliderController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphSliderController.class);
    private static final int GRAPH_SLIDER_SEGMENTS = 10;

    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull Slider graphSlider;


    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        try {
            graphVisualizer = Hygene.getInstance().getGraphVisualizer();
        } catch (UIInitialisationException e) {
            LOGGER.error("Unable to initialize GraphSliderController.", e);
            return;
        }

        if (graphSlider != null) {
            graphSlider.maxProperty().bind(graphVisualizer.getNodeCountProperty());
            graphSlider.majorTickUnitProperty().bind(Bindings.divide(
                    Bindings.max(GRAPH_SLIDER_SEGMENTS, graphVisualizer.getNodeCountProperty()),
                    GRAPH_SLIDER_SEGMENTS));

            graphVisualizer.getCenterNodeIdProperty().addListener((observable, oldNodeId, newNodeId) -> {
                if (graphSlider != null) {
                    graphSlider.setValue(newNodeId.doubleValue());
                }
            });
        }
    }

    /**
     * Fires when the user is finished sliding the graph slider, and sets the center
     * {@link org.dnacronym.hygene.models.Node} in {@link GraphVisualizer} to the new value of the graph slider.
     */
    @FXML
    final void sliderDragDone() {
        if (graphSlider != null && graphVisualizer != null) {
            final int sliderCenterNodeId = (int) Math.round(graphSlider.getValue());
            graphVisualizer.getCenterNodeIdProperty().setValue(sliderCenterNodeId);
        }
    }
}
