package org.dnacronym.hygene.models;

import java.util.List;


/**
 * Represents the metadata of a {@link Node}.
 */
public final class NodeMetadata {
    private final String name;
    private final String sequence;
    private final List<String> genomes;


    /**
     * Constructs and initializes a {@link NodeMetadata} object.
     *
     * @param name     the node ID in the GFA file
     * @param sequence the DNA sequence
     * @param genomes  the list of genomes this node belongs to
     */
    public NodeMetadata(final String name, final String sequence, final List<String> genomes) {
        this.name = name;
        this.sequence = sequence;
        this.genomes = genomes;
    }


    /**
     * Gets the original node ID of the GFA file.
     *
     * @return the original node ID of the GFA file
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the DNA sequence of the node.
     *
     * @return the DNA sequence of the node
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Returns the genomes this node belongs to.
     *
     * @return the genomes
     */
    public List<String> getGenomes() {
        return genomes;
    }
}
