package org.dnacronym.hygene.ui.util;

import javafx.beans.property.IntegerProperty;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GraphMovementCalculator}s.
 */
// TODO rewrite GraphMovementCalculator and test this final version
public final class GraphMovementCalculatorTest {
    private GraphMovementCalculator graphMovementCalculator;
    private GraphVisualizer graphVisualizer;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty nodeCountProperty;


    @BeforeEach
    void beforeEach() {
        graphVisualizer = mock(GraphVisualizer.class);
        centerNodeIdProperty = mock(IntegerProperty.class);
        nodeCountProperty = mock(IntegerProperty.class);

        when(centerNodeIdProperty.get()).thenReturn(10);
        when(nodeCountProperty.get()).thenReturn(20);

        when(graphVisualizer.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphVisualizer.getNodeCountProperty()).thenReturn(nodeCountProperty);

        graphMovementCalculator = new GraphMovementCalculator(graphVisualizer);
    }


    @Test
    void testDragging() {
        graphMovementCalculator.onMousePressed(10);
        graphMovementCalculator.onMouseDragged(100);

        verify(centerNodeIdProperty).set(any(Integer.class));
    }
}
