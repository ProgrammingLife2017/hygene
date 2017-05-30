package org.dnacronym.hygene.ui.graph;

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

        graphDimensionsCalculator.setGraph(createGraph());
        graphDimensionsCalculator.setCanvasSize(mockCanvas().getWidth(), mockCanvas().getHeight());
    }


    @Test
    void testComputeXPosition() {
        assertThat(graphDimensionsCalculator.computeXPosition(0)).isEqualTo(-400);
    }

    @Test
    void testComputeRightXPosition() {
        assertThat(graphDimensionsCalculator.computeRightXPosition(0)).isEqualTo(0);
    }

    @Test
    void testComputeYPosition() {
        assertThat(graphDimensionsCalculator.computeYPosition(0)).isEqualTo(300.0 / 2);
    }

    @Test
    void testComputeMiddleYPosition() {
        assertThat(graphDimensionsCalculator.computeMiddleYPosition(0)).isEqualTo(300.0 / 2);
    }

    @Test
    void testComputeWidth() {
        assertThat(graphDimensionsCalculator.computeWidth(0)).isEqualTo(400);
    }

    @Test
    void testComputeAndGetLaneHeight() {
        assertThat(graphDimensionsCalculator.getLaneHeightProperty().get()).isEqualTo(300);
    }

    @Test
    void testUpperBoundNodeId() {
        graphDimensionsCalculator.getCenterNodeIdProperty().set(1000);
        assertThat(graphDimensionsCalculator.getCenterNodeIdProperty().get()).isEqualTo(0);
    }

    @Test
    void testLowerBoundNodeId() {
        graphDimensionsCalculator.getCenterNodeIdProperty().set(-1000);
        assertThat(graphDimensionsCalculator.getCenterNodeIdProperty().get()).isEqualTo(0);
    }

    @Test
    void testUpperBoundRadius() {
        graphDimensionsCalculator.getRadiusProperty().set(1000);
        assertThat(graphDimensionsCalculator.getRadiusProperty().get()).isEqualTo(1);
    }

    private Graph createGraph() {
        return new Graph(new int[][] {
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
