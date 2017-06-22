package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link SegmentDrawingToolkit}.
 */
final class SnpDrawingToolkitTest {
    private NodeDrawingToolkit snpDrawingToolkit;

    @Mock
    private GraphicsContext graphicsContext;
    @Captor
    private ArgumentCaptor<double[]> xArray;
    @Captor
    private ArgumentCaptor<double[]> yArray;


    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);

        snpDrawingToolkit = new SnpDrawingToolkit();
        snpDrawingToolkit.setGraphicsContext(graphicsContext);
    }


    @Test
    void testNodeHeight() {
        snpDrawingToolkit.setNodeHeight(10);
        snpDrawingToolkit.draw(0, 0, 10, Color.BLACK);

        verify(graphicsContext).fillPolygon(xArray.capture(), yArray.capture(), eq(4));
        assertThat(xArray.getValue()).containsExactly(0, 5, 10, 5);
        assertThat(yArray.getValue()).containsExactly(5, -10, 5, 20);
    }
}
