package org.dnacronym.hygene.ui.controller.graph;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.util.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public final class GraphSliderController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphSliderController.class);
    private static final int GRAPH_SLIDER_SEGMENTS = 10;

    private static final int MINIMUM_GRAPH_THUMB_WIDTH = 20;
    private static final String GRAPH_SLIDER_STYLE = "-fx-pref-height: 30;"
            + "-fx-pref-width: %d;"
            + "-fx-arc-height: 0;"
            + "-fx-arc-width: 0;";

    private GraphVisualizer graphVisualizer;
    private GraphDimensionsCalculator graphDimensionsCalculator;
    private GraphStore graphStore;

    @FXML
    private Pane graphSliderPane;
    @FXML
    private Slider graphSlider;


    /**
     * Create new instance of a {@link GraphSliderController}.
     */
    public GraphSliderController() {
        try {
            setGraphVisualiser(Hygene.getInstance().getGraphVisualizer());
            setGraphDimensionCalculator(Hygene.getInstance().getGraphDimensionsCalculator());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize GraphSliderController.", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphSlider.maxProperty().bind(Bindings.max(
                0,
                Bindings.subtract(graphVisualizer.getNodeCountProperty(), 1)));
        graphSlider.majorTickUnitProperty().bind(Bindings.divide(
                Bindings.max(GRAPH_SLIDER_SEGMENTS, graphVisualizer.getNodeCountProperty()),
                GRAPH_SLIDER_SEGMENTS));

        graphSlider.valueProperty().bindBidirectional(graphVisualizer.getCenterNodeIdProperty());

        graphVisualizer.getCenterNodeIdProperty().addListener(
                (observable, oldNodeId, newNodeId) -> graphSlider.setValue(newNodeId.doubleValue()));

        graphDimensionsCalculator.getMinXNodeIdProperty().addListener(
                (observable, oldValue, newValue) -> updateGraphSlider(
                        newValue.intValue(),
                        graphDimensionsCalculator.getMaxXNodeIdProperty().get(),
                        graphVisualizer.getNodeCountProperty().get()));
        graphDimensionsCalculator.getMaxXNodeIdProperty().addListener(
                (observable, oldValue, newValue) -> updateGraphSlider(
                        graphDimensionsCalculator.getMinXNodeIdProperty().get(),
                        newValue.intValue(),
                        graphVisualizer.getNodeCountProperty().get()));

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
     * Sets the {@link GraphDimensionsCalculator} for use by the controller.
     * <p>
     * This allows the slider to change the width of the thumb when zooming.
     *
     * @param graphDimensionCalculator {@link GraphDimensionsCalculator} for use by the controller
     */
    void setGraphDimensionCalculator(final GraphDimensionsCalculator graphDimensionCalculator) {
        this.graphDimensionsCalculator = graphDimensionCalculator;
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

    /**
     * Update the width of the width of the slider of the slider.
     *
     * @param minXNodeId {@link org.dnacronym.hygene.models.Node} id of node at the leftmost position onscreen
     * @param maxXNodeId {@link org.dnacronym.hygene.models.Node} id of node of the rightmost position onscreen
     * @param nodeCount  total amount of {@link org.dnacronym.hygene.models.Node}s in the
     *                   {@link org.dnacronym.hygene.models.Graph}.
     */
    void updateGraphSlider(final int minXNodeId, final int maxXNodeId, final int nodeCount) {
        final double sliderWidth = graphSlider.getWidth();
        final double thumbPortionOfSlider = (double) (maxXNodeId - minXNodeId) / nodeCount;
        final int newThumbWidth = (int) Math.round(sliderWidth * thumbPortionOfSlider);

        final StackPane thumb = (StackPane) graphSlider.lookup(".thumb");
        thumb.setStyle(String.format(GRAPH_SLIDER_STYLE, Math.max(newThumbWidth, MINIMUM_GRAPH_THUMB_WIDTH)));
    }
}
