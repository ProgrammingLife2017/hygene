package org.dnacronym.hygene.events;

import org.dnacronym.hygene.models.GraphQuery;


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
}
