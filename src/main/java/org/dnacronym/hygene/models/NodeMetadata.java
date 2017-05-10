package org.dnacronym.hygene.models;

/**
 * Represents the metadata of a {@link Node}.
 */
public class NodeMetadata {
    private String originalNodeId;
    private String sequence;


    /**
     * Constructs and initializes a {@link NodeMetadata} object.
     *
     * @param originalNodeId the node ID in the GFA file
     * @param sequence the DNA sequence
     */
    public NodeMetadata(String originalNodeId, String sequence) {
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
