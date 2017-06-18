package org.dnacronym.hygene.ui.graph;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public final class GraphSliderController implements Initializable {
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;

    @FXML
    private ScrollBar graphScrollBar;


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
}
