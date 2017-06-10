package org.dnacronym.hygene.coordinatesystem;


/**
 * Class representing a point in the genome coordinate system.
 */
public final class GenomeIndexPoint {
    private final int genomeId;
    private final int base;
    private final int nodeId;


    /**
     * Constructs a new {@link GenomeIndexPoint} instance.
     *
     * @param genomeId the ID of the genome
     * @param base     the base count in that genome at the beginning of the node
     * @param nodeId   the ID of the node at that index point
     */
    public GenomeIndexPoint(final int genomeId, final int base, final int nodeId) {
        this.genomeId = genomeId;
        this.base = base;
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
     * Returns the base.
     *
     * @return the base
     */
    public int getBase() {
        return base;
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
