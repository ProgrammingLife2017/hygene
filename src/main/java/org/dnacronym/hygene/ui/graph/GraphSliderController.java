package org.dnacronym.hygene.ui.graph;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import org.dnacronym.hygene.graph.layout.FafospLayerer;
import org.dnacronym.hygene.ui.drawing.HeatMapDrawing;

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
    private Pane sliderPane;
    @FXML
    private ScrollBar graphScrollBar;
    @FXML
    private Canvas heatMapCanvas;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        graphDimensionsCalculator.getGraphProperty().addListener((observable, oldValue, newValue) -> {
            final int sentinelId = newValue.getNodeArrays().length - 1;
            graphScrollBar.setMin(1);
            graphScrollBar.setMax((double) FafospLayerer.LAYER_WIDTH * newValue.getUnscaledXPosition(sentinelId - 1)
                    + newValue.getLength(sentinelId - 1));
        });

        heatMapCanvas.widthProperty().bind(sliderPane.widthProperty());
        heatMapCanvas.heightProperty().bind(sliderPane.heightProperty());

        final HeatMapDrawing heatMapDrawing = new HeatMapDrawing();
        heatMapDrawing.setCanvas(heatMapCanvas);

        heatMapDrawing.setBuckets(buckets);

        graphScrollBar.valueProperty().bindBidirectional(graphDimensionsCalculator.getViewPointProperty());
        graphDimensionsCalculator.getViewRadiusProperty().addListener(observable -> graphScrollBar.setVisibleAmount(
                (double) graphDimensionsCalculator.getMaxX() - graphDimensionsCalculator.getMinX()));

        graphScrollBar.managedProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        graphScrollBar.visibleProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
    }
}
