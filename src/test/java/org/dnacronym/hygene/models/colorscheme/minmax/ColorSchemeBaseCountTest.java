package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
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
final class ColorSchemeBaseCountTest {
    private ColorSchemeBaseCount colorSchemeBaseCount;


    @BeforeEach
    void beforeEach() {
        this.colorSchemeBaseCount = new ColorSchemeBaseCount(Color.CORNSILK, Color.ANTIQUEWHITE, "G");
    }


    @Test
    void testGetBase() {
        assertThat(colorSchemeBaseCount.getBase()).isEqualTo("G");
    }

    @Test
    void testBaseCount() throws ParseException {
        final Node node = mock(Node.class);
        final NodeMetadata nodeMetadata = mock(NodeMetadata.class);
        final String sequence = "AGGAA";
        when(node.retrieveMetadata()).thenReturn(nodeMetadata);
        when(nodeMetadata.getSequence()).thenReturn(sequence);

        assertThat(colorSchemeBaseCount.calculateColor(node)).isEqualTo(
                Color.CORNSILK.interpolate(Color.ANTIQUEWHITE, 2.0 / sequence.length())
        );
    }
}
