package org.dnacronym.insertproductname.models;

import java.util.List;


/**
 * A {@code SequenceGraph} contains {@code SequenceNode}s.
 *
 * This graph has all its nodes passed to it on construction. It then adds two sentinel nodes (a 'source' and a 'sink'),
 * to the start and end of the graph structure. The source node connects to all vertices without incoming edges on the
 * left side, while the sink node is connected to all vertices without incoming edges on the right side. These two nodes
 * allow for simplified and more unified graph processing.
 */
public final class SequenceGraph {
    public static final String SOURCE_NODE_ID = "<SOURCE>";
    public static final String SINK_NODE_ID = "<SINK>";

    private final List<SequenceNode> nodes;
    private final SequenceNode sourceNode;
    private final SequenceNode sinkNode;


    /**
     * Constructs a new {@code SequenceGraph} with the given nodes.
     *
     * @param nodes the list of nodes
     */
    public SequenceGraph(final List<SequenceNode> nodes) {
        this.nodes = nodes;
        this.sourceNode = new SequenceNode(SOURCE_NODE_ID, "");
        this.sinkNode = new SequenceNode(SINK_NODE_ID, "");

        initEdgeNodes();
    }


    /**
     * Finds the edge nodes of this graph and connects them to sentinels.
     */
    private void initEdgeNodes() {
        nodes.forEach(node -> {
            if (!node.hasLeftNeighbours()) {
                sourceNode.linkToRightNeighbour(node);
            }

            if (!node.hasRightNeighbours()) {
                sinkNode.linkToLeftNeighbour(node);
            }
        });
    }

    /**
     * Returns the start node.
     *
     * @return the start node.
     */
    public SequenceNode getSourceNode() {
        return sourceNode;
    }

    /**
     * Returns the end node.
     *
     * @return the end node.
     */
    public SequenceNode getSinkNode() {
        return sinkNode;
    }

    /**
     * Returns the nodes.
     * <p>
     * At a later stage (after an initial prototype of the parser has been built), this data structure will be better
     * encapsulated by this class, and this accessor removed.
     *
     * @return the nodes.
     */
    public List<SequenceNode> getNodes() {
        return nodes;
    }

    /**
     * Returns the size of the graph, measured in terms of the number of nodes.
     *
     * @return the number of nodes in the graph.
     */
    public int size() {
        return nodes.size() + 2;
    }
}
