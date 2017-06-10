package org.dnacronym.hygene.coordinatesystem;


/**
 * Class representing a point in the genome coordinate system.
 */
public final class GenomeIndexPoint {
    private final int genomeId;
    private final int baseId;
    private final int nodeId;


    /**
     * Constructs a new {@link GenomeIndexPoint} instance.
     *
     * @param genomeId the ID of the genome
     * @param baseId   the base count in that genome at the beginning of the node
     * @param nodeId   the ID of the node at that index point
     */
    public GenomeIndexPoint(final int genomeId, final int baseId, final int nodeId) {
        this.genomeId = genomeId;
        this.baseId = baseId;
        this.nodeId = nodeId;
    }


    /**
     * Returns the genome ID.
     *
     * @return the genome ID
     */
    public int getGenomeId() {
        return genomeId;
    }

    /**
     * Returns the base ID.
     *
     * @return the base ID
     */
    public int getBaseId() {
        return baseId;
    }

    /**
     * Returns the node ID.
     *
     * @return the node ID
     */
    public int getNodeId() {
        return nodeId;
    }
}
