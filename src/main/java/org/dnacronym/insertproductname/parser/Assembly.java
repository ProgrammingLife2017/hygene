package org.dnacronym.insertproductname.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * .
 *
 * @author Felix Dekker
 */
public final class Assembly {
    private final Map<String, Segment> segments;
    private final List<Link> links;


    public Assembly() {
        segments = new HashMap<>();
        links = new ArrayList<>();
    }


    public final Segment getSegment(final String name) {
        return segments.get(name);
    }

    public final void addSegment(final Segment segment) {
        segments.put(segment.getName(), segment);
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(final Link link) {
        links.add(link);
    }
}
