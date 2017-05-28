package org.dnacronym.hygene.ui.node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * Visualizer which shows a nice representation of the selected node.
 */
public final class SequenceVisualizer {
    private static final double BASE_WIDTH = 30;
    private static final double BASE_HEIGHT = 40;
    private static final double HORIZONTAL_GAP = 10;
    private static final double VERTICAL_GAP = 5;
    private static final double ARC_WIDTH = 10;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private final StringProperty sequenceProperty;
    private final IntegerProperty offsetProperty;
    private final BooleanProperty visibleProperty;


    /**
     * Create instance of {@link SequenceVisualizer}.
     */
    public SequenceVisualizer() {
        sequenceProperty = new SimpleStringProperty();
        offsetProperty = new SimpleIntegerProperty();

        sequenceProperty.addListener((observable, oldValue, newValue) -> {
            offsetProperty.set(Math.min(newValue.length() - 1, offsetProperty.get()));
            draw();
        });
        offsetProperty.addListener((observable, oldValue, newValue) -> draw());

        visibleProperty = new SimpleBooleanProperty();
        visibleProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue){
                draw();
            }
        });
    }


    /**
     * Sets the {@link Canvas} for use by the visualizer.
     *
     * @param canvas {@link Canvas} for use by the visualizer
     */
    public void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();
    }


    /**
     * Returns the {@link BooleanProperty} which decides if the {@link javafx.scene.layout.Pane} is visible.
     *
     * @return {@link BooleanProperty} which decides if the {@link javafx.scene.layout.Pane} if visible
     */
    public BooleanProperty getVisibleProperty() {
        return visibleProperty;
    }

    /**
     * Returns the {@link StringProperty} which decides the sequence.
     *
     * @return {@link StringProperty} which decides the sequence
     */
    public StringProperty getSequenceProperty() {
        return sequenceProperty;
    }

    /**
     * Returns the {@link ReadOnlyIntegerProperty} which decides the offset.
     *
     * @return {@link ReadOnlyIntegerProperty} which decides the sequence
     */
    public ReadOnlyIntegerProperty getOffsetProperty() {
        return offsetProperty;
    }

    /**
     * Draw square on canvas with given message.
     *
     * @param message text to display in middle of square
     * @param x       upper left x of square
     * @param y       upper left y of square
     * @param width   width of square
     * @param height  height of square
     * @param arcSize arc size of square
     */
    private void drawSquare(final String message, final double x, final double y,
                            final double width, final double height, final double arcSize,
                            final Color squareFill, final Color textFill) {
        graphicsContext.setFill(squareFill);
        graphicsContext.fillRoundRect(x, y, width, height, arcSize, arcSize);

        graphicsContext.setFill(textFill);
        graphicsContext.fillText(message, x + width * 0.25, y + height * 0.75);
    }

    /**
     * Decrement the current offset.
     * <p>
     * Lower bound set to 0. Draws the sequence again.
     *
     * @param amount amount to decrement by
     */
    void decrementOffset(final int amount) {
        offsetProperty.set(Math.max(offsetProperty.get() - amount, 0));
        draw();
    }

    /**
     * Increment the current offset.
     * <p>
     * Upper bound set at sequenceProperty length - 1. Draws the sequence again.
     *
     * @param amount amount to increment the offset by
     */
    void incrementOffset(final int amount) {
        offsetProperty.set(Math.min(offsetProperty.get() + amount, sequenceProperty.get().length() - 1));
        draw();
    }

    /**
     * Clear the canvas.
     */
    private void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Visualize the current sequenceProperty with the current offset.
     * <p>
     * If sequence is null then area is only cleared.
     */
    void draw() {
        clear();
        if (sequenceProperty.get() == null) {
            return;
        }

        for (int i = offsetProperty.get(); i < sequenceProperty.get().length(); i++) {
            final double topRightX = HORIZONTAL_GAP + (i - offsetProperty.get()) * (BASE_WIDTH + HORIZONTAL_GAP);

            if (topRightX + BASE_WIDTH > canvas.getWidth()) {
                break;
            }

            String base = String.valueOf(sequenceProperty.get().charAt(i));

            drawSquare(base,
                    topRightX,
                    VERTICAL_GAP,
                    BASE_WIDTH,
                    BASE_HEIGHT,
                    ARC_WIDTH,
                    Color.BLUE,
                    Color.BLACK);

            drawSquare(String.valueOf(i),
                    topRightX,
                    VERTICAL_GAP * 2 + BASE_HEIGHT,
                    BASE_WIDTH,
                    BASE_HEIGHT,
                    ARC_WIDTH,
                    Color.DARKBLUE,
                    Color.BLACK);
        }
    }
}
