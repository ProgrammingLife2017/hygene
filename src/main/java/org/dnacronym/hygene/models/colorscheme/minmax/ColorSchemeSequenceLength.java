package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;


/**
 * The color is determined by the sequence length.
 */
public final class ColorSchemeSequenceLength extends AbstractColorSchemeMinMax {
    private static final int DEFAULT_MAX = 100;


    /**
     * Creates instance of {@link ColorSchemeSequenceLength} with max value set to {@value DEFAULT_MAX}.
     */
    public ColorSchemeSequenceLength() {
        super(DEFAULT_MAX);
    }


    @Override
    public Color calculateColor(final Node node) {
        return calculateColor(node.getSequenceLength());
    }
}
