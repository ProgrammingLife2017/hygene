package org.dnacronym.hygene.events;

import org.dnacronym.hygene.graph.CenterPointQuery;
import org.dnacronym.hygene.graph.Segment;

import java.util.Set;
import java.util.stream.Collectors;


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
     * Returns node IDs that should be present in the graph visualization after the center point query change.
     *
     * @return a set of node IDs
     */
    public Set<Integer> getNodeIds() {
        return centerPointQuery.getCache().getNodes().stream()
                .filter(node -> node instanceof Segment)
                .map(node -> ((Segment) node).getId())
                .collect(Collectors.toSet());
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
