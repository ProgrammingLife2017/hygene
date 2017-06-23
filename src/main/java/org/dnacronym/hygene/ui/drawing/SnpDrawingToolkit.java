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
        getGraphicsContext().setFill(color);
        getGraphicsContext().fillPolygon(getRhombusX(snpX, snpWidth), getRhombusY(snpY), 4);
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
        getGraphicsContext().setStroke(highlightType.getColor());
        getGraphicsContext().setLineWidth(NODE_OUTLINE_WIDTH);
        getGraphicsContext().strokePolygon(getRhombusX(snpX, snpWidth), getRhombusY(snpY), 4);

        if (highlightType == HighlightType.BOOKMARKED) {
            drawBookmarkIndicator(snpX, snpWidth);
        }
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


    /**
     * Returns the four X-coordinates for a rhombus at the given position with the given width.
     *
     * @param left  the left-most point of the rhombus
     * @param width the width of the rhombus
     * @return the four X-coordinates for a rhombus at the given position with the given width
     */
    private double[] getRhombusX(final double left, final double width) {
        final double center = left + width / 2;

        return new double[] {
                center - width / 2,
                center,
                center + width / 2,
                center
        };
    }

    /**
     * Returns the four Y-coordinates for a rhombus at the given position.
     *
     * @param top the top of the rhombus
     * @return the four Y-coordinates for a rhombus at the given position
     */
    private double[] getRhombusY(final double top) {
        final double center = top + getNodeHeight() / 2;

        return new double[] {
                center,
                center - getSnpHeight() / 2,
                center,
                center + getSnpHeight() / 2
        };
    }
}
