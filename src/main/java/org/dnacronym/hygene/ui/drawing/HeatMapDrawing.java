package org.dnacronym.hygene.ui.drawing;

import javafx.beans.property.DoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;


/**
 * Draws a simple heat map on the given canvas.
 */
public final class HeatMapDrawing {
    private static final Color MIN_COLOR = Color.DARKBLUE;
    private static final Color MAX_COLOR = Color.INDIANRED;

    private List<Integer> buckets;
    private int minValue;
    private int maxValue;

    private GraphicsContext graphicsContext;
    private DoubleProperty canvasHeight;
    private DoubleProperty canvasWidth;


    /**
     * Sets the buckets for use during drawing.
     * <p>
     * The more buckets there are, the more continuous the heat map will look.<br>
     * Prompts a draw.
     *
     * @param buckets the buckets used for drawing
     */
    public void setBuckets(final List<Integer> buckets) {
        this.buckets = buckets;

        maxValue = 0;
        minValue = Integer.MAX_VALUE;

        for (final Integer integer : buckets) {
            maxValue = Math.max(maxValue, integer);
            minValue = Math.min(minValue, integer);
        }

        draw();
    }

    /**
     * Sets the {@link Canvas} for use during drawing.
     * <p>
     * Prompts a draw.
     *
     * @param canvas the {@link Canvas} on which drawing will take place
     */
    public void setCanvas(final Canvas canvas) {
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.canvasWidth = canvas.widthProperty();
        this.canvasHeight = canvas.heightProperty();

        canvasWidth.addListener((observable, oldValue, newValue) -> draw());
        canvasHeight.addListener((observable, oldValue, newValue) -> draw());

        draw();
    }

    /**
     * Redraw on the given {@link Canvas}.
     */
    private void draw() {
        if (graphicsContext == null || buckets == null) {
            return;
        }

        graphicsContext.clearRect(0, 0, canvasWidth.get(), canvasHeight.get());
        drawHeatMap(minValue, maxValue, buckets);
    }

    /**
     * Draw a heat map.
     *
     * @param minValue minimum value found in the buckets
     * @param maxValue maximum value found in the buckets
     * @param buckets  buckets to draw
     */
    private void drawHeatMap(final int minValue, final int maxValue, final List<Integer> buckets) {
        final double bucketWidth = canvasWidth.get() / buckets.size();
        double xPos = 0;
        double prevValue = 0; // used to allow smoother transition in heat map

        graphicsContext.setFill(Color.RED);
        for (final Integer bucket : buckets) {
            graphicsContext.setFill(
                    MIN_COLOR.interpolate(MAX_COLOR, (((double) bucket - minValue) / 2 + prevValue / 2) / maxValue));

            final double bucketHeight = (double) (bucket - minValue) / maxValue * canvasHeight.get();
            graphicsContext.fillRect(xPos, 0, bucketWidth, bucketHeight);

            prevValue = bucket;
            xPos += bucketWidth;
        }
    }
}
