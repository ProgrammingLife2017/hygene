package org.dnacronym.hygene.graph.metadata;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.core.ThrottledExecutor;
import org.dnacronym.hygene.event.CenterPointQueryChangeEvent;
import org.dnacronym.hygene.event.LayoutDoneEvent;
import org.dnacronym.hygene.event.NodeMetadataCacheUpdateEvent;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.graph.Subgraph;
import org.dnacronym.hygene.graph.PathCalculator;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ParseException;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Represents the cache of {@link Node}s with metadata loaded.
 */
public final class NodeMetadataCache {
    private static final Logger LOGGER = LogManager.getLogger(NodeMetadataCache.class);

    /**
     * Defines the maximum radius for which nodes will be cached.
     */
    private static final int RADIUS_THRESHOLD = 150;
    /**
     * The minimum number of milliseconds that must be between each retrieval operation.
     */
    private static final int RETRIEVE_METADATA_TIMEOUT = 10;

    private final ThrottledExecutor retrievalExecutor;
    private final GfaFile gfaFile;
    private final Map<Integer, NodeMetadata> cache;
    private final PathCalculator pathCalculator;

    private int currentRadius;


    /**
     * Constructs and initializes {@link NodeMetadataCache}.
     *
     * @param gfaFile the {@link GfaFile} that provides the parsing functionality
     */
    public NodeMetadataCache(final GfaFile gfaFile) {
        this.retrievalExecutor = new ThrottledExecutor(RETRIEVE_METADATA_TIMEOUT);
        this.gfaFile = gfaFile;
        this.cache = new HashMap<>();
        this.pathCalculator = new PathCalculator();
    }


    /**
     * Updates the current radius when a {@link org.dnacronym.hygene.graph.CenterPointQuery} changes.
     *
     * @param event a {@link CenterPointQueryChangeEvent}
     */
    @Subscribe
    public void centerPointQueryChanged(final CenterPointQueryChangeEvent event) {
        this.currentRadius = event.getRadius();
    }

    /**
     * Retrieves metadata as soon as the layout is done.
     *
     * @param event a {@link LayoutDoneEvent}
     */
    @Subscribe
    public void layoutDone(final LayoutDoneEvent event) {
        if (currentRadius >= RADIUS_THRESHOLD) {
            retrievalExecutor.stop();
            return;
        }

        retrievalExecutor.run(() -> {
            retrieveMetadata(gfaFile, event.getSubgraph());
            if (Thread.interrupted()) {
                return;
            }
            pathCalculator.computePaths(event.getSubgraph());
            HygeneEventBus.getInstance().post(new NodeMetadataCacheUpdateEvent(event.getSubgraph()));
        });
    }

    /**
     * Returns the {@link ThrottledExecutor} used to retrieve metadata.
     *
     * @return the {@link ThrottledExecutor} used to retrieve metadata
     */
    ThrottledExecutor getRetrievalExecutor() {
        return retrievalExecutor;
    }


    /**
     * Retrieves metadata for cached nodes that have no metadata yet.
     *
     * @param gfaFile  the {@link GfaFile} that provides the parsing functionality
     * @param subgraph a {@link Subgraph} with metadata
     */
    private void retrieveMetadata(final GfaFile gfaFile, final Subgraph subgraph) {
        subgraph.getSegments().stream()
                .filter(segment -> !segment.hasMetadata() && cache.containsKey(segment.getId()))
                .forEach(segment -> {
                    final NodeMetadata metadata = cache.get(segment.getId());
                    if (metadata != null) {
                        segment.setMetadata(metadata);
                    }
                });

        try {
            final Map<Integer, Long> sortedSegmentsWithoutMetadata
                    = getSortedSegmentsWithoutMetadata(subgraph.getSegments());
            final Map<Integer, NodeMetadata> metadata
                    = gfaFile.parseNodeMetadata(sortedSegmentsWithoutMetadata);

            metadata.entrySet().forEach(entry -> {
                final Segment segment = subgraph.getSegment(entry.getKey());
                if (segment != null) {
                    segment.setMetadata(entry.getValue());
                }
            });
        } catch (final ParseException e) {
            LOGGER.error("Node metadata could not be retrieved.", e);
        }
    }

    /**
     * Removes {@link Segment} that already have metadata, sorts them by ascending byte offset, and maps the
     * {@link Segment}s ids to their byte offsets.
     *
     * @param segments a {@link Collection} of {@link Segment}s
     * @return a map from {@link Segment} ids to their byte offsets
     */
    private Map<Integer, Long> getSortedSegmentsWithoutMetadata(final Collection<Segment> segments) {
        return segments.stream()
                .filter(node -> !node.hasMetadata())
                .sorted(Comparator.comparingLong(Segment::getByteOffset))
                .collect(Collectors.toMap(
                        Segment::getId,
                        Segment::getByteOffset,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}
