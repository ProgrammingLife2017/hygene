package org.dnacronym.hygene.ui.util;

import javafx.scene.canvas.Canvas;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Tests the calculations methods of {@link GraphDimensionsCalculator}.
 */
final class GraphDimensionsCalculatorTest {

    @Test
    void testComputeDiameter() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 10, 25, 0, 0, 0
        );

        assertThat(calculator.computeDiameter()).isEqualTo(15);
    }

    @Test
    void testComputeXPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 10, 0, 0, 0
        );

        assertThat(calculator.computeXPosition(0)).isEqualTo(20 / 10 * 400);
    }

    @Test
    void testComputeXPositionWithDiameter0() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 0, 0, 0
        );

        assertThat(calculator.computeXPosition(0)).isEqualTo(0);
    }

    @Test
    void testComputeRightXPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 10, 0, 0, 0
        );

        assertThat(calculator.computeRightXPosition(0)).isEqualTo(800 + 400);
    }

    @Test
    void testComputeYPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 10, -1, 0, 50
        );

        assertThat(calculator.computeYPosition(0)).isEqualTo((30 + 1) * 150 + 150 / 2 - 50 / 2);
    }

    @Test
    void testComputeMiddleYPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 10, -5, 0, 50
        );

        assertThat(calculator.computeMiddleYPosition(0)).isEqualTo(1750 + 25);
    }

    @Test
    void testComputeWidth() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 20, -1, 0, 50
        );

        assertThat(calculator.computeWidth(0)).isEqualTo(10.0 / 20 * 400);
    }

    @Test
    void testGetNodeHeight() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 0, 0, 50
        );

        assertThat(calculator.getNodeHeight()).isEqualTo(50);
    }

    @Test
    void testComputeAndGetLaneHeight() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 0, 2, 0
        );

        assertThat(calculator.getLaneHeight()).isEqualTo(100);
    }

    private Graph createGraph() {
        return new Graph(new int[][]{
                NodeBuilder.start()
                        .withSequenceLength(10)
                        .withUnscaledXPosition(20)
                        .withUnscaledYPosition(30)
                        .toArray()
        }, null);
    }

    private Canvas mockCanvas() {
        final Canvas canvas = mock(Canvas.class);

        when(canvas.getWidth()).thenReturn(400.0);
        when(canvas.getHeight()).thenReturn(300.0);

        return canvas;
    }
}
