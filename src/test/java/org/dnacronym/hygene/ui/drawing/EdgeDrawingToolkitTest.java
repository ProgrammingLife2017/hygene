package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Unit tests of {@link EdgeDrawingToolkit}.
 */
final class EdgeDrawingToolkitTest {
    private EdgeDrawingToolkit drawingToolkit;
    private GraphicsContext graphicsContext;


    @BeforeEach
    void beforeEach() {
        drawingToolkit = new EdgeDrawingToolkit();
        graphicsContext = mock(GraphicsContext.class);
        drawingToolkit.setGraphicsContext(graphicsContext);
    }


    @Test
    void testDrawEdge() {
        drawingToolkit.drawEdge(10, 20, 30, 40, 50, Color.BEIGE);

        verify(graphicsContext).setLineWidth(eq(50d));
        verify(graphicsContext).setStroke(eq(Color.BEIGE));
        verify(graphicsContext).strokeLine(eq(10d), eq(20d), eq(30d), eq(40d));
    }

    @Test
    void testDrawEdgeTwoPaths() {
        drawingToolkit.drawEdgePaths(10, 20, 30, 40, 50, Arrays.asList(Color.BEIGE, Color.BLUE));

        verify(graphicsContext).setLineWidth(eq(25d));

        verify(graphicsContext, atLeast(1)).setStroke(Color.BEIGE);
        verify(graphicsContext, atLeast(1)).setStroke(Color.BLUE);

        verify(graphicsContext).strokeLine(eq(10d), eq(20d), eq(30d), eq(40d));
        verify(graphicsContext).strokeLine(eq(10d), eq(45d), eq(30d), eq(65d));
    }

    @Test
    void testDrawAnnotations() {
        drawingToolkit.drawEdgeAnnotations(50, 60, 70, 80, 5, Arrays.asList(Color.AZURE, Color.BLUE));

        verify(graphicsContext).setLineWidth(eq(2d));

        verify(graphicsContext, atLeast(1)).setStroke(Color.AZURE);
        verify(graphicsContext, atLeast(1)).setStroke(Color.BLUE);

        verify(graphicsContext).strokeLine(eq(50d), eq(60d + 5 + 2 + 1), eq(70d), eq(80d + 5 + 2 + 1));
        verify(graphicsContext).strokeLine(eq(50d), eq(60d + 5 + 2 + 1), eq(70d), eq(80d + 5 + 2 + 1));
    }

    @Test
    void testSetAnnotationHeight() {
        drawingToolkit.setAnnotationHeight(5);
        drawingToolkit.drawEdgeAnnotations(40, 50, 60, 70, 10, Arrays.asList(Color.AZURE, Color.BLUE));

        verify(graphicsContext).setLineWidth(eq(5d));

        verify(graphicsContext, atLeast(1)).setStroke(Color.AZURE);
        verify(graphicsContext, atLeast(1)).setStroke(Color.BLUE);

        verify(graphicsContext).strokeLine(
                eq(40d), eq(50d + 10 + 5 + 5d / 2), eq(60d), eq(70d + 10 + 5 + 5d / 2));
        verify(graphicsContext).strokeLine(
                eq(40d), eq(50d + 10 + 5 + 5 + 5d / 2), eq(60d), eq(70d + 10 + 5 + 5 + 5d / 2));
    }
}
