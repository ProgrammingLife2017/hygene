package org.dnacronym.hygene.ui.node;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SequenceVisualizer}.
 */
final class SequenceVisualizerTest {
    private SequenceVisualizer sequenceVisualizer;
    private GraphicsContext graphicsContext;
    private DoubleProperty canvasWidthProperty;


    @BeforeEach
    void beforeEach() {
        final Canvas canvas = mock(Canvas.class);
        graphicsContext = mock(GraphicsContext.class);
        canvasWidthProperty = mock(DoubleProperty.class);
        when(canvas.getGraphicsContext2D()).thenReturn(graphicsContext);
        when(canvas.widthProperty()).thenReturn(canvasWidthProperty);

        sequenceVisualizer = new SequenceVisualizer();
        sequenceVisualizer.setCanvas(canvas);
    }


    /**
     * Set canvas.
     */

    @Test
    @SuppressWarnings("unchecked")
    void testSetCanvasListener() {
        verify(canvasWidthProperty).addListener(any(ChangeListener.class));
    }

    /**
     * Offset tests.
     */

    @Test
    void testInitialOffset() {
        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(0);
    }

    @Test
    void testIncrementOffset() {
        sequenceVisualizer.getSequenceProperty().set("aab");

        sequenceVisualizer.incrementOffset(1);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(1);
    }

    @Test
    void testOffsetUpperBound() {
        sequenceVisualizer.getSequenceProperty().set("aab");

        sequenceVisualizer.incrementOffset(100);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(2);
    }

    @Test
    void testOffsetLowerBound() {
        sequenceVisualizer.getSequenceProperty().set("aab");

        sequenceVisualizer.decrementOffset(100);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(0);
    }

    @Test
    void testSetOffset() {
        sequenceVisualizer.getSequenceProperty().set("aab");

        sequenceVisualizer.setOffset(2);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(2);
    }

    @Test
    void testSetOffsetLowerBound() {
        sequenceVisualizer.getSequenceProperty().set("aab");

        sequenceVisualizer.setOffset(-10);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(0);
    }

    @Test
    void testSetOffsetUpperBound() {
        sequenceVisualizer.getSequenceProperty().set("aab");

        sequenceVisualizer.setOffset(1000);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(2);
    }

    /**
     * Change tests.
     */

    @Test
    void testDrawSequenceChange() {
        sequenceVisualizer.getSequenceProperty().set("asdf");

        verify(graphicsContext).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testDrawOffsetChangeSequenceNull() {
        sequenceVisualizer.setOffset(2);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(0);
        verify(graphicsContext, never()).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testDrawOffsetChangeSequenceNotNull() {
        sequenceVisualizer.getSequenceProperty().set("asdf");
        sequenceVisualizer.setOffset(2);

        assertThat(sequenceVisualizer.getOffsetProperty().get()).isEqualTo(2);
        verify(graphicsContext, times(2)).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testVisibleChange() {
        sequenceVisualizer.getVisibleProperty().set(true);

        verify(graphicsContext).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
}
