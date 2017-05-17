package org.dnacronym.hygene.ui.util;

import javafx.scene.canvas.Canvas;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


final class GraphDimensionsCalculatorTest {

    @Test
    void testComputeDiameter() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 10, 25, 0
        );

        assertThat(calculator.computeDiameter()).isEqualTo(15);
    }

    @Test
    void testComputeXPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 10, 0
        );

        assertThat(calculator.computeXPosition(0)).isEqualTo(20 / 10 * 400);
    }

    @Test
    void testComputeXPositionWithDiameter0() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 0, 0
        );

        assertThat(calculator.computeXPosition(0)).isEqualTo(0);
    }

    @Test
    void testComputeRightXPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 10, 0
        );

        assertThat(calculator.computeRightXPosition(0)).isEqualTo(800 + 400);
    }

    @Test
    void testComputeYPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 2, 0, 10, 50
        );

        assertThat(calculator.computeYPosition(0)).isEqualTo(30 * 150 + 150 / 2 - 50 / 2);
    }

    @Test
    void testComputeMiddleYPosition() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 2, 0, 10, 50
        );

        assertThat(calculator.computeMiddleYPosition(0)).isEqualTo(4550 + 25);
    }

    @Test
    void testComputeWidth() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 2, 0, 20, 50
        );

        assertThat(calculator.computeWidth(0)).isEqualTo(10.0 / 20 * 400);
    }

    @Test
    void testGetNodeHeight() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 0, 0, 0, 50
        );

        assertThat(calculator.getNodeHeight()).isEqualTo(50);
    }

    @Test
    void testComputeAndGetLaneHeight() {
        final GraphDimensionsCalculator calculator = new GraphDimensionsCalculator(
                createGraph(), mockCanvas(), 3, 0, 0, 0
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
