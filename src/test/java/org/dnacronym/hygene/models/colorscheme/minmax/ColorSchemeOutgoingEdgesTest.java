package org.dnacronym.hygene.models.colorscheme.minmax;

import javafx.scene.paint.Color;
import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ColorSchemeOutgoingEdges}.
 */
final class ColorSchemeOutgoingEdgesTest {
    private ColorSchemeOutgoingEdges colorSchemeOutgoingEdges;


    @BeforeEach
    void beforeEach() {
        colorSchemeOutgoingEdges = new ColorSchemeOutgoingEdges(32, Color.AZURE, Color.BURLYWOOD);
    }


    @Test
    void testOutgoingEdgesColor() {
        final Node node = mock(Node.class);
        when(node.getNumberOfOutgoingEdges()).thenReturn(3);

        assertThat(colorSchemeOutgoingEdges.calculateColor(node)).isEqualTo(
                Color.AZURE.interpolate(Color.BURLYWOOD, 3.0 / 32)
        );
    }
}
