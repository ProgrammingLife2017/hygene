package org.dnacronym.hygene.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public final class GraphSliderController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphSliderController.class);
    private static final int GRAPH_SLIDER_SEGMENTS = 10;

    @FXML
    private Slider graphSlider;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final GraphVisualizer graphVisualizer;
        try {
            graphVisualizer = Hygene.getInstance().getGraphVisualizer();
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize GraphSliderController.", e);
            return;
        }

        graphSlider.maxProperty().bind(graphVisualizer.getNodeCountProperty());
        graphSlider.majorTickUnitProperty().bind(Bindings.divide(
                Bindings.max(GRAPH_SLIDER_SEGMENTS, graphVisualizer.getNodeCountProperty()),
                GRAPH_SLIDER_SEGMENTS));
        graphSlider.valueProperty().bindBidirectional(graphVisualizer.getCenterNodeIdProperty());

        graphVisualizer.getCenterNodeIdProperty().addListener(
                (observable, oldNodeId, newNodeId) -> graphSlider.setValue(newNodeId.doubleValue())
        );
    }
}
