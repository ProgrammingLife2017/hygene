package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GraphMovementCalculator}.
 */
final class GraphMovementCalculatorTest {
    private GraphMovementCalculator graphMovementCalculator;
    private GraphVisualizer graphVisualizer;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty nodeCountProperty;


    @BeforeEach
    void beforeEach() {
        graphVisualizer = mock(GraphVisualizer.class);
        centerNodeIdProperty = mock(IntegerProperty.class);
        nodeCountProperty = mock(IntegerProperty.class);

        when(centerNodeIdProperty.get()).thenReturn(15);
        when(nodeCountProperty.get()).thenReturn(2000);

        when(graphVisualizer.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphVisualizer.getNodeCountProperty()).thenReturn(nodeCountProperty);

        graphMovementCalculator = new GraphMovementCalculator(graphVisualizer);
        graphMovementCalculator.getPanningSensitivityProperty().set(1);
    }


    @Test
    void testDragging() {
        graphMovementCalculator.onMousePressed(0);
        graphMovementCalculator.onMouseDragged(-100);

        verify(centerNodeIdProperty).set(15 + 100);
    }

    @Test
    void testLowerBound() {
        graphMovementCalculator.onMousePressed(0);
        graphMovementCalculator.onMouseDragged(100);

        verify(centerNodeIdProperty).set(0);
    }

    @Test
    void testUpperBound() {
        when(nodeCountProperty.get()).thenReturn(20);
        graphMovementCalculator.onMousePressed(0);
        graphMovementCalculator.onMouseDragged(-100);

        verify(centerNodeIdProperty).set(19);
    }
}
