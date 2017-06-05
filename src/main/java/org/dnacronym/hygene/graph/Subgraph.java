package org.dnacronym.hygene.graph;

import java.util.HashSet;
import java.util.Set;


/**
 * Class representing a subgraph.
 */
public final class Subgraph {
    private final Set<Node> nodes;
    private final Set<Node> sourceNeighbours;
    private final Set<Node> sinkNeighbours;


    /**
     * Constructs a new {@link Subgraph} instance.
     *
     * @param nodes the nodes of this subgraph
     */
    public Subgraph(final Set<Node> nodes) {
        this.nodes = nodes;

        sourceNeighbours = new HashSet<>();
        sinkNeighbours = new HashSet<>();
    }


    /**
     * Returns the nodes.
     *
     * @return the nodes
     */
    public Set<Node> getNodes() {
        return nodes;
    }

    /**
     * Returns the neighbours of the source node.
     *
     * @return the neighbours of the source node
     */
    public Set<Node> getSourceNeighbours() {
        return sourceNeighbours;
    }

    /**
     * Returns the neighbours of the sink node.
     *
     * @return the neighbours of the sink node
     */
    public Set<Node> getSinkNeighbours() {
        return sinkNeighbours;
    }
}
