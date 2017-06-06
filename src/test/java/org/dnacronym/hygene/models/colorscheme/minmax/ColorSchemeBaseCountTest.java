package org.dnacronym.hygene.models.colorscheme.minmax;

import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeBaseCount}.
 */
final class ColorSchemeBaseCountTest extends ColorSchemeMinMaxBase {
    private ColorSchemeBaseCount colorSchemeBaseCount;


    @BeforeEach
    void beforeEach() {
        this.colorSchemeBaseCount = new ColorSchemeBaseCount();
        colorSchemeBaseCount.setMinColor(getMinColor());
        colorSchemeBaseCount.setMaxColor(getMaxColor());
    }

    @Test
    void testBaseCount() throws ParseException {
        final Node node = mock(Node.class);
        final NodeMetadata nodeMetadata = mock(NodeMetadata.class);
        final String sequence = "AGGAA";
        when(node.retrieveMetadata()).thenReturn(nodeMetadata);
        when(nodeMetadata.getSequence()).thenReturn(sequence);

        assertThat(colorSchemeBaseCount.calculateColor(node)).isEqualTo(
                getMinColor().interpolate(getMaxColor(), 2.0 / sequence.length())
        );
    }

    @Test
    void testBaseCountSetBase() throws ParseException {
        final Node node = mock(Node.class);
        final NodeMetadata nodeMetadata = mock(NodeMetadata.class);
        final String sequence = "AGGAA";
        when(node.retrieveMetadata()).thenReturn(nodeMetadata);
        when(nodeMetadata.getSequence()).thenReturn(sequence);

        colorSchemeBaseCount.setBase("A");
        assertThat(colorSchemeBaseCount.calculateColor(node)).isEqualTo(
                getMinColor().interpolate(getMaxColor(), 3.0 / sequence.length())
        );
    }
}
