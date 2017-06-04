package org.dnacronym.hygene.ui.node;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * Toolkit used to draw nodes attributes.
 * <p>
 * This class deals with the drawing of nodes. This includes highlighting a node, drawing text in a node and drawing
 * a bookmark identifier of a node.
 */
public final class NodeDrawingToolkit {
    private static final int NODE_OUTLINE_WIDTH = 3;
    private static final int BOOKMARK_IDENTIFIER_HEIGHT = 10;
    private static final int ARC_SIZE = 10;

    private GraphicsContext graphicsContext;


    /**
     * Sets the {@link GraphicsContext} used for drawing.
     *
     * @param graphicsContext the {@link GraphicsContext} to set
     */
    public void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * Draw a highlight band around a given node of width {@value NODE_OUTLINE_WIDTH}.
     *
     * @param rectX          the upper left x position of the node
     * @param rectY          the upper left y position of the node
     * @param rectWidth      the width of the node
     * @param rectHeight     the height of the node
     * @param highlightColor the color of the highlight
     */
    public void highlightNode(final double rectX, final double rectY, final double rectWidth, final double rectHeight,
                              final Color highlightColor) {
        graphicsContext.setStroke(highlightColor);
        graphicsContext.setLineWidth(NODE_OUTLINE_WIDTH);
        graphicsContext.strokeRoundRect(rectX - NODE_OUTLINE_WIDTH / 2.0, rectY - NODE_OUTLINE_WIDTH / 2.0,
                rectWidth + NODE_OUTLINE_WIDTH, rectHeight + NODE_OUTLINE_WIDTH, ARC_SIZE, ARC_SIZE);
    }

    /**
     * Draw a bookmark flag and bookmark indicator for a node.
     *
     * @param rectX             the left x position of the node to bookmark
     * @param rectY             the upper y position of the node to bookmark
     * @param rectWidth         the width of the node to bookmark
     * @param arrowHeight       the height the arrow should be
     * @param bottomYIdentifier the bottom y position of the identifier at the bottom of the graph
     */
    @SuppressWarnings("MagicNumber") // Using constants here does not make the method more readable.
    public void drawBookmarkFlag(final double rectX, final double rectY, final double rectWidth,
                                 final double arrowHeight, final double bottomYIdentifier) {
        graphicsContext.setFill(Color.RED);

        final double arrowPortionHeight = arrowHeight / 4;
        final double arrowHorizontalCenter = rectX + rectWidth / 2;

        graphicsContext.fillPolygon(
                new double[] {arrowHorizontalCenter,
                        arrowHorizontalCenter - rectWidth / 4, arrowHorizontalCenter - rectWidth / 8,
                        arrowHorizontalCenter - rectWidth / 8, arrowHorizontalCenter + rectWidth / 8,
                        arrowHorizontalCenter + rectWidth / 8, arrowHorizontalCenter + rectWidth / 4,
                        arrowHorizontalCenter},
                new double[] {rectY,
                        rectY - arrowPortionHeight, rectY - arrowPortionHeight,
                        rectY - arrowHeight, rectY - arrowHeight,
                        rectY - arrowPortionHeight, rectY - arrowPortionHeight,
                        rectY},
                8);

        graphicsContext.fillRect(rectX, bottomYIdentifier - BOOKMARK_IDENTIFIER_HEIGHT,
                rectWidth, BOOKMARK_IDENTIFIER_HEIGHT);
    }

    /**
     * Draw a sequence as black text inside a node.
     *
     * @param rectX      the upper left x position of the node to bookmark
     * @param rectY      the upper left y position of the node to bookmark
     * @param rectWidth  the width of the node
     * @param rectHeight the height of the node
     * @param charWidth  the width of a single character
     * @param charHeight the height of a single character
     * @param sequence   sequence to draw in node
     */
    public void drawText(final double rectX, final double rectY, final double rectWidth, final double rectHeight,
                         final double charWidth, final double charHeight, final String sequence) {
        graphicsContext.setFill(Color.BLACK);
        final int charCount = (int) Math.max((rectWidth - ARC_SIZE) / charWidth, 0);

        final double fontX = rectX + 0.5 * (rectWidth + (ARC_SIZE / 4.0) - charCount * charWidth);
        final double fontY = rectY + 0.5 * rectHeight + 0.25 * charHeight;

        graphicsContext.fillText(sequence.substring(0, Math.min(sequence.length(), charCount)), fontX, fontY);
    }
}
