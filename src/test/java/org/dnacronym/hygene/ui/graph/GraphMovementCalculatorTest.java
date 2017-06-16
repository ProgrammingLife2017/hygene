package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GraphMovementCalculator}.
 */
final class GraphMovementCalculatorTest {
    private GraphMovementCalculator graphMovementCalculator;
    private GraphDimensionsCalculator graphDimensionsCalculator;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty radiusProperty;


    @BeforeEach
    void beforeEach() {
        centerNodeIdProperty = new SimpleIntegerProperty(0);
        radiusProperty = new SimpleIntegerProperty(0);

        graphDimensionsCalculator = mock(GraphDimensionsCalculator.class);
        when(graphDimensionsCalculator.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphDimensionsCalculator.getRadiusProperty()).thenReturn(radiusProperty);
        when(graphDimensionsCalculator.getNodeCountProperty()).thenReturn(new SimpleIntegerProperty(65));

        graphMovementCalculator = new GraphMovementCalculator(graphDimensionsCalculator);
        graphMovementCalculator.getPanningSensitivityProperty().set(1);
        graphMovementCalculator.getZoomingSensitivityProperty().set(1);
    }


    @Test
    void testDraggingRight() {
        graphMovementCalculator.onMouseDragEntered(0);
        graphMovementCalculator.onMouseDragged(-100);

        assertThat(centerNodeIdProperty.get()).isEqualTo(100);
    }

    @Test
    void testDraggingLeft() {
        graphMovementCalculator.onMouseDragEntered(0);
        graphMovementCalculator.onMouseDragged(100);

        assertThat(centerNodeIdProperty.get()).isEqualTo(-100);
    }
}
