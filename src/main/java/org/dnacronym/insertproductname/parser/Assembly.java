package org.dnacronym.insertproductname.parser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * An {@code Assembly} is a collection of {@code Segment}s and {@code Link}s between those {@code Segment}s.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class Assembly {
    private final Map<String, Segment> segments;
    private final List<Link> links;


    /**
     * Constructs a new, empty {@code Assembly}.
     */
    public Assembly() {
        segments = new HashMap<>();
        links = new ArrayList<>();
    }


    /**
     * Returns the {@code Segment} with the given name, or throw exception if it doesn't exist.
     *
     * @param name the name of the {@code Segment}.
     * @return the {@code Segment} with the given name if it exists
     * @throws ParseException if segment with given name is not present
     */
    public @NonNull Segment getSegment(final @NonNull String name) throws ParseException {
        return Optional.ofNullable(segments.get(name))
                .orElseThrow(() -> new ParseException("Segment " + name + " is not present."));
    }

    /**
     * Returns the {@code Collection} of {@code Segment}s.
     *
     * @return the {@code Collection} of {@code Segment}s.
     */
    public Collection<@NonNull Segment> getSegments() {
        return segments.values();
    }

    /**
     * Adds a {@code Segment} to this {@code Assembly}.
     *
     * @param segment a {@code Segment}.
     */
    public void addSegment(final Segment segment) {
        segments.put(segment.getName(), segment);
    }

    /**
     * Adds a {@code Link} to this {@code Assembly}.
     *
     * @param link a {@code Link}.
     */
    public void addLink(final Link link) {
        links.add(link);
    }

    /**
     * Returns the {@code List} of {@code Link}s in this {@code Assembly}.
     *
     * @return the {@code List} of {@code Link}s in this {@code Assembly}.
     */
    public List<Link> getLinks() {
        return links;
    }
}
