package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.parser.ParseException;


/**
 * Color is determined by the number of occurrences of the specified base in the sequence of the {@link Node}.
 * <p>
 * Each time the color is determined by the base portion of the node, not a set max value.
 */
public final class ColorSchemeBaseCount extends AbstractColorSchemeMinMax {
    private static final Logger LOGGER = LogManager.getLogger(ColorSchemeBaseCount.class);

    private final String base;


    /**
     * Creates an instance of {@link ColorSchemeBaseCount}.
     *
     * @param minColor the minimum color of the color scheme
     * @param maxColor the maximum color of the color scheme
     * @param base the base to count in the sequence
     */
    public ColorSchemeBaseCount(final Color minColor, final Color maxColor, final String base) {
        super(1, minColor, maxColor);
        this.base = base;
    }


    /**
     * Returns the base which is counted in the sequences of {@link Node}s.
     *
     * @return base which is counted in the sequences of {@link Node}s
     */
    public String getBase() {
        return base;
    }

    @Override
    public Color calculateColor(final Node node) {
        try {
            final String sequence = node.retrieveMetadata().getSequence();
            final int baseCount = StringUtils.countMatches(sequence, base);

            return getMinColor().interpolate(getMaxColor(), (double) baseCount / sequence.length());
        } catch (final ParseException e) {
            LOGGER.error("Unable to retrieve node sequence for color node.", e);
            return calculateColor(0);
        }
    }
}
