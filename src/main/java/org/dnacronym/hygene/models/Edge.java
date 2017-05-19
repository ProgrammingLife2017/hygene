package org.dnacronym.hygene.models;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.parser.ParseException;

import java.util.Objects;


/**
 * Edge class used with the internal data structure.
 */
public final class Edge implements Comparable<Edge> {
    private final int from;
    private final int to;
    private final int lineNumber;
    private final @Nullable Graph graph;


    /**
     * Constructs and initializes a new {@link Edge}.
     *
     * @param from       node from which the edge originates
     * @param to         node at which the edge ends
     * @param lineNumber line number of GFA file where the edge is specified
     * @param graph      a reference to the graph that the edge belongs to, in case there is no graph (yet) for this
     *                   edge to be on, null is accepted
     */
    public Edge(final int from, final int to, final int lineNumber, final @Nullable Graph graph) {
        this.from = from;
        this.to = to;
        this.lineNumber = lineNumber;
        this.graph = graph;
    }


    /**
     * Getter for the {@link Edge}'s source node.
     *
     * @return the edge's source node id
     */
    public int getFrom() {
        return from;
    }

    /**
     * Getter for the {@link Edge}'s destination.
     *
     * @return the edge's destination node id
     */
    public int getTo() {
        return to;
    }

    /**
     * Getter for the {@link Edge}'s line number.
     *
     * @return the edge's line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Getter for the {@link Graph} reference.
     *
     * @return a reference to the {@link Graph} the edge belongs to
     */
    public @Nullable Graph getGraph() {
        return graph;
    }

    /**
     * Retrieves metadata of the edge.
     *
     * @return metadata of the edge
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public EdgeMetadata retrieveMetadata() throws ParseException {
        return EdgeMetadata.retrieveFor(this);
    }

    /**
     * Compares this edge to another {@link Edge}.
     * <p>
     * First the destination 'to' is compared. If {@code to == other.to}, the source 'from' will be used for comparison.
     *
     * @param other {@link Edge} to compare this edge with
     * @return the value {@code 0} if {@code to == other.to} and {@code from == other.from}; a value less than {@code 0}
     * if {@code to < other.to} or {@code from < other.from}; and a value greater than {@code 0} if {@code to >
     * other.to} or {@code from > other.from}
     */
    @Override
    public int compareTo(final Edge other) {
        final int compareEdgeTo = Integer.compare(to, other.to);
        final int compareEdgeFrom = Integer.compare(from, other.from);

        if (compareEdgeTo == 0) {
            return compareEdgeFrom;
        }
        return compareEdgeTo;
    }

    @Override
    public boolean equals(final @Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Edge edge = (Edge) other;

        return to == edge.to && from == edge.from && lineNumber == edge.lineNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, from, lineNumber);
    }
}
