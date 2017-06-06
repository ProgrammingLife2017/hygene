package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeIncomingEdges}.
 */
final class ColorSchemeIncomingEdgesTest {
    private ColorSchemeIncomingEdges colorSchemeIncomingEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeIncomingEdges = new ColorSchemeIncomingEdges(10, Color.BLACK, Color.GREEN);
    }


    @Test
    void testIncomingEdgesColor() {
        final Node node = mock(Node.class);
        when(node.getNumberOfIncomingEdges()).thenReturn(3);

        assertThat(colorSchemeIncomingEdges.calculateColor(node)).isEqualTo(
                Color.BLACK.interpolate(Color.GREEN, 3.0 / 10)
        );
    }
}
