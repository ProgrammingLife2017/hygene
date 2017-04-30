package org.dnacronym.insertproductname.parser;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     * Returns the {@code Segment} with the given name, or {@code null} if it doesn't exist.
     *
     * @param name the name of the {@code Segment}.
     * @return the {@code Segment} with the given name, or {@code null} if it doesn't exist.
     */
    public @Nullable Segment getSegment(final String name) {
        return segments.get(name);
    }

    /**
     * Returns the {@code Collection} of {@code Segment}s.
     *
     * @return the {@code Collection} of {@code Segment}s.
     */
    public Collection<Segment> getSegments() {
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
