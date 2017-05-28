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
import javafx.scene.text.Text;


/**
 * Visualizer which shows a nice representation of the selected node.
 */
public final class SequenceVisualizer {
    private static final double SQUARE_WIDTH = 30;
    private static final double SQUARE_HEIGHT = 40;
    private static final double HORIZONTAL_GAP = 10;
    private static final double VERTICAL_GAP = 5;
    private static final double ARC_SIZE = 10;

    private static final double TEXT_HEIGHT_PORTION_OFFSET = 0.75;

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
            final int newOffset = newValue != null ? Math.min(offsetProperty.get(), newValue.length()) : 0;
            if (newOffset == offsetProperty.get()) {
                draw(); // ensure draw is called if offset property remains unchanged
            }

            setOffset(newOffset);
        });
        offsetProperty.addListener((observable, oldValue, newValue) -> draw());

        visibleProperty = new SimpleBooleanProperty();
        visibleProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
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

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> draw());
    }

    /**
     * Returns the {@link BooleanProperty} which decides if the {@link javafx.scene.layout.Pane} is visible.
     *
     * @return {@link BooleanProperty} which decides if the {@link javafx.scene.layout.Pane} if visible
     */
    BooleanProperty getVisibleProperty() {
        return visibleProperty;
    }

    /**
     * Returns the {@link StringProperty} which decides the sequence.
     *
     * @return {@link StringProperty} which decides the sequence
     */
    StringProperty getSequenceProperty() {
        return sequenceProperty;
    }

    /**
     * Returns the {@link ReadOnlyIntegerProperty} which decides the offset.
     *
     * @return {@link ReadOnlyIntegerProperty} which decides the sequence
     */
    ReadOnlyIntegerProperty getOffsetProperty() {
        return offsetProperty;
    }

    /**
     * Draw square on canvas with given message.
     *
     * @param message    text to display in middle of square
     * @param x          upper left x of square
     * @param y          upper left y of square
     * @param squareFill {@link Color} fill of background of square
     * @param textFill   {@link Color} fill of text in square
     */
    private void drawSquare(final String message, final double x, final double y,
                            final Color squareFill, final Color textFill) {
        graphicsContext.setStroke(squareFill);
        graphicsContext.strokeRoundRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT, ARC_SIZE, ARC_SIZE);

        final Text messageText = new Text(message);
        messageText.setFont(graphicsContext.getFont());
        final double textWidth = messageText.getBoundsInLocal().getWidth();

        graphicsContext.setStroke(textFill);
        graphicsContext.fillText(
                message,
                x + SQUARE_WIDTH / 2 - textWidth / 2,
                y + SQUARE_HEIGHT * TEXT_HEIGHT_PORTION_OFFSET);
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
     * Upper bound set at sequence length - 1. Draws the sequence again.
     *
     * @param amount amount to increment the offset by
     */
    void incrementOffset(final int amount) {
        offsetProperty.set(Math.min(offsetProperty.get() + amount, sequenceProperty.get().length() - 1));
        draw();
    }

    /**
     * Change offset to new value.
     * <p>
     * Upper bound set at sequence length - 1. Lower bound is set at 0. If sequence is null, offset set to 0. Afterwards
     * draws again.
     *
     * @param offset new offset amount
     */
    void setOffset(final int offset) {
        if (sequenceProperty.get() == null) {
            offsetProperty.set(0);
            return;
        }

        offsetProperty.set(Math.max(0, Math.min(offset, sequenceProperty.get().length() - 1)));
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
    private void draw() {
        clear();
        if (sequenceProperty.get() == null) {
            return;
        }

        for (int i = offsetProperty.get(); i < sequenceProperty.get().length(); i++) {
            final double topRightX = HORIZONTAL_GAP + (i - offsetProperty.get()) * (SQUARE_WIDTH + HORIZONTAL_GAP);

            if (topRightX + SQUARE_WIDTH > canvas.getWidth()) {
                break;
            }

            String base = String.valueOf(sequenceProperty.get().charAt(i));

            drawSquare(base, topRightX, VERTICAL_GAP, Color.BLUE, Color.BLACK);
            drawSquare(String.valueOf(i), topRightX, VERTICAL_GAP * 2 + SQUARE_HEIGHT, Color.DARKBLUE, Color.BLACK);
        }
    }
}
