package org.dnacronym.hygene.models;

import org.dnacronym.hygene.parser.ParseException;

import java.util.List;
import java.util.Optional;


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
     * Retrieves metadata of the given edge.
     *
     * @param node the edge to retrieve metadata of
     * @return metadata of the edge
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public static NodeMetadata retrieveFor(final Node node) throws ParseException {
        return Optional.ofNullable(node.getGraph())
                .orElseThrow(() -> new ParseException("Cannot parse metadata for node because graph is unknown"))
                .getGfaFile()
                .parseNodeMetadata(node.getLineNumber());
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
