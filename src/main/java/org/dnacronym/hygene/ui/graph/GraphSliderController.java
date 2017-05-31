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

    private GraphDimensionsCalculator graphDimensionsCalculator;

    @FXML
    private ScrollBar graphScrollBar;


    /**
     * Create new instance of a {@link GraphSliderController}.
     */
    public GraphSliderController() {
        try {
            setGraphDimensionCalculator(Hygene.getInstance().getGraphDimensionsCalculator());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize GraphSliderController.", e);
            new ErrorDialogue(e).show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphScrollBar.maxProperty().bind(Bindings.max(
                0,
                Bindings.subtract(graphDimensionsCalculator.getNodeCountProperty(), 1)));

        graphScrollBar.valueProperty().bindBidirectional(graphDimensionsCalculator.getCenterNodeIdProperty());
        graphScrollBar.visibleAmountProperty().bind(Bindings.subtract(
                graphDimensionsCalculator.getMaxXNodeIdProperty(),
                graphDimensionsCalculator.getMinXNodeIdProperty()));

        graphScrollBar.managedProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        graphScrollBar.visibleProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
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
}
