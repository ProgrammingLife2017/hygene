package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.parser.ParseException;


/**
 * Color is determined by the amount of the set base in the sequence of the {@link Node}.
 * <p>
 * Each time the color is determined by the base portion of the node, not a set max value.
 */
public final class ColorSchemeBaseCount extends AbstractColorSchemeMinMax {
    private static final Logger LOGGER = LogManager.getLogger(ColorSchemeBaseCount.class);

    private static final String DEFAULT_BASE = "G";

    private String base;


    /**
     * Creates an instance of {@link ColorSchemeBaseCount}.
     * <p>
     * The default base is set to {@value DEFAULT_BASE}.
     */
    public ColorSchemeBaseCount() {
        super(1);
        base = DEFAULT_BASE;
    }


    /**
     * Sets the base to count in the node sequence.
     *
     * @param base the base to count in the sequence
     */
    public void setBase(final String base) {
        this.base = base;
    }

    /**s
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
            setMaxValue(sequence.length());

            return calculateColor(StringUtils.countMatches(sequence, base));
        } catch (final ParseException e) {
            LOGGER.error("Unable to retrieve node sequence for color node.", e);
            return calculateColor(0);
        }
    }
}
