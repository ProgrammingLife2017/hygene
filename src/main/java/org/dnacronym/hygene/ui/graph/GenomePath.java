package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * Class representing a Genome path.
 * <p>
 * This class is used for marking genomes as selected or not.
 */
public class GenomePath {
    private final String name;
    private final String index;
    private final SimpleBooleanProperty isSelected;

    /**
     * Construct a new {@link GenomePath}.
     *
     * @param name  the name
     * @param index the index
     */
    public GenomePath(final String name, final String index) {
        this.name = name;
        this.index = index;
        this.isSelected = new SimpleBooleanProperty();
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
    public boolean isIsSelected() {
        return isSelected.get();
    }

    /**
     * Property determining whether or not a genome is selected.
     *
     * @return property which decides whether the genome is currently selected
     */
    public SimpleBooleanProperty isSelectedProperty() {
        return isSelected;
    }
}
