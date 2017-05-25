package org.dnacronym.hygene.ui.util;

import javafx.scene.canvas.Canvas;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.NodeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link GraphDimensionsCalculator}s.
 */
final class GraphDimensionsCalculatorTest {
    private GraphDimensionsCalculator graphDimensionsCalculator;


    @BeforeEach
    void beforeEach() {
        graphDimensionsCalculator = new GraphDimensionsCalculator();
    }


    @Test
    void testComputeXPosition() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 10, 0);
        assertThat(graphDimensionsCalculator.computeXPosition(0)).isEqualTo(-400);
    }

    @Test
    void testComputeRightXPosition() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 10, 0);
        assertThat(graphDimensionsCalculator.computeRightXPosition(0)).isEqualTo(0);
    }

    @Test
    void testComputeYPosition() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 10, 0);
        assertThat(graphDimensionsCalculator.computeYPosition(0)).isEqualTo(-300.0 / 2);
    }

    @Test
    void testComputeMiddleYPosition() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 10, 0);
        assertThat(graphDimensionsCalculator.computeMiddleYPosition(0)).isEqualTo(-300.0 / 2);
    }

    @Test
    void testComputeWidth() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 20, 0);
        assertThat(graphDimensionsCalculator.computeWidth(0)).isEqualTo(400);
    }

    @Test
    void testGetNodeHeight() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 0, 105);
        assertThat(graphDimensionsCalculator.getNodeHeight()).isEqualTo(105);
    }

    @Test
    void testComputeAndGetLaneHeight() {
        graphDimensionsCalculator.calculate(createGraph(), mockCanvas(), 0, 0, 0);
        assertThat(graphDimensionsCalculator.getLaneHeight()).isEqualTo(-300);
    }

    private Graph createGraph() {
        return new Graph(new int[][]{
                NodeBuilder.start()
                        .withSequenceLength(500)
                        .withUnscaledXPosition(600)
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
