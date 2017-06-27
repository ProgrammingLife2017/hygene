package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.graph.node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    public void draw(final double snpX, final double snpY, final double snpWidth, final Color color,
                     final String sequence) {
        final List<Character> sequences = extractSequences(sequence);

        if (sequences.isEmpty()) {
            getGraphicsContext().setFill(color);
            getGraphicsContext().fillPolygon(getRhombusX(snpX, snpWidth), getRhombusY(snpY), 4);
        } else {
            getGraphicsContext().setFill(Node.baseToColor(sequences.get(0)));
            getGraphicsContext().fillPolygon(getRhombusTopX(snpX, snpWidth), getRhombusTopY(snpY), 3);

            getGraphicsContext().setFill(Node.baseToColor(sequences.get(1)));
            getGraphicsContext().fillPolygon(getRhombusBottomX(snpX, snpWidth), getRhombusBottomY(snpY), 3);
        }
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
     * @param annotationColors the colors of the annotations going through the node
     * @param startOffset      the offset of the start of the annotation in the node. If it runs from the start of the
     *                         node, it should be 0
     * @param endOffset        the offset of the end of the annotation in de node. If it runs to the end of the node, it
     *                         should be equal to the node width
     */
    @Override
    public void drawAnnotations(final double snpX, final double snpY, final double nodeWidth,
                                final List<Annotation> annotations, final Map<Annotation, Double> startOffset,
                                final Map<Annotation, Double> endOffset) {
        getGraphicsContext().setLineDashes(ANNOTATION_DASH_LENGTH);
        getGraphicsContext().setLineWidth(getAnnotationHeight());

        double annotationYOffset = snpY + getNodeHeight() + getAnnotationHeight() + getAnnotationHeight() / 2;
        for (final Annotation annotation : annotations) {
            final Color color = annotation.getColor();
            getGraphicsContext().setStroke(color);
            getGraphicsContext().strokeLine(snpX, annotationYOffset,
                    snpX + nodeWidth / 2, annotationYOffset + getSnpHeight() / 2);
            getGraphicsContext().strokeLine(snpX + nodeWidth / 2, annotationYOffset + getSnpHeight() / 2,
                    snpX + nodeWidth, annotationYOffset);

            annotationYOffset += getAnnotationHeight();
        }

        getGraphicsContext().setLineDashes(ANNOTATION_DASH_DEFAULT);
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
        if (snpWidth / 2 <= getCharWidth()) {
            return;
        }

        getGraphicsContext().setFill(Color.BLACK);
        getGraphicsContext().setFont(getNodeFont());

        final List<Character> sequences = extractSequences(sequence);
        getGraphicsContext().fillText(sequences.get(0).toString(),
                snpX + snpWidth / 2 - getCharWidth() / 2, snpY + 5);
        getGraphicsContext().fillText(sequences.get(1).toString(),
                snpX + snpWidth / 2 - getCharWidth() / 2, snpY + getSnpHeight() / 2 - 5);
    }


    private double[] getRhombusX(final double left, final double width) {
        final double center = left + width / 2;

        return new double[] {
                center - width / 2,
                center,
                center + width / 2,
                center
        };
    }

    private double[] getRhombusY(final double top) {
        final double center = top + getNodeHeight() / 2;

        return new double[] {
                center,
                center - getSnpHeight() / 2,
                center,
                center + getSnpHeight() / 2
        };
    }

    private double[] getRhombusTopX(final double left, final double width) {
        final double center = left + width / 2;

        return new double[] {
                center - width / 2,
                center,
                center + width / 2
        };
    }

    private double[] getRhombusBottomX(final double left, final double width) {
        final double center = left + width / 2;

        return new double[] {
                center + width / 2,
                center,
                center - width / 2,
        };
    }

    private double[] getRhombusTopY(final double top) {
        final double center = top + getNodeHeight() / 2;

        return new double[] {
                center,
                center - getSnpHeight() / 2,
                center
        };
    }

    private double[] getRhombusBottomY(final double top) {
        final double center = top + getNodeHeight() / 2;

        return new double[] {
                center,
                center + getSnpHeight() / 2,
                center
        };
    }


    private List<Character> extractSequences(final String sequenceDescription) {
        if ("".equals(sequenceDescription)) {
            return new ArrayList<>();
        }

        String sequences = sequenceDescription;
        if (sequences.charAt(0) == '[') {
            sequences = sequences.substring(1);
        }
        if (sequences.charAt(sequences.length() - 1) == ']') {
            sequences = sequences.substring(0, sequences.length() - 1);
        }

        return Arrays.stream(sequences.split(","))
                .map(String::trim)
                .map(sequence -> sequence.charAt(0))
                .collect(Collectors.toList());
    }
}
