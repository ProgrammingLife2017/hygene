package org.dnacronym.hygene.ui.node;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        nodeDrawingToolkit = new NodeDrawingToolkit();

        graphicsContext = mock(GraphicsContext.class);
        nodeDrawingToolkit.setGraphicsContext(graphicsContext);
    }


    @Test
    void testNodeHeight() {
        nodeDrawingToolkit.setNodeHeight(10);
        nodeDrawingToolkit.fillNode(0, 0, 10, Color.BLACK);

        verify(graphicsContext).fillRoundRect(
                anyDouble(), anyDouble(), anyDouble(), eq(10.0), anyDouble(), anyDouble());
    }

    @Test
    void testNodeColorDraw() {
        nodeDrawingToolkit.fillNode(10, 20, 30, Color.ALICEBLUE);

        verify(graphicsContext).setFill(Color.ALICEBLUE);
        verify(graphicsContext).fillRoundRect(10, 20, 30, 0, 10, 10);
    }

    @Test
    void testHighlightSelectedNodeDraw() {
        nodeDrawingToolkit.drawNodeHighlight(10, 20, 30, NodeDrawingToolkit.HighlightType.SELECTED);

        verify(graphicsContext, atLeast(1)).setStroke(Color.rgb(0, 255, 46));
        verify(graphicsContext).strokeRoundRect(10 - 3 / 2.0, 20 - 3 / 2.0, 30 + 3, 3, 10, 10);
    }

    @Test
    void testHighlightBookmarkedNodeDraw() {
        nodeDrawingToolkit.drawNodeHighlight(10, 20, 30, NodeDrawingToolkit.HighlightType.BOOKMARKED);

        verify(graphicsContext, atLeast(1)).setStroke(Color.RED);
        verify(graphicsContext).strokeRoundRect(10 - 3 / 2.0, 20 - 3 / 2.0, 30 + 3, 3, 10, 10);
    }

    @Test
    void testDrawText() {
        final String text = "test text";

        nodeDrawingToolkit.drawNodeSequence(0, 0, 100, text);

        verify(graphicsContext).fillText(eq(text), anyDouble(), anyDouble());
    }

    @Test
    void testDrawTextTrimmed() {
        final String text = "test text";

        nodeDrawingToolkit.drawNodeSequence(0, 0, 0, text);

        verify(graphicsContext).fillText(eq(""), anyDouble(), anyDouble());
    }
}
