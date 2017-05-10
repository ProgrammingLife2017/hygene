package org.dnacronym.hygene.models;

/**
 * Represents the metadata of a {@link Node}.
 */
public final class NodeMetadata {
    private final String originalNodeId;
    private final String sequence;


    /**
     * Constructs and initializes a {@link NodeMetadata} object.
     *
     * @param originalNodeId the node ID in the GFA file
     * @param sequence the DNA sequence
     */
    public NodeMetadata(final String originalNodeId, final String sequence) {
        this.originalNodeId = originalNodeId;
        this.sequence = sequence;
    }


    /**
     * Gets the original node ID of the GFA file.
     *
     * @return the original node ID of the GFA file.
     */
    public String getOriginalNodeId() {
        return originalNodeId;
    }

    /**
     * Gets the DNA sequence of the node.
     *
     * @return the DNA sequence of the node.
     */
    public String getSequence() {
        return sequence;
    }
}
