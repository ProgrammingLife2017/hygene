package org.dnacronym.hygene.ui.path;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Unit test for {@link GenomePath}.
 */
class GenomePathTest {
    private GenomePath genomePath;


    @BeforeEach
    void setUp() {
        genomePath = new GenomePath("1", "nutella.fasta");
    }


    @Test
    void testGetName() {
        assertThat(genomePath.getName()).isEqualTo("nutella.fasta");
    }

    @Test
    void testGetIndex() {
        assertThat(genomePath.getIndex()).isEqualTo("1");
    }

    @Test
    void testIsSelectedFalse() {
        assertThat(genomePath.isSelected()).isFalse();
    }

    @Test
    void testIsSelectedTrue() {
        genomePath.selectedProperty().set(false);
        assertThat(genomePath.isSelected()).isFalse();
    }

    @Test
    void testSelectedProperty() {
        assertThat(genomePath.selectedProperty().get()).isFalse();
    }

    @Test
    void testToString() {
        assertThat(genomePath.toString()).isEqualTo("nutella.fasta");
    }

    @Test
    void testColor() {
        genomePath.setColor(Color.RED);
        assertThat(genomePath.getColor().get()).isEqualTo(Color.RED);
    }
}
