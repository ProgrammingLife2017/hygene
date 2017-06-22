package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;

import java.util.List;


/**
 * Toolkit used to draw SNPs; single-nucleotide polymorphisms.
 */
public final class SnpDrawingToolkit extends NodeDrawingToolkit {
    /**
     * Fills a rhombus based on the node position and width, with the set {@link Color} fill.
     *
     * @param snpX     the top left x position of the node
     * @param snpY     the top left y position of the node
     * @param snpWidth the width of the node
     * @param color    the {@link Color} to fill the node with
     */
    @Override
    public void draw(final double snpX, final double snpY, final double snpWidth, final Color color) {
        final double snpXMiddle = snpX + snpWidth / 2;
        final double snpYMiddle = snpY + getNodeHeight() / 2;

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
                        snpYMiddle - getSnpHeight() / 2,
                        snpYMiddle,
                        snpYMiddle + getSnpHeight() / 2
                },
                4);
    }

    /**
     * Fills a rhombus based on the node position and width.
     * <p>
     * The genome colors are spread evenly across the height of the node. They are drawn as lanes along the node.
     *
     * @param snpX         the top left x position of the node
     * @param snpY         the top left y position of the node
     * @param snpWidth     the width of the node
     * @param genomeColors the colors of the paths going through the node
     */
    @Override
    public void drawGenomes(final double snpX, final double snpY, final double snpWidth,
                            final List<Color> genomeColors) {
        // Not yet implemented
    }

    /**
     * Draws annotations below a node.
     * <p>
     * Annotations have the given colors, and are dashed.
     *
     * @param snpX             the top left x position of the node
     * @param snpY             the top left y position of the node
     * @param snpWidth         the width of the node
     * @param annotationColors the colors of the annotations going through the node
     */
    @Override
    public void drawAnnotations(final double snpX, final double snpY, final double snpWidth,
                                final List<Color> annotationColors) {
        // Not yet implemented
    }

    /**
     * Draw a highlight band around a given node of width {@value NODE_OUTLINE_WIDTH}.
     *
     * @param snpX          the top left x position of the node
     * @param snpY          the top left y position of the node
     * @param snpWidth      the width of the node
     * @param highlightType the type of highlight
     */
    @Override
    public void drawHighlight(final double snpX, final double snpY, final double snpWidth,
                              final HighlightType highlightType) {
        // Not yet implemented
    }

    /**
     * Draw a sequence as black text inside a node.
     *
     * @param snpX     the top left x position of the node
     * @param snpY     the top left y position of the node
     * @param snpWidth the width of the node
     * @param sequence the sequence of the node
     */
    @Override
    public void drawSequence(final double snpX, final double snpY, final double snpWidth, final String sequence) {
        // Not yet implemented
    }
}
