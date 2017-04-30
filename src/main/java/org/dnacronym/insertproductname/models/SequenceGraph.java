package org.dnacronym.insertproductname.models;


/**
 * A {@code SequenceGraph} contains {@code SequenceNode}s.
 */
public final class SequenceGraph {
    private final SequenceNode startNode;


    /**
     * Constructs a new {@code SequenceGraph} with the given starting node.
     *
     * @param startNode the first link of the segment chain
     */
    public SequenceGraph(final SequenceNode startNode) {
        this.startNode = startNode;
    }


    /**
     * Returns the start node.
     *
     * @return the start node.
     */
    public SequenceNode getStartNode() {
        return startNode;
    }
}
