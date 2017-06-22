package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link NodeDrawingToolkit}.
 */
final class NodeDrawingToolkitTest {
    private NodeDrawingToolkit nodeDrawingToolkit;
    private GraphicsContext graphicsContext;


    @BeforeEach
    void beforeEach() {
        nodeDrawingToolkit = new SegmentDrawingToolkit();

        graphicsContext = mock(GraphicsContext.class);
        nodeDrawingToolkit.setGraphicsContext(graphicsContext);
    }


    @Test
    void testNodeHeight() {
        nodeDrawingToolkit.setNodeHeight(10);
        nodeDrawingToolkit.draw(0, 0, 10, Color.BLACK);

        verify(graphicsContext).fillRoundRect(
                anyDouble(), anyDouble(), anyDouble(), eq(10.0), anyDouble(), anyDouble());
    }

    @Test
    void testNodeColorDraw() {
        nodeDrawingToolkit.draw(10, 20, 30, Color.ALICEBLUE);

        verify(graphicsContext).setFill(eq(Color.ALICEBLUE));
        verify(graphicsContext).fillRoundRect(
                eq(10d), eq(20d), eq(30d), eq(0d), eq(10d), eq(10d));
    }

    @Test
    void testNodePaths() {
        nodeDrawingToolkit.setNodeHeight(10);
        nodeDrawingToolkit.drawGenomes(20, 30, 40, Arrays.asList(Color.BLUE, Color.RED));

        verify(graphicsContext, atLeast(1)).setFill(eq(Color.BLUE));
        verify(graphicsContext, atLeast(1)).setFill(eq(Color.RED));

        verify(graphicsContext).fillRoundRect(
                eq(20d), eq(30d), eq(40d), eq(5d), eq(10d), eq(10d));
        verify(graphicsContext).fillRoundRect(
                eq(20d), eq(35d), eq(40d), eq(5d), eq(10d), eq(10d));
    }

    @Test
    void testNodeAnnotations() {
        nodeDrawingToolkit.setNodeHeight(20);
        nodeDrawingToolkit.setAnnotationHeight(5);
        nodeDrawingToolkit.drawAnnotations(50, 80, 40, Arrays.asList(Color.BLACK, Color.WHITE));

        verify(graphicsContext, atLeast(1)).setStroke(eq(Color.BLACK));
        verify(graphicsContext, atLeast(1)).setStroke(eq(Color.WHITE));

        verify(graphicsContext).setLineDashes(10);

        verify(graphicsContext).strokeLine(
                eq(50d), eq(80 + 20 + 5 + 5d / 2), eq(50 + 40d), eq(80 + 20 + 5 + 5d / 2));
        verify(graphicsContext).strokeLine(
                eq(50d), eq(80 + 20 + 5 + 5d / 2), eq(50 + 40d), eq(80 + 20 + 5 + 5d / 2));
    }

    @Test
    void testHighlightSelectedNodeDraw() {
        nodeDrawingToolkit.drawHighlight(10, 20, 30, HighlightType.SELECTED);

        verify(graphicsContext, atLeast(1)).setStroke(Color.rgb(0, 255, 46));

        verify(graphicsContext).strokeRoundRect(10 - 3 / 2.0, 20 - 3 / 2.0, 30 + 3, 3, 10d, 10d);
    }

    @Test
    void testHighlightBookmarkedNodeDraw() {
        nodeDrawingToolkit.drawHighlight(10, 20, 30, HighlightType.BOOKMARKED);

        verify(graphicsContext, atLeast(1)).setStroke(Color.RED);
        verify(graphicsContext).strokeRoundRect(10 - 3 / 2.0, 20 - 3 / 2.0, 30 + 3, 3, 10d, 10d);
    }

    @Test
    void testDrawText() {
        final String text = "test text";

        nodeDrawingToolkit.drawSequence(0, 0, 100, text);

        verify(graphicsContext).fillText(eq(text), anyDouble(), anyDouble());
    }

    @Test
    void testDrawTextTrimmed() {
        final String text = "test text";

        nodeDrawingToolkit.drawSequence(0, 0, 0, text);

        verify(graphicsContext).fillText(eq(""), anyDouble(), anyDouble());
    }
}
