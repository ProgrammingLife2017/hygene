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
import org.dnacronym.hygene.ui.graph.RTree;


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
    private final IntegerProperty onScreenBasesProperty;

    private RTree rTree;
    private final IntegerProperty hoveredBaseIdProperty;


    /**
     * Create instance of {@link SequenceVisualizer}.
     */
    public SequenceVisualizer() {
        sequenceProperty = new SimpleStringProperty();
        offsetProperty = new SimpleIntegerProperty();
        onScreenBasesProperty = new SimpleIntegerProperty();
        hoveredBaseIdProperty = new SimpleIntegerProperty(-1);

        sequenceProperty.addListener((observable, oldValue, newValue) -> {
            if (offsetProperty.get() == 0) {
                draw(); // force redraw if offset remains unchanged.
            }
            offsetProperty.set(0);
        });
        offsetProperty.addListener((observable, oldValue, newValue) -> draw());
        hoveredBaseIdProperty.addListener((observable, oldValue, newValue) -> draw());

        visibleProperty = new SimpleBooleanProperty();
        visibleProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                draw();
            }
        });
    }

    /**
     * Sets the {@link Canvas} for use by the visualizer.
     * <p>
     * When the user clicks on the canvas, if the x and y coordinates intersect with a base then the offset is updated
     * to the given base. If the mouse is hovered of a base the hovered base id is updated.
     *
     * @param canvas {@link Canvas} for use by the visualizer
     */
    public void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> draw());
        canvas.setOnMouseClicked(event -> {
            if (rTree == null) {
                return;
            }
            rTree.find(event.getX(), event.getY(), offsetProperty::set);
        });
        canvas.setOnMouseMoved(event -> {
            if (rTree == null) {
                return;
            }
            rTree.find(event.getX(), event.getY(), hoveredBaseIdProperty::set);
        });
        canvas.setOnMouseExited(event -> hoveredBaseIdProperty.set(-1));
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
    public void setOffset(final int offset) {
        if (sequenceProperty.get() == null) {
            offsetProperty.set(0);
            return;
        }

        offsetProperty.set(Math.max(0, Math.min(offset, sequenceProperty.get().length() - 1)));
    }

    /**
     * Clears the canvas and reset the {@link RTree).
     */
    private void clear() {
        rTree = new RTree();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Visualizes the current sequenceProperty with the current offset.
     * <p>
     * If sequence is null then area is only cleared. If a base offset is equal to the hovered on base, then its color
     * is red instead of blue.
     */
    private void draw() {
        clear();
        if (sequenceProperty.get() == null) {
            return;
        }

        int onscreen = 0;
        for (int i = offsetProperty.get(); i < sequenceProperty.get().length(); i++, onscreen++) {
            final double topRightX = HORIZONTAL_GAP + (i - offsetProperty.get()) * (SQUARE_WIDTH + HORIZONTAL_GAP);

            if (topRightX + SQUARE_WIDTH > canvas.getWidth()) {
                onScreenBasesProperty.set(onscreen);
                break;
            }

            final String base = String.valueOf(sequenceProperty.get().charAt(i));

            drawSquare(base, topRightX, VERTICAL_GAP,
                    i == hoveredBaseIdProperty.get() ? Color.RED : Color.BLUE, Color.BLACK);
            drawSquare(String.valueOf(i), topRightX, VERTICAL_GAP * 2 + SQUARE_HEIGHT,
                    i == hoveredBaseIdProperty.get() ? Color.DARKRED : Color.DARKBLUE, Color.BLACK);

            rTree.addNode(i, topRightX, VERTICAL_GAP, SQUARE_WIDTH, SQUARE_HEIGHT * 2 + VERTICAL_GAP);
        }
    }

    /**
     * Returns the {@link BooleanProperty} which decides if the {@link javafx.scene.layout.Pane} is visible.
     *
     * @return the {@link BooleanProperty} which decides if the {@link javafx.scene.layout.Pane} if visible
     */
    public BooleanProperty getVisibleProperty() {
        return visibleProperty;
    }

    /**
     * Returns the {@link StringProperty} which decides the sequence.
     *
     * @return the {@link StringProperty} which decides the sequence
     */
    public StringProperty getSequenceProperty() {
        return sequenceProperty;
    }

    /**
     * Returns the {@link ReadOnlyIntegerProperty} which decides the offset.
     *
     * @return the {@link ReadOnlyIntegerProperty} which decides the offset
     */
    public ReadOnlyIntegerProperty getOffsetProperty() {
        return offsetProperty;
    }

    /**
     * Returns the {@link ReadOnlyIntegerProperty} which describes the amount of bases drawn on the canvas.
     *
     * @return the {@link ReadOnlyIntegerProperty} which describes the amount of bases drawn on the canvas
     */
    public ReadOnlyIntegerProperty getOnScreenBasesCountProperty() {
        return onScreenBasesProperty;
    }
}
