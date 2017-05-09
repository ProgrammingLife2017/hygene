package org.dnacronym.hygene.models;

public class NodeMetadata {
    private String originalNodeId;
    private String sequence;

    public NodeMetadata(String originalNodeId, String sequence) {
        this.originalNodeId = originalNodeId;
        this.sequence = sequence;
    }

    public String getOriginalNodeId() {
        return originalNodeId;
    }

    public String getSequence() {
        return sequence;
    }
}
