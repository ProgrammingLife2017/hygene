package org.dnacronym.hygene.ui.graph;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.GraphIterator;
import org.dnacronym.hygene.graph.SequenceDirection;
import org.dnacronym.hygene.graph.layout.FafospLayerer;
import org.dnacronym.hygene.ui.drawing.HeatMapDrawing;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Controller of the slider which allows traversing the graph.
 */
public final class GraphSliderController implements Initializable {
    private static final int BUCKET_COUNT = 2500;

    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private GraphStore graphStore;


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
            graphScrollBar.setMax(newValue.getRealEndXPosition(sentinelId - 1));
        });

        heatMapCanvas.widthProperty().bind(sliderPane.widthProperty());
        heatMapCanvas.heightProperty().bind(sliderPane.heightProperty());

        final HeatMapDrawing heatMapDrawing = new HeatMapDrawing();
        heatMapDrawing.setCanvas(heatMapCanvas);

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) ->
                heatMapDrawing.setBuckets(generateBuckets(newValue.getGraph())));

        graphScrollBar.valueProperty().bindBidirectional(graphDimensionsCalculator.getViewPointProperty());
        graphDimensionsCalculator.getViewRadiusProperty().addListener(observable -> graphScrollBar.setVisibleAmount(
                (double) graphDimensionsCalculator.getMaxX() - graphDimensionsCalculator.getMinX()));

        graphScrollBar.managedProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
        graphScrollBar.visibleProperty().bind(graphDimensionsCalculator.getGraphProperty().isNotNull());
    }

    /**
     * Generate a list of buckets of size {@value BUCKET_COUNT}.
     *
     * @param graph the graph which to generate the buckets for
     * @return the list of buckets representing node densities in the graph
     */
    private List<Integer> generateBuckets(final Graph graph) {
        final Map<Integer, Integer> buckets = new HashMap<>(BUCKET_COUNT);

        final int sinkId = graph.getNodeArrays().length - 1;
        final long graphWidth = (long) graph.getUnscaledXPosition(sinkId) * 1000 + graph.getLength(sinkId);
        final long bucketSize = Math.round((double) graphWidth / BUCKET_COUNT);

        new GraphIterator(graph).visitAll(SequenceDirection.RIGHT, nodeId -> {
            final int left = Math.toIntExact(graph.getRealStartXPosition(nodeId) / bucketSize);
            final int right = Math.toIntExact(graph.getRealEndXPosition(nodeId) / bucketSize);

            for (int pos = left; pos <= right; pos++) {
                buckets.put(pos, buckets.containsKey(pos) ? buckets.get(pos) + 1 : 1);
            }
        });

        return new ArrayList<>(buckets.values());
    }
}
