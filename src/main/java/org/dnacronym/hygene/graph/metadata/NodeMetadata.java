package org.dnacronym.hygene.graph.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Represents the metadata of a {@link org.dnacronym.hygene.graph.node.Node}.
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
     * Combines the given sets of {@link NodeMetadata} into a single object.
     *
     * @param nodeMetadata a collection of {@link NodeMetadata}
     */
    public NodeMetadata(final Collection<NodeMetadata> nodeMetadata) {
        this.name = nodeMetadata.stream().map(NodeMetadata::getName).collect(Collectors.toList()).toString();
        this.sequence = nodeMetadata.stream().map(NodeMetadata::getSequence).collect(Collectors.toList()).toString();

        final Set<String> combinedGenomes = nodeMetadata.stream()
                .map(NodeMetadata::getGenomes)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        this.genomes = new ArrayList<>(combinedGenomes);
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
