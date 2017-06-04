package org.dnacronym.hygene.ui.node;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
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
        nodeDrawingToolkit = new NodeDrawingToolkit();

        graphicsContext = mock(GraphicsContext.class);
        nodeDrawingToolkit.setGraphicsContext(graphicsContext);
    }


    @Test
    void testHighlightNodeColor() {
        final Color strokeColor = Color.ALICEBLUE;

        nodeDrawingToolkit.highlightNode(0, 0, 0, 0, strokeColor);

        verify(graphicsContext).setStroke(strokeColor);
    }

    @Test
    void testHighlightNodeDraw() {
        nodeDrawingToolkit.highlightNode(10, 20, 30, 40, Color.BEIGE);

        verify(graphicsContext).strokeRoundRect(10 - 3 / 2.0, 20 - 3 / 2.0, 30 + 3, 40 + 3, 10, 10);
    }

    @Test
    void testDrawBookmarkFlagRed() {
        nodeDrawingToolkit.drawBookmarkFlag(0, 0, 0, 0, 10);

        verify(graphicsContext).setFill(Color.RED);
    }

    @Test
    void testDrawBookmarkFlagArrow() {
        nodeDrawingToolkit.drawBookmarkFlag(0, 0, 0, 0, 10);

        verify(graphicsContext).fillPolygon(any(double[].class), any(double[].class), eq(8));
    }

    @Test
    void testDrawingBookmarkBottomIdentifier() {
        nodeDrawingToolkit.drawBookmarkFlag(10, 0, 20, 0, 23);

        verify(graphicsContext).fillRect(10, 13, 20, 10);
    }

    @Test
    void testDrawText() {
        final String text = "test text";

        nodeDrawingToolkit.drawText(0, 0, 100, 5, 1, 1, text);

        verify(graphicsContext).fillText(eq(text), anyDouble(), anyDouble());
    }

    @Test
    void testDrawTextTrimmed() {
        final String text = "test text";

        nodeDrawingToolkit.drawText(0, 0, 10, 5, 1, 1, text);

        verify(graphicsContext).fillText(eq(""), anyDouble(), anyDouble());
    }
}
