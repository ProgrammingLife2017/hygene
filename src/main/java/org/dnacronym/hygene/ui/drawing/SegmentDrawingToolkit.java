package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.annotation.Annotation;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Toolkit used to draw segments.
 */
public final class SegmentDrawingToolkit extends NodeDrawingToolkit {
    @Override
    public void draw(final double nodeX, final double nodeY, final double nodeWidth) {
        getGraphicsContext().fillRect(nodeX, nodeY, nodeWidth, getNodeHeight());
    }

    /**
     * Fills a round rectangle based on the node position and width, with the set {@link Color} fill.
     *
     * @param segmentX     the top left x position of the node
     * @param segmentY     the top left y position of the node
     * @param segmentWidth the width of the node
     * @param color        the {@link Color} to fill the node with
     */
    @Override
    public void draw(final double segmentX, final double segmentY, final double segmentWidth, final Color color,
                     final String sequence) {
        drawGenomes(segmentX, segmentY, segmentWidth, Collections.singletonList(color));
    }

    /**
     * Fills a round rectangle based on the node position and width.
     * <p>
     * The genome colors are spread evenly across the height of the node. They are drawn as lanes along the node.
     *
     * @param segmentX     the top left x position of the node
     * @param segmentY     the top left y position of the node
     * @param segmentWidth the width of the node
     * @param genomeColors the colors of the paths going through the node
     */
    @Override
    public void drawGenomes(final double segmentX, final double segmentY, final double segmentWidth,
                            final List<Color> genomeColors) {
        double laneHeightOffset = segmentY;
        final double laneHeight = getNodeHeight() / genomeColors.size();

        for (final Color color : genomeColors) {
            getGraphicsContext().setFill(color);
            getGraphicsContext().fillRoundRect(segmentX, laneHeightOffset, segmentWidth, laneHeight, ARC_SIZE,
                    ARC_SIZE);

            laneHeightOffset += laneHeight;
        }
    }

    /**
     * Draws annotations below a node.
     * <p>
     * Annotations have the given colors, and are dashed.
     *
     * @param segmentX     the top left x position of the node
     * @param segmentY     the top left y position of the node
     * @param nodeWidth    the width of the node
     * @param startOffsets the offset of the start of the annotation in the node. If it runs from the start of the
     *                     node, it should be 0
     */
    @Override
    public void drawAnnotations(final double segmentX, final double segmentY, final double nodeWidth,
                                final List<Annotation> annotations, final Map<Annotation, Double> startOffsets,
                                final Map<Annotation, Double> endOffsets) {
        getGraphicsContext().setLineDashes(ANNOTATION_DASH_LENGTH);
        getGraphicsContext().setLineWidth(getAnnotationHeight());

        double annotationYOffset = segmentY + getNodeHeight() + getAnnotationHeight() + getAnnotationHeight() / 2;
        for (final Annotation annotation : annotations) {
            final Color color = annotation.getColor();

            final double startOffset = startOffsets.containsKey(annotation) ? startOffsets.get(annotation) : 0;
            final double endOffset = endOffsets.containsKey(annotation) ? endOffsets.get(annotation) : 1;

            getGraphicsContext().setStroke(color);
            getGraphicsContext().strokeLine(
                    segmentX + startOffset * nodeWidth, annotationYOffset,
                    segmentX + endOffset * nodeWidth, annotationYOffset);

            annotationYOffset += getAnnotationHeight();
        }

        getGraphicsContext().setLineDashes(ANNOTATION_DASH_DEFAULT);
    }

    /**
     * Draw a highlight band around a given node of width {@value NODE_OUTLINE_WIDTH}.
     *
     * @param segmentX      the top left x position of the node
     * @param segmentY      the top left y position of the node
     * @param segmentWidth  the width of the node
     * @param highlightType the type of highlight
     */
    @Override
    public void drawHighlight(final double segmentX, final double segmentY, final double segmentWidth,
                              final HighlightType highlightType) {
        getGraphicsContext().setStroke(highlightType.getColor());
        getGraphicsContext().setLineWidth(NODE_OUTLINE_WIDTH);
        getGraphicsContext().strokeRoundRect(
                segmentX - NODE_OUTLINE_WIDTH / 2.0,
                segmentY - NODE_OUTLINE_WIDTH / 2.0,
                segmentWidth + NODE_OUTLINE_WIDTH,
                getNodeHeight() + NODE_OUTLINE_WIDTH,
                ARC_SIZE, ARC_SIZE);

        if (highlightType == HighlightType.BOOKMARKED) {
            drawBookmarkIndicator(segmentX, segmentWidth);
        }
    }

    /**
     * Draw a sequence as black text inside a node.
     *
     * @param segmentX     the top left x position of the node
     * @param segmentY     the top left y position of the node
     * @param segmentWidth the width of the node
     * @param sequence     the sequence of the node
     */
    @Override
    public void drawSequence(final double segmentX, final double segmentY, final double segmentWidth,
                             final String sequence) {
        getGraphicsContext().setFill(Color.BLACK);
        getGraphicsContext().setFont(getNodeFont());

        final int charCount = (int) (segmentWidth / getCharWidth());
        final String sequenceToDraw;
        if (charCount == 0) {
            return;
        } else if (sequence.length() > charCount) {
            sequenceToDraw = sequence.substring(0, charCount - 1) + "\u2026";
        } else {
            sequenceToDraw = sequence;
        }

        final double sequenceWidth = sequenceToDraw.length() * getCharWidth();

        final double fontX = segmentX + segmentWidth / 2 - sequenceWidth / 2;
        final double fontY = segmentY + getNodeHeight() / 2 + getCharHeight() / 4;

        getGraphicsContext().fillText(sequenceToDraw, fontX, fontY);
    }
}
