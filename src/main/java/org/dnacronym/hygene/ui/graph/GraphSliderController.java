package org.dnacronym.hygene.ui.graph;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public final class GraphSliderController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GraphSliderController.class);

    private GraphVisualizer graphVisualizer;
    private GraphDimensionsCalculator graphDimensionsCalculator;
    private GraphStore graphStore;

    @FXML
    private ScrollBar graphScrollBar;


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
            new ErrorDialogue(e).show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphDimensionsCalculator.getCenterNodeIdProperty().addListener(
                (observable, oldNodeId, newNodeId) -> graphScrollBar.setValue(newNodeId.doubleValue()));

        graphScrollBar.maxProperty().bind(Bindings.max(
                0,
                Bindings.subtract(graphDimensionsCalculator.getNodeCountProperty(), 1)));

        graphScrollBar.valueProperty().addListener((observable, oldValue, newValue) ->
                graphDimensionsCalculator.updateCenterNodeId(newValue.intValue()));
        graphScrollBar.visibleAmountProperty().bind(Bindings.subtract(
                graphDimensionsCalculator.getMaxXNodeIdProperty(),
                graphDimensionsCalculator.getMinXNodeIdProperty()));

        graphScrollBar.managedProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
        graphScrollBar.visibleProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
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
     * Set the {@link GraphStore} in the controller.
     *
     * @param graphStore {@link GraphStore} to store in the {@link GraphController}
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }
}
