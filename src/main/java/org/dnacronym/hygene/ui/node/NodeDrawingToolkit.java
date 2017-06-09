package org.dnacronym.hygene.ui.node;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


/**
 * Toolkit used to draw nodes attributes.
 * <p>
 * This class deals with the drawing of nodes. This includes highlighting a node, drawing text in a node and drawing
 * a bookmark identifier of a node.
 */
public final class NodeDrawingToolkit {
    private static final int BOOKMARK_INDICATOR_HEIGHT = 10;
    private static final int NODE_OUTLINE_WIDTH = 3;
    private static final int ARC_SIZE = 10;
    /**
     * Font used inside the nodes, this should always be a monospace font.
     */
    private static final String DEFAULT_NODE_FONT = "Consolas";
    /**
     * Scalar for the size of the node text font as fraction of the node's height.
     */
    private static final double DEFAULT_NODE_FONT_HEIGHT_SCALAR = 0.7;

    private GraphicsContext graphicsContext;

    private double canvasHeight;
    private double nodeHeight;
    private double charWidth;
    private double charHeight;
    private Font nodeFont;


    /**
     * Sets the {@link GraphicsContext} used for drawing.
     *
     * @param graphicsContext the {@link GraphicsContext} to set
     */
    public void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * Sets the node height used for drawing.
     * <p>
     * Also updates the font size used for drawing sequences within nodes.
     *
     * @param nodeHeight the node height
     */
    public void setNodeHeight(final double nodeHeight) {
        this.nodeHeight = nodeHeight;

        final Text text = new Text("X");
        text.setFont(new Font(DEFAULT_NODE_FONT, 1));

        final double font1PHeight = text.getLayoutBounds().getHeight();
        final double fontSize = DEFAULT_NODE_FONT_HEIGHT_SCALAR * nodeHeight / font1PHeight;
        this.nodeFont = new Font(DEFAULT_NODE_FONT, fontSize);
        text.setFont(nodeFont);

        this.charWidth = text.getLayoutBounds().getWidth();
        this.charHeight = text.getLayoutBounds().getHeight();
    }

    /**
     * Sets the canvas height.
     *
     * @param canvasHeight the canvas height
     */
    public void setCanvasHeight(final double canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    /**
     * Fills a round rectangle based on the node position and width, with the set {@link Color} fill.
     *
     * @param nodeX     the top left x position of the node
     * @param nodeY     the top left y position of the node
     * @param nodeWidth the width of the node
     * @param fill      the {@link Color} to fill the node with
     */
    public void fillNode(final double nodeX, final double nodeY, final double nodeWidth, final Color fill) {
        graphicsContext.setFill(fill);
        graphicsContext.fillRoundRect(nodeX, nodeY, nodeWidth, nodeHeight, ARC_SIZE, ARC_SIZE);
    }

    /**
     * Draw a highlight band around a given node of width {@value NODE_OUTLINE_WIDTH}.
     *
     * @param nodeX         the top left x position of the node
     * @param nodeY         the top left y position of the node
     * @param nodeWidth     the width of the node
     * @param highlightType the type of highlight
     */
    public void drawNodeHighlight(final double nodeX, final double nodeY, final double nodeWidth,
                                  final HighlightType highlightType) {
        graphicsContext.setStroke(highlightType.color);
        graphicsContext.setLineWidth(NODE_OUTLINE_WIDTH);
        graphicsContext.strokeRoundRect(
                nodeX - NODE_OUTLINE_WIDTH / 2.0,
                nodeY - NODE_OUTLINE_WIDTH / 2.0,
                nodeWidth + NODE_OUTLINE_WIDTH,
                nodeHeight + NODE_OUTLINE_WIDTH,
                ARC_SIZE, ARC_SIZE);

        if (highlightType == HighlightType.BOOKMARKED) {
            drawBookmarkIndicator(nodeX, nodeWidth);
        }
    }

    /**
     * Draws a bookmark indicator at the bottom of the graph.
     *
     * @param nodeX     the left x position of the node to bookmark
     * @param nodeWidth the width of the node to bookmark
     */
    private void drawBookmarkIndicator(final double nodeX, final double nodeWidth) {
        graphicsContext.setFill(HighlightType.BOOKMARKED.color);
        graphicsContext.fillRect(nodeX, canvasHeight - BOOKMARK_INDICATOR_HEIGHT, nodeWidth, BOOKMARK_INDICATOR_HEIGHT);
    }

    /**
     * Draw a sequence as black text inside a node.
     *
     * @param nodeX     the top left x position of the node
     * @param nodeY     the top left y position of the node
     * @param nodeWidth the width of the node
     * @param sequence  the sequence of the node
     */
    public void drawNodeSequence(final double nodeX, final double nodeY, final double nodeWidth,
                                 final String sequence) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(nodeFont);
        final int charCount = (int) Math.max((nodeWidth - ARC_SIZE) / charWidth, 0);

        final double fontX = nodeX + (nodeWidth + (ARC_SIZE / 4.0) - charCount * charWidth) / 2;
        final double fontY = nodeY + nodeHeight / 2 + charHeight / 2;

        graphicsContext.fillText(sequence.substring(0, Math.min(sequence.length(), charCount)), fontX, fontY);
    }

    /**
     * A highlight type denotes what the highlight type is.
     */
    public enum HighlightType {
        SELECTED(Color.rgb(0, 255, 46)),
        BOOKMARKED(Color.RED),
        QUERIED(Color.PURPLE);

        private Color color;


        /**
         * Creates a new {@link HighlightType}.
         *
         * @param color the {@link Color} of the highlight type
         */
        HighlightType(final Color color) {
            this.color = color;
        }
    }
}
