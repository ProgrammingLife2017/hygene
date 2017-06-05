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
    void testHighlightNodeDraw() {
        nodeDrawingToolkit
                .setDimensions(10, 20, 30)
                .setHighlighted(true)
                .drawNode();

        verify(graphicsContext).setFill(Color.RED);
        verify(graphicsContext).strokeRoundRect(10 - 3 / 2.0, 20 - 3 / 2.0, 30 + 3, 40 + 3, 10, 10);
    }

    @Test
    void testDrawBookmarkFlagArrow() {
        nodeDrawingToolkit
                .setDimensions(10, 10, 10)
                .setBookmarked(true)
                .drawNode();

        verify(graphicsContext).fillPolygon(any(double[].class), any(double[].class), eq(8));
    }

    @Test
    void testDrawText() {
        final String text = "test text";

        nodeDrawingToolkit
                .setDimensions(10, 10, 100)
                .setSequence(text)
                .setDrawSequence(true)
                .drawNode();

        verify(graphicsContext).fillText(eq(text), anyDouble(), anyDouble());
    }

    @Test
    void testDrawTextTrimmed() {
        final String text = "test text";

        nodeDrawingToolkit
                .setDimensions(10, 10, 1)
                .setSequence(text)
                .setDrawSequence(true)
                .drawNode();

        verify(graphicsContext).fillText(eq(""), anyDouble(), anyDouble());
    }
}
