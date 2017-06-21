package org.dnacronym.hygene.ui.path;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * Class representing a Genome path in GUI.
 * <p>
 * This class is used for marking genomes as selected or not.
 */
public final class GenomePath {
    private final String name;
    private final String index;
    private final SimpleBooleanProperty selected;

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
    }

    /**
     * Gets the genomes name.
     *
     * @return the genomes name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the genomes index.
     *
     * @return the genomes index
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
}
