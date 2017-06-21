package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Collections;
import java.util.List;


/**
 * Toolkit used to draw nodes.
 * <p>
 * This class deals with the drawing of nodes. This includes highlighting a node, drawing text in a node and drawing
 * a bookmark identifier of a node.
 */
public final class NodeDrawingToolkit extends DrawingToolkit {
    private static final int BOOKMARK_INDICATOR_HEIGHT = 10;
    private static final int NODE_OUTLINE_WIDTH = 3;
    private static final int SNP_HEIGHT_FACTOR = 3;
    private static final int ARC_SIZE = 10;
    /**
     * Font used inside the nodes, this should always be a monospace font.
     */
    private static final String DEFAULT_NODE_FONT = "Consolas";
    /**
     * Scalar for the size of the node text font as fraction of the node's height.
     */
    private static final double DEFAULT_NODE_FONT_HEIGHT_SCALAR = 0.7;

    private double canvasHeight;
    private double nodeHeight;
    private double snpHeight;
    private double charWidth;
    private double charHeight;
    private Font nodeFont;


    /**
     * Sets the node height used for drawing.
     * <p>
     * Also updates the font size used for drawing sequences within nodes.
     *
     * @param nodeHeight the node height
     */
    public void setNodeHeight(final double nodeHeight) {
        this.nodeHeight = nodeHeight;
        this.snpHeight = nodeHeight * SNP_HEIGHT_FACTOR;

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
     * @param color     the {@link Color} to fill the node with
     */
    public void drawNode(final double nodeX, final double nodeY, final double nodeWidth, final Color color) {
        drawNodeGenomes(nodeX, nodeY, nodeWidth, Collections.singletonList(color));
    }

    /**
     * Fills a round rectangle based on the node position and width.
     * <p>
     * The genome colors are spread evenly across the height of the node. They are drawn as lanes along the node.
     *
     * @param nodeX      the top left x position of the node
     * @param nodeY      the top left y position of the node
     * @param nodeWidth  the width of the node
     * @param pathColors the colors of the paths going through the node
     */
    public void drawNodeGenomes(final double nodeX, final double nodeY, final double nodeWidth,
                                final List<Color> pathColors) {
        double laneHeightOffset = nodeY;
        final double laneHeight = nodeHeight / pathColors.size();

        for (final Color color : pathColors) {
            getGraphicsContext().setFill(color);
            getGraphicsContext().fillRoundRect(nodeX, laneHeightOffset, nodeWidth, laneHeight, ARC_SIZE, ARC_SIZE);

            laneHeightOffset += laneHeight;
        }
    }

    /**
     * Draws annotations below a node.
     * <p>
     * Annotations have the given colors, and are dashed.
     *
     * @param nodeX            the top left x position of the node
     * @param nodeY            the top left y position of the node
     * @param nodeWidth        the width of the node
     * @param annotationColors the colors of the annotations going through the node
     */
    public void drawNodeAnnotations(final double nodeX, final double nodeY, final double nodeWidth,
                                    final List<Color> annotationColors) {
        getGraphicsContext().setLineDashes(ANNOTATION_DASH_LENGTH);
        getGraphicsContext().setLineWidth(getAnnotationHeight());

        double annotationYOffset = nodeY + nodeHeight + getAnnotationHeight() + getAnnotationHeight() / 2;
        for (final Color color : annotationColors) {
            getGraphicsContext().setStroke(color);
            getGraphicsContext().strokeLine(nodeX, annotationYOffset, nodeX + nodeWidth, annotationYOffset);

            annotationYOffset += getAnnotationHeight();
        }

        getGraphicsContext().setLineDashes(ANNOTATION_DASH_DEFAULT);
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
        getGraphicsContext().setStroke(highlightType.color);
        getGraphicsContext().setLineWidth(NODE_OUTLINE_WIDTH);
        getGraphicsContext().strokeRoundRect(
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
        getGraphicsContext().setFill(HighlightType.BOOKMARKED.color);
        getGraphicsContext()
                .fillRect(nodeX, canvasHeight - BOOKMARK_INDICATOR_HEIGHT, nodeWidth, BOOKMARK_INDICATOR_HEIGHT);
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
        getGraphicsContext().setFill(Color.BLACK);
        getGraphicsContext().setFont(nodeFont);
        final int charCount = (int) Math.max((nodeWidth - ARC_SIZE) / charWidth, 0);

        final double fontX = nodeX + (nodeWidth + (ARC_SIZE / 4.0) - charCount * charWidth) / 2;
        final double fontY = nodeY + nodeHeight / 2 + charHeight / 2;

        getGraphicsContext().fillText(sequence.substring(0, Math.min(sequence.length(), charCount)), fontX, fontY);
    }

    /**
     * Fills a rhombus based on the snp position and width, with the set {@link Color} fill.
     *
     * @param snpX     the top left x position of the snp
     * @param snpY     the top left y position of the snp
     * @param snpWidth the width of the snp
     * @param color    the {@link Color} to fill the node with
     */
    public void drawSnp(final double snpX, final double snpY, final double snpWidth, final Color color) {
        final double snpXMiddle = snpX + snpWidth / 2;
        final double snpYMiddle = snpY + nodeHeight / 2;

        getGraphicsContext().setFill(color);
        getGraphicsContext().fillPolygon(
                new double[] {
                        snpXMiddle - snpWidth / 2,
                        snpXMiddle,
                        snpXMiddle + snpWidth / 2,
                        snpXMiddle
                },
                new double[] {
                        snpYMiddle,
                        snpYMiddle - snpHeight / 2,
                        snpYMiddle,
                        snpYMiddle + snpHeight / 2
                },
                4);
    }


    /**
     * A highlight type denotes what the highlight type is.
     */
    public enum HighlightType {
        SELECTED(Color.rgb(0, 255, 46)),
        BOOKMARKED(Color.RED),
        HIGHLIGHTED(Color.BLACK),
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


        /**
         * Returns the {@link Color} of this highlight type.
         *
         * @return the {@link Color} of this highlight type
         */
        public Color getColor() {
            return color;
        }
    }
}
