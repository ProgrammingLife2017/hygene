package org.dnacronym.hygene.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * A {@link SequenceAlignmentGraph} is a collection of {@link Segment}s and {@link Link}s between those
 * {@link Segment}s.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class SequenceAlignmentGraph {
    private final Map<String, Segment> segments;
    private final List<Link> links;


    /**
     * Constructs a new, empty {@link SequenceAlignmentGraph}.
     */
    public SequenceAlignmentGraph() {
        segments = new HashMap<>();
        links = new ArrayList<>();
    }


    /**
     * Returns the {@link Segment} with the given name, or throws an exception if it doesn't exist.
     *
     * @param name the name of the {@link Segment}
     * @return the {@link Segment} with the given name if it exists
     * @throws ParseException if segment with given name is not present
     */
    public Segment getSegment(final String name) throws ParseException {
        return Optional.ofNullable(segments.get(name))
                .orElseThrow(() -> new ParseException("Segment " + name + " is not present."));
    }

    /**
     * Returns the {@link Collection} of {@link Segment}s.
     *
     * @return the {@link Collection} of {@link Segment}s
     */
    public Collection<Segment> getSegments() {
        return segments.values();
    }

    /**
     * Adds a {@link Segment} to this {@link SequenceAlignmentGraph}.
     *
     * @param segment a {@link Segment}
     */
    public void addSegment(final Segment segment) {
        segments.put(segment.getName(), segment);
    }

    /**
     * Adds a {@link Link} to this {@link SequenceAlignmentGraph}.
     *
     * @param link a {@link Link}
     */
    public void addLink(final Link link) {
        links.add(link);
    }

    /**
     * Returns the {@link List} of {@link Link}s in this {@link SequenceAlignmentGraph}.
     *
     * @return the {@link List} of {@link Link}s in this {@link SequenceAlignmentGraph}
     */
    public List<Link> getLinks() {
        return links;
    }
}
