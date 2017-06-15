package org.dnacronym.hygene.events;

import org.dnacronym.hygene.graph.CenterPointQuery;


/**
 * Represents a change in the center point query.
 */
public final class CenterPointQueryChangeEvent {
    private final CenterPointQuery centerPointQuery;


    /**
     * Constructs and initializes {@link CenterPointQueryChangeEvent}.
     *
     * @param centerPointQuery the query that was updated
     */
    public CenterPointQueryChangeEvent(final CenterPointQuery centerPointQuery) {
        this.centerPointQuery = centerPointQuery;
    }


    /**
     * Returns the updated query.
     *
     * @return the updated query
     */
    public CenterPointQuery getCenterPointQuery() {
        return centerPointQuery;
    }

    /**
     * Returns the new center point of the center point query.
     *
     * @return the new center point of the center point query
     */
    public int getCenterPoint() {
        return centerPointQuery.getCentre();
    }

    /**
     * Returns the new radius of the center point query.
     *
     * @return the new radius of the center point query
     */
    public int getRadius() {
        return centerPointQuery.getRadius();
    }
}
