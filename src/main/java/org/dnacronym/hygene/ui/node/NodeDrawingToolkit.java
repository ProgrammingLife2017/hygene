package org.dnacronym.hygene.ui.node;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.dnacronym.hygene.models.NodeColor;


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
    private double laneHeight;
    private double nodeHeight;
    private double charWidth;
    private double charHeight;
    private Font nodeFont;

    private double nodeY;
    private double nodeX;
    private double nodeWidth;
    private Color nodeColor;
    private String sequence;

    private boolean highlight;
    private boolean drawSequence;
    private boolean bookmarked;


    /**
     * Sets the {@link GraphicsContext} used for drawing.
     *
     * @param graphicsContext the {@link GraphicsContext} to set
     */
    public void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
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
     * Sets the lane height.
     *
     * @param laneHeight the lane height
     */
    public void setLaneHeight(final double laneHeight) {
        this.laneHeight = laneHeight;
    }

    /**
     * Sets the highlighted property.
     *
     * @param highlight the highlight property
     * @return current instance of the toolkit to provide a fluent interface
     */
    public NodeDrawingToolkit setHighlighted(final boolean highlight) {
        this.highlight = highlight;
        return this;
    }

    /**
     * Sets the draw text property.
     *
     * @param drawSequence the draw text property
     * @return current instance of the toolkit to provide a fluent interface
     */
    public NodeDrawingToolkit setDrawSequence(final boolean drawSequence) {
        this.drawSequence = drawSequence;
        return this;
    }

    /**
     * Sets the bookmarked property.
     *
     * @param bookmarked the bookmarked property
     * @return current instance of the toolkit to provide a fluent interface
     */
    public NodeDrawingToolkit setBookmarked(final boolean bookmarked) {
        this.bookmarked = bookmarked;
        return this;
    }

    /**
     * Sets the node dimensions.
     *
     * @param nodeX     the upper left x of the node
     * @param nodeY     the upper left y of the node
     * @param nodeWidth the width of the node
     * @return current instance of the toolkit to provide a fluent interface
     */
    public NodeDrawingToolkit setDimensions(final double nodeX, final double nodeY, final double nodeWidth) {
        this.nodeX = nodeX;
        this.nodeY = nodeY;
        this.nodeWidth = nodeWidth;
        return this;
    }

    /**
     * Sets the color of the node.
     *
     * @param nodeColor the color of the node
     * @return current instance of the toolkit to provide a fluent interface
     */
    public NodeDrawingToolkit setNodeColor(final Color nodeColor) {
        this.nodeColor = nodeColor;
        return this;
    }

    /**
     * Sets the sequence of the node.
     *
     * @param sequence the sequence of the node
     * @return current instance of the toolkit to provide a fluent interface
     */
    public NodeDrawingToolkit setSequence(final String sequence) {
        this.sequence = sequence;
        return this;
    }

    /**
     * Draw the node based on the set values.
     */
    public void drawNode() {
        graphicsContext.setFill(nodeColor);
        graphicsContext.fillRoundRect(nodeX, nodeY, nodeWidth, nodeHeight, ARC_SIZE, ARC_SIZE);

        if (highlight) {
            drawNodeHighlight(NodeColor.BRIGHT_GREEN.getFXColor());
        }
        if (drawSequence) {
            drawNodeSequence(charWidth, charHeight);
        }
        if (bookmarked) {
            drawNodeBookmark(laneHeight - nodeHeight, canvasHeight);
        }
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
     * Draw a highlight band around a given node of width {@value NODE_OUTLINE_WIDTH}.
     *
     * @param highlightColor the color of the highlight
     */
    void drawNodeHighlight(final Color highlightColor) {
        graphicsContext.setStroke(highlightColor);
        graphicsContext.setLineWidth(NODE_OUTLINE_WIDTH);
        graphicsContext.strokeRoundRect(
                nodeX - NODE_OUTLINE_WIDTH / 2.0,
                nodeY - NODE_OUTLINE_WIDTH / 2.0,
                nodeWidth + NODE_OUTLINE_WIDTH,
                nodeHeight + NODE_OUTLINE_WIDTH,
                ARC_SIZE, ARC_SIZE);
    }

    /**
     * Draw a bookmark flag and bookmark indicator for a node.
     *
     * @param arrowHeight       the height the arrow should be
     * @param bottomYIdentifier the bottom y position of the identifier at the bottom of the graph
     */
    @SuppressWarnings("MagicNumber") // Using constants here does not make the method more readable.
    void drawNodeBookmark(final double arrowHeight, final double bottomYIdentifier) {
        graphicsContext.setFill(Color.RED);

        final double arrowPortionHeight = arrowHeight / 4;
        final double arrowHorizontalCenter = nodeX + nodeWidth / 2;

        graphicsContext.fillPolygon(
                new double[] {arrowHorizontalCenter,
                        arrowHorizontalCenter - nodeWidth / 4, arrowHorizontalCenter - nodeWidth / 8,
                        arrowHorizontalCenter - nodeWidth / 8, arrowHorizontalCenter + nodeWidth / 8,
                        arrowHorizontalCenter + nodeWidth / 8, arrowHorizontalCenter + nodeWidth / 4,
                        arrowHorizontalCenter},
                new double[] {nodeY,
                        nodeY - arrowPortionHeight, nodeY - arrowPortionHeight,
                        nodeY - arrowHeight, nodeY - arrowHeight,
                        nodeY - arrowPortionHeight, nodeY - arrowPortionHeight,
                        nodeY},
                8);

        graphicsContext.fillRect(nodeX, bottomYIdentifier - BOOKMARK_IDENTIFIER_HEIGHT,
                nodeWidth, BOOKMARK_IDENTIFIER_HEIGHT);
    }

    /**
     * Draw a sequence as black text inside a node.
     *
     * @param charWidth  the width of a single character
     * @param charHeight the height of a single character
     */
    void drawNodeSequence(final double charWidth, final double charHeight) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(nodeFont);
        final int charCount = (int) Math.max((nodeWidth - ARC_SIZE) / charWidth, 0);

        final double fontX = nodeX + (nodeWidth + (ARC_SIZE / 4.0) - charCount * charWidth) / 2;
        final double fontY = nodeY + nodeHeight / 2 + charHeight / 2;

        graphicsContext.fillText(sequence.substring(0, Math.min(sequence.length(), charCount)), fontX, fontY);
    }
}
