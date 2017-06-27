package org.dnacronym.hygene.ui.graph;

import javafx.collections.transformation.FilteredList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.ui.path.GenomePath;

import javax.inject.Inject;


/**
 * Represents a {@link NodeTooltip}.
 */
public class NodeTooltip {
    private static final Color HYGREEN = Color.rgb(0, 179, 146);
    private static final int DEFAULT_WIDTH = 315;
    private static final int DEFAULT_HEIGHT = 85;
    private static final int LINE_HEIGHT = 10;
    private static final int BORDER_BOTTOM_HEIGHT = 4;
    private static final int X_PADDING = 10;
    private static final int Y_PADDING = 20;
    private static final int MAX_SEQUENCE_LENGTH = 20;

    private final GraphVisualizer graphVisualizer;
    private final GraphicsContext graphicsContext;
    private final GfaNode node;
    private final int height;
    private final double middleX;
    private final double belowY;

    private int offset;


    /**
     * Constructs {@link NodeTooltip}.
     *
     * @param graphicsContext the context
     * @param node            the node
     * @param middleX         the middle x position of the node
     * @param belowY          the below y position of the node
     */
    @Inject
    public NodeTooltip(final GraphVisualizer graphVisualizer, final GraphicsContext graphicsContext,
                       final GfaNode node, final double middleX, final double belowY) {
        int tempHeight = DEFAULT_HEIGHT;

        this.graphVisualizer = graphVisualizer;
        this.graphicsContext = graphicsContext;
        this.node = node;
        this.middleX = middleX;
        this.belowY = belowY;

        if (node.hasMetadata()) {
            tempHeight += node.getMetadata().getGenomes().size() * LINE_HEIGHT + 10;
        }
        if (node.getSegments().get(0).getSequenceLength() > MAX_SEQUENCE_LENGTH) {
            tempHeight += LINE_HEIGHT + 5;
        }

        this.height = tempHeight;
    }


    /**
     * Shows the tooltip.
     */
    public void show() {
        drawBox();

        drawTip();
        drawSequence();
        drawSequenceLength();
        drawGenomes();
    }

    /**
     * Draws the tooltip box.
     */
    private void drawBox() {
        graphicsContext.setEffect(new DropShadow(10, 0, 2, Color.GREY));

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(middleX - (DEFAULT_WIDTH / 2), belowY + LINE_HEIGHT, DEFAULT_WIDTH, height);
        graphicsContext.setEffect(null);

        graphicsContext.fillPolygon(
                new double[] {middleX, middleX - 10, middleX + 10},
                new double[] {belowY, belowY + 10, belowY + 10},
                3
        );

        graphicsContext.setFill(HYGREEN);
        graphicsContext.fillRect(
                middleX - (DEFAULT_WIDTH / 2),
                belowY + LINE_HEIGHT + (height - BORDER_BOTTOM_HEIGHT),
                DEFAULT_WIDTH,
                BORDER_BOTTOM_HEIGHT
        );
    }

    /**
     * Draws the tip text.
     */
    private void drawTip() {
        if (!node.hasMetadata() || node.getMetadata().getSequence().length() <= MAX_SEQUENCE_LENGTH) {
            return;
        }

        graphicsContext.setFill(Color.GREY);
        graphicsContext.fillText(
                "Click the node to see the full sequence.",
                middleX - (DEFAULT_WIDTH / 2) + X_PADDING,
                belowY + LINE_HEIGHT + Y_PADDING + offset
        );

        offset += LINE_HEIGHT + 5;
    }

    /**
     * Draws the sequence text.
     */
    private void drawSequence() {
        if (!node.hasMetadata()) {
            return;
        }

        final Segment segment = node.getSegments().get(0);
        final String sequence = limitStringAt(segment.getMetadata().getSequence(), MAX_SEQUENCE_LENGTH);

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(
                "Sequence: " + sequence,
                middleX - (DEFAULT_WIDTH / 2) + X_PADDING,
                belowY + LINE_HEIGHT + Y_PADDING + offset
        );

        offset += LINE_HEIGHT;
    }

    /**
     * Draws the sequence length text.
     */
    private void drawSequenceLength() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(
                "Length: " + node.getSegments().get(0).getSequenceLength(),
                middleX - (DEFAULT_WIDTH / 2) + X_PADDING,
                belowY + LINE_HEIGHT + Y_PADDING + offset
        );

        offset += LINE_HEIGHT;
    }

    /**
     * Draws the list of genomes the node is in.
     */
    private void drawGenomes() {
        if (!node.hasMetadata()) {
            return;
        }

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(
                "Genomes:",
                middleX - (DEFAULT_WIDTH / 2) + X_PADDING,
                belowY + LINE_HEIGHT + Y_PADDING + offset
        );
        offset += LINE_HEIGHT;

        node.getMetadata().getGenomes().forEach(genome -> {
            final FilteredList<GenomePath> possiblePaths = graphVisualizer.getGenomePathsProperty()
                    .filtered(genomePath -> genomePath.getName().equals(genome));
            if (possiblePaths.isEmpty()) {
                return;
            }

            final Color color = possiblePaths.get(0).getColor().get();
            graphicsContext.setFill(color == null ? Color.BLACK : color);
            graphicsContext.fillText("    " + genome,
                    middleX - (DEFAULT_WIDTH / 2) + X_PADDING,
                    belowY + LINE_HEIGHT + Y_PADDING + offset);
            offset += LINE_HEIGHT;
        });
    }

    private String limitStringAt(final String string, final int length) {
        if (string.length() > length) {
            return string.substring(0, length) + (string.length() > length ? "..." : "");
        } else {
            return string;
        }
    }
}
