package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Segment;


/**
 * Represents a {@link NodeTooltip}.
 */
public class NodeTooltip {
    private static final Color HYGENE_COLOR = Color.rgb(0, 179, 146);
    private static final int WIDTH = 300;
    private static final int HEIGHT = 90;
    private static final int NODE_Y_OFFSET = 10;
    private static final int BORDER_BOTTOM_HEIGHT = 4;
    private static final int X_PADDING = 10;
    private static final int Y_PADDING = 20;

    private final GraphicsContext graphicsContext;
    private final GfaNode node;
    private final double middleX;
    private final double belowY;


    /**
     * Constructs {@link NodeTooltip}.
     *
     * @param graphicsContext the context
     * @param node the node
     * @param middleX the middle x position of the node
     * @param belowY the below y position of the node
     */
    public NodeTooltip(final GraphicsContext graphicsContext, final GfaNode node, final double middleX, final double belowY) {
        this.graphicsContext = graphicsContext;
        this.node = node;
        this.middleX = middleX;
        this.belowY = belowY;
    }


    /**
     * Shows the tooltip.
     */
    public void show() {
        drawBox();
        drawNodeId();
        drawSequence();
        drawTip();
    }

    /**
     * Draws the node ID text.
     */
    private void drawNodeId() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(
                "Node ID:  " + node.getSegments().get(0).getId(),
                middleX - (WIDTH / 2) + X_PADDING,
                belowY + NODE_Y_OFFSET + Y_PADDING
        );
    }

    /**
     * Draws the sequence text.
     */
    private void drawSequence() {
        final Segment segment = node.getSegments().get(0);
        if (segment.hasMetadata()) {
            final String sequence;
            if (segment.getMetadata().getSequence().length() > 20) {
               sequence = segment.getMetadata().getSequence().substring(0, 20)
                        + (segment.getMetadata().getSequence().length() > 20 ? "..." : "");
            } else {
                sequence = segment.getMetadata().getSequence();
            }
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillText(
                    "Sequence: " + sequence,
                    middleX - (WIDTH / 2) + X_PADDING,
                    belowY + NODE_Y_OFFSET + Y_PADDING + 20
            );
        }
    }

    /**
     * Draws the tip text.
     */
    private void drawTip() {
        graphicsContext.setFill(Color.GREY);
        graphicsContext.fillText(
                "Click the node to see more details",
                middleX - (WIDTH / 2) + X_PADDING,
                belowY + NODE_Y_OFFSET + Y_PADDING + 45
        );
    }

    /**
     * Draws the tooltip box.
     */
    private void drawBox() {
        graphicsContext.setEffect(new DropShadow(10, 0, 2, Color.GREY));

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(middleX - (WIDTH / 2), belowY + NODE_Y_OFFSET, WIDTH, HEIGHT);
        graphicsContext.setEffect(null);

        graphicsContext.fillPolygon(
                new double[]{middleX, middleX - 10, middleX + 10},
                new double[]{belowY, belowY + 10, belowY + 10},
                3
        );

        graphicsContext.setFill(HYGENE_COLOR);
        graphicsContext.fillRect(
                middleX - (WIDTH / 2),
                belowY + NODE_Y_OFFSET + (HEIGHT - BORDER_BOTTOM_HEIGHT),
                WIDTH,
                BORDER_BOTTOM_HEIGHT
        );
    }
}
