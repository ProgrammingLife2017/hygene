package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.lang3.SystemUtils;
import org.dnacronym.hygene.graph.annotation.Annotation;

import java.util.List;
import java.util.Map;


/**
 * Toolkit used to draw nodes.
 * <p>
 * This class deals with the drawing of nodes. This includes highlighting a node, drawing text in a node and drawing
 * a bookmark identifier of a node.
 */
public abstract class NodeDrawingToolkit extends DrawingToolkit {
    /**
     * Font used inside the nodes, this should always be a monospace font.
     */
    private static final String DEFAULT_NODE_FONT = "Courier New";
    private static final String DEFAULT_MAC_NODE_FONT = "Andale Mono";
    /**
     * Scalar for the size of the node text font as fraction of the node's height.
     */
    static final double DEFAULT_NODE_FONT_HEIGHT_SCALAR = 0.7;
    static final int BOOKMARK_INDICATOR_HEIGHT = 10;
    static final int NODE_OUTLINE_WIDTH = 3;
    static final int SNP_HEIGHT_FACTOR = 3;
    static final int ARC_SIZE = 10;

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
    public final void setNodeHeight(final double nodeHeight) {
        this.nodeHeight = nodeHeight;
        this.snpHeight = nodeHeight * SNP_HEIGHT_FACTOR;

        final Text text = new Text("X");
        text.setFont(new Font(DEFAULT_NODE_FONT, 1));

        final double font1PHeight = text.getLayoutBounds().getHeight();

        final String font;

        if (SystemUtils.IS_OS_MAC) {
            font = DEFAULT_MAC_NODE_FONT;
        } else {
            font = DEFAULT_NODE_FONT;
        }

        final double fontSize = DEFAULT_NODE_FONT_HEIGHT_SCALAR * nodeHeight / font1PHeight;
        this.nodeFont = new Font(font, fontSize);
        text.setFont(nodeFont);

        this.charWidth = text.getLayoutBounds().getWidth();
        this.charHeight = text.getLayoutBounds().getHeight();
    }

    /**
     * Sets the canvas height.
     *
     * @param canvasHeight the canvas height
     */
    public final void setCanvasHeight(final double canvasHeight) {
        this.canvasHeight = canvasHeight;
    }



    /**
     * Fills a round rectangle based on the node position and width, with the set {@link Color} fill.
     *
     * @param nodeX     the top left x position of the node
     * @param nodeY     the top left y position of the node
     * @param nodeWidth the width of the node
     */
    public abstract void draw(double nodeX, double nodeY, double nodeWidth);

    /**
     * Fills a round rectangle based on the node position and width, with the set {@link Color} fill.
     *
     * @param nodeX     the top left x position of the node
     * @param nodeY     the top left y position of the node
     * @param nodeWidth the width of the node
     * @param color     the {@link Color} to fill the node with
     */
    public abstract void draw(double nodeX, double nodeY, double nodeWidth, Color color, String sequence);

    /**
     * Fills a round rectangle based on the node position and width.
     * <p>
     * The genome colors are spread evenly across the height of the node. They are drawn as lanes along the node.
     *
     * @param nodeX        the top left x position of the node
     * @param nodeY        the top left y position of the node
     * @param nodeWidth    the width of the node
     * @param genomeColors the colors of the paths going through the node
     */
    public abstract void drawGenomes(double nodeX, double nodeY, double nodeWidth, List<Color> genomeColors);

    /**
     * Draws annotations below a node.
     * <p>
     * Annotations have the given colors, and are dashed.
     *
     * @param nodeX       the top left x position of the node
     * @param nodeY       the top left y position of the node
     * @param nodeWidth   the width of the node
     * @param annotations
     * @param startOffset the offset of the start of the annotation in the node. If it runs from the start of the
     *                    node, it should be 0
     * @param endOffset   the offset of the end of the annotation in de node. If it runs to the end of the node, it
     *                    should be equal to the node width
     */
    public abstract void drawAnnotations(double nodeX, double nodeY, final double nodeWidth,
                                         final List<Annotation> annotations, final Map<Annotation, Double> startOffset,
                                         final Map<Annotation, Double> endOffset);

    /**
     * Draw a highlight band around a given node of width {@value NODE_OUTLINE_WIDTH}.
     *
     * @param nodeX         the top left x position of the node
     * @param nodeY         the top left y position of the node
     * @param nodeWidth     the width of the node
     * @param highlightType the type of highlight
     */
    public abstract void drawHighlight(double nodeX, double nodeY, double nodeWidth, HighlightType highlightType);

    /**
     * Draw a sequence as black text inside a node.
     *
     * @param nodeX     the top left x position of the node
     * @param nodeY     the top left y position of the node
     * @param nodeWidth the width of the node
     * @param sequence  the sequence of the node
     */
    public abstract void drawSequence(double nodeX, double nodeY, double nodeWidth, String sequence);


    /**
     * Returns the node height.
     *
     * @return the node height
     */
    final double getNodeHeight() {
        return nodeHeight;
    }

    /**
     * Returns the SNP height.
     *
     * @return the SNP height
     */
    final double getSnpHeight() {
        return snpHeight;
    }

    /**
     * Returns the character width.
     *
     * @return the character width
     */
    final double getCharWidth() {
        return charWidth;
    }

    /**
     * Returns the character width.
     *
     * @return the character width
     */
    final double getCharHeight() {
        return charHeight;
    }

    /**
     * Returns the node font.
     *
     * @return the node font
     */
    final Font getNodeFont() {
        return nodeFont;
    }


    /**
     * Draws a bookmark indicator at the bottom of the graph.
     *
     * @param nodeX     the left x position of the node to bookmark
     * @param nodeWidth the width of the node to bookmark
     */
    final void drawBookmarkIndicator(final double nodeX, final double nodeWidth) {
        getGraphicsContext().setFill(HighlightType.BOOKMARKED.getColor());
        getGraphicsContext()
                .fillRect(nodeX, canvasHeight - BOOKMARK_INDICATOR_HEIGHT, nodeWidth, BOOKMARK_INDICATOR_HEIGHT);
    }
}
