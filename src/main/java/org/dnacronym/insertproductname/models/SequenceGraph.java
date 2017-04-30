package org.dnacronym.insertproductname.models;


/**
 * A {@code SequenceGraph} contains {@code SequenceNode}s.
 */
public final class SequenceGraph {
    private final SequenceNode startNode;
    private final SequenceNode endNode;


    /**
     * Constructs a new {@code SequenceGraph} with the given starting node.
     *
     * @param startNode the first link of the segment chain
     * @param endNode the last link of the segment chain
     */
    public SequenceGraph(final SequenceNode startNode, final SequenceNode endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
    }


    /**
     * Returns the start node.
     *
     * @return the start node.
     */
    public SequenceNode getStartNode() {
        return startNode;
    }

    /**
     * Returns the end node.
     *
     * @return the end node.
     */
    public SequenceNode getEndNode() {
        return endNode;
    }
}
