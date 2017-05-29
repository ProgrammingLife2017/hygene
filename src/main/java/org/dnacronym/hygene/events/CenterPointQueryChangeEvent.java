package org.dnacronym.hygene.events;

import org.dnacronym.hygene.models.GraphQuery;

import java.util.Set;


/**
 * Represents a change in the center point query.
 */
public final class CenterPointQueryChangeEvent {
    private final GraphQuery graphQuery;


    /**
     * Constructs and initializes {@link CenterPointQueryChangeEvent}.
     *
     * @param graphQuery the query that was updated
     */
    public CenterPointQueryChangeEvent(final GraphQuery graphQuery) {
        this.graphQuery = graphQuery;
    }


    /**
     * Returns the updated query.
     *
     * @return the updated query
     */
    public GraphQuery getGraphQuery() {
        return graphQuery;
    }

    /**
     * Returns node IDs that should be present in the graph visualization after the center point query change.
     *
     * @return a set of node IDs
     */
    public Set<Integer> getNodeIds() {
        return graphQuery.getNodeIds();
    }

    /**
     * Returns the new radius of the center point query.
     *
     * @return the new radius of the center point query
     */
    public int getRadius() {
        return graphQuery.getRadius();
    }

}
