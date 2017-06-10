package org.dnacronym.hygene.coordinatesystem;


/**
 * Class representing an index point in the genome coordinate system.
 */
public final class GenomePoint {
    private final int genomeId;
    private final int base;
    private final int nodeId;
    private final int baseOffsetInNode;


    /**
     * Constructs a new {@link GenomePoint} instance.
     *
     * @param genomeId         the ID of the genome
     * @param base             the base count in that genome at the beginning of the node
     * @param nodeId           the ID of the node at that index point
     * @param baseOffsetInNode the offset of bases in that node
     */
    public GenomePoint(final int genomeId, final int base, final int nodeId, final int baseOffsetInNode) {
        this.genomeId = genomeId;
        this.base = base;
        this.nodeId = nodeId;
        this.baseOffsetInNode = baseOffsetInNode;
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

    /**
     * Returns the base offset in the node this point belongs to.
     *
     * @return the base offset in the node this point belongs to
     */
    public int getBaseOffsetInNode() {
        return baseOffsetInNode;
    }
}
