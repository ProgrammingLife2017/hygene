package org.dnacronym.insertproductname.models;

import java.util.HashMap;
import java.util.Map;


/**
 * A {@code SequenceGraph} contains {@code SequenceNode}s.
 */
public final class SequenceGraph {
    private final Map<String, SequenceNode> nodes;
    private final SequenceNode startNode;
    private final SequenceNode endNode;


    /**
     * Constructs a new {@code SequenceGraph} with the given starting node.
     *
     * @param startNode the first link of the segment chain
     * @param endNode the last link of the segment chain
     */
    public SequenceGraph(final SequenceNode startNode, final SequenceNode endNode) {
        this.nodes = new HashMap<>();
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

    /**
     * Returns the nodes.
     * <p>
     * At a later stage (after an initial prototype of the parser has been built), this data structure will be better
     * encapsulated by this class, and this accessor removed.
     *
     * @return the nodes.
     */
    public Map<String, SequenceNode> getNodes() {
        return nodes;
    }

    /**
     * Returns the size of the graph, measured in terms of the number of nodes.
     *
     * @return the number of nodes in the graph.
     */
    public int size() {
        return nodes.size();
    }
}
