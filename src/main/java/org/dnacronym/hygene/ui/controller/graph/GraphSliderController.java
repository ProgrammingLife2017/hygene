package org.dnacronym.hygene.ui.controller.graph;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public final class GraphSliderController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphSliderController.class);
    private static final int GRAPH_SLIDER_SEGMENTS = 10;

    private static final int MINIMUM_GRAH_THUMB_WIDTH = 20;
    private static final String GRAPH_SLIDER_STYLE =
            "-fx-pref-height: 30;\n" +
                    "-fx-pref-width: %d;\n" +
                    "-fx-arc-height: 0;\n" +
                    "-fx-arc-width: 0;\n";

    private GraphVisualizer graphVisualizer;
    private GraphStore graphStore;

    @FXML
    private Pane graphSliderPane;
    @FXML
    private Slider graphSlider;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize GraphSliderController.", e);
            return;
        }

        graphSlider.maxProperty().bind(Bindings.max(
                0,
                Bindings.subtract(graphVisualizer.getNodeCountProperty(), 1)));
        graphSlider.majorTickUnitProperty().bind(Bindings.divide(
                Bindings.max(GRAPH_SLIDER_SEGMENTS, graphVisualizer.getNodeCountProperty()),
                GRAPH_SLIDER_SEGMENTS));

        graphSlider.valueProperty().bindBidirectional(graphVisualizer.getCenterNodeIdProperty());

        graphVisualizer.getCenterNodeIdProperty().addListener(
                (observable, oldNodeId, newNodeId) -> graphSlider.setValue(newNodeId.doubleValue()));

        graphSliderPane.managedProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
        graphSliderPane.visibleProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
    }

    /**
     * Sets the {@link GraphVisualizer}.
     * <p>
     * This allows the sliders to change the properties of the {@link GraphVisualizer}.
     *
     * @param graphVisualiser graph pane to set in the controller
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualiser) {
        this.graphVisualizer = graphVisualiser;
    }

    /**
     * Sets the {@link GraphStore} in the controller.
     *
     * @param graphStore {@link GraphStore} to store in the {@link GraphController}
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Set the {@link GraphVisualizer}. This allows the sliders to change the properties of the {@link GraphVisualizer}.
     *
     * @param graphVisualiser graph pane to set in the controller
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualiser) {
        this.graphVisualizer = graphVisualiser;
    }

    /**
     * Set the {@link GraphStore} in the controller.
     *
     * @param graphStore {@link GraphStore} to store in the {@link GraphController}
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    void updateGraphSlider(final int minX, final int maxX) {

    }
}
