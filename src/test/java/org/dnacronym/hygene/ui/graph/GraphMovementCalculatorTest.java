package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
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
    private LongProperty viewPointProperty;
    private IntegerProperty radiusProperty;


    @BeforeEach
    void beforeEach() {
        viewPointProperty = new SimpleLongProperty(0);
        radiusProperty = new SimpleIntegerProperty(0);

        graphDimensionsCalculator = mock(GraphDimensionsCalculator.class);
        when(graphDimensionsCalculator.getViewPointProperty()).thenReturn(viewPointProperty);
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

        assertThat(viewPointProperty.get()).isEqualTo(10000);
    }

    @Test
    void testDraggingLeft() {
        graphMovementCalculator.onMouseDragEntered(0);
        graphMovementCalculator.onMouseDragged(100);

        assertThat(viewPointProperty.get()).isEqualTo(-10000);
    }
}
