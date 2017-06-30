package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.Canvas;
import org.assertj.core.data.Offset;
import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.NodeBuilder;
import org.dnacronym.hygene.graph.Subgraph;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.parser.GfaFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link GraphDimensionsCalculator}s.
 */
final class GraphDimensionsCalculatorTest {
    private static final double CANVAS_WIDTH = 400.0;
    private static final double CANVAS_HEIGHT = 300.0;

    private GraphDimensionsCalculator graphDimensionsCalculator;
    private GraphStore graphStore;


    @BeforeEach
    public void beforeEach() {
        graphStore = new GraphStore();
        graphDimensionsCalculator = new GraphDimensionsCalculator(graphStore);

        graphDimensionsCalculator.setGraph(createGraph());
        graphDimensionsCalculator.setCanvasSize(mockCanvas().getWidth(), mockCanvas().getHeight());
    }


    @Test
    void testGraphChanged() {
        assertThat(graphDimensionsCalculator.getNodeCountProperty().get()).isEqualTo(3);

        final GfaFile gfaFileTwoNodes = mock(GfaFile.class);
        when(gfaFileTwoNodes.getGraph()).thenReturn(createTwoNodeGraph());
        graphStore.getGfaFileProperty().set(gfaFileTwoNodes);

        assertThat(graphDimensionsCalculator.getNodeCountProperty().get()).isEqualTo(4);
    }

    @Test
    void testComputeYPosition() {
        final Subgraph subgraph = new Subgraph();
        final Segment segment = new Segment(1, 0, 0);
        segment.setYPosition(30);
        subgraph.add(segment);

        graphDimensionsCalculator.calculate(subgraph);

        assertThat(graphDimensionsCalculator.computeYPosition(segment))
                .isEqualTo(CANVAS_HEIGHT / 2 / 10, Offset.offset(1.0));
    }

    @Test
    void testComputeMiddleYPosition() {
        final Subgraph subgraph = new Subgraph();
        final Segment segment = new Segment(1, 0, 0);
        segment.setYPosition(30);
        subgraph.add(segment);

        graphDimensionsCalculator.calculate(subgraph);

        assertThat(graphDimensionsCalculator.computeMiddleYPosition(segment))
                .isEqualTo(CANVAS_HEIGHT / 2 / 10, Offset.offset(1.0));
    }

    @Test
    void testComputeAndGetLaneHeight() {
        final Segment segment = new Segment(1, 0, 0);
        final Subgraph subgraph = new Subgraph();
        subgraph.add(segment);
        graphDimensionsCalculator.calculate(subgraph);

        assertThat(graphDimensionsCalculator.getLaneHeightProperty().get()).isEqualTo(300 / 10);
    }


    private Graph createGraph() {
        return new Graph(new int[][] {
                NodeBuilder.start()
                        .toArray(),
                NodeBuilder.start()
                        .withSequenceLength(500)
                        .withUnscaledXPosition(600)
                        .toArray(),
                NodeBuilder.start()
                        .withUnscaledXPosition(1000)
                        .toArray()
        }, null);
    }

    private Graph createTwoNodeGraph() {
        return new Graph(new int[][] {
                NodeBuilder.start()
                        .toArray(),
                NodeBuilder.start()
                        .withSequenceLength(500)
                        .withUnscaledXPosition(600)
                        .toArray(),
                NodeBuilder.start()
                        .withSequenceLength(300)
                        .withUnscaledXPosition(400)
                        .toArray(),
                NodeBuilder.start()
                        .withUnscaledXPosition(1000)
                        .toArray()
        }, null);
    }

    private Canvas mockCanvas() {
        final Canvas canvas = mock(Canvas.class);

        when(canvas.getWidth()).thenReturn(CANVAS_WIDTH);
        when(canvas.getHeight()).thenReturn(CANVAS_HEIGHT);

        return canvas;
    }
}
