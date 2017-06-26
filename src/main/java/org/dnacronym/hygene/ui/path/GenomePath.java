package org.dnacronym.hygene.ui.path;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Class representing a genome path in the GUI.
 * <p>
 * This class is used for marking genomes as selected.
 */
public final class GenomePath {
    private static final Logger LOGGER = LogManager.getLogger(GenomePath.class);


    private final String name;
    private final String index;
    private final SimpleBooleanProperty selected;
    private final SimpleObjectProperty<Color> color;


    /**
     * Construct a new {@link GenomePath}.
     *
     * @param index the index
     * @param name  the name
     */
    public GenomePath(final String index, final String name) {
        this.name = name;
        this.index = index;
        this.selected = new SimpleBooleanProperty();
        this.color = new SimpleObjectProperty<>();
    }


    /**
     * Gets the genome's name.
     *
     * @return the genome's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the genome's index.
     *
     * @return the genome's index
     */
    public String getIndex() {
        return index;
    }

    /**
     * Whether the user has selected this genome in the GUI.
     *
     * @return true when the genome is selected in the GUI
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Property determining whether or not a genome is selected.
     *
     * @return property which decides whether the genome is currently selected
     */
    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public SimpleObjectProperty<Color> getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        LOGGER.info("Changing color of " + getName() + " to " + getColor().get());
        this.color.set(color);
    }
}
