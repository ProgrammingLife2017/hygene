package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.GraphicsContext;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;


/**
 * Unit tests of {@link GraphAnnotationVisualizer}.
 */
final class GraphAnnotationVisualizerTest {
    private GraphAnnotationVisualizer graphAnnotationVisualizer;


    @BeforeEach
    void beforeEach() {
        final GraphDimensionsCalculator graphDimensionsCalculator = mock(GraphDimensionsCalculator.class);
        final GraphicsContext graphicsContext = mock(GraphicsContext.class);

        graphAnnotationVisualizer = new GraphAnnotationVisualizer(graphDimensionsCalculator);
        graphAnnotationVisualizer.setGraphicsContext(graphicsContext);
    }


    @Test
    void testNotEnoughGenomePoints() {
        final List<GenomePoint> genomePointList = new ArrayList<>(Collections.singleton(mock(GenomePoint.class)));

        graphAnnotationVisualizer.draw("s", genomePointList, new ArrayList<>());
    }
}
