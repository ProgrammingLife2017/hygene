package org.dnacronym.hygene.models;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.dnacronym.hygene.events.CenterPointQueryChangeEvent;
import org.dnacronym.hygene.parser.ParseException;

import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Represents the cache of {@link Node}s with metadata loaded.
 */
public final class NodeMetadataCache {
    private static final Logger LOGGER = LogManager.getLogger(NodeMetadataCache.class);

    /**
     * Defines the maximum radius for which nodes will be cached.
     */
    private static final int CACHE_RADIUS_THRESHOLD = 150;

    private final ConcurrentHashMap<Integer, Node> cache;
    private final Graph graph;

    private @MonotonicNonNull Thread thread;

    private int previousCenterPoint = -1;
    private int previousRadius = -1;


    /**
     * Constructs and initializes {@link NodeMetadataCache}.
     *
     * @param graph the currently loaded graph which the nodes are located on
     */
    public NodeMetadataCache(final Graph graph) {
        this.graph = graph;
        cache = new ConcurrentHashMap<>();
    }


    /**
     * Returns true if the cache has metadata of the given node id stored.
     *
     * @param nodeId ID of the node
     * @return true if the cache has metadata of the given node id stored
     */
    public boolean has(final int nodeId) {
        return cache.containsKey(nodeId);
    }

    /**
     * Returns {@link Node} with loaded metadata of the given node id.
     * <p>
     * If the node is not in the cache (yet) it will be retrieved on demand.
     *
     * @param nodeId ID of the node
     * @return {@link Node} with loaded metadata of the given node id
     * @throws ParseException if the node is not in memory yet and parsing fails
     */
    public Node getOrRetrieve(final int nodeId) throws ParseException {
        final Node cachedNode = cache.get(nodeId);

        if (cachedNode != null) {
            return cachedNode;
        }

        return retrieve(nodeId);
    }

    /**
     * Handles the {@link CenterPointQueryChangeEvent} by adding new items to the cache.
     * <p>
     * The process of retrieving items is done in a background thread to not influence the performance
     * of the application.
     *
     * @param event the event describing the change in the center point query
     */
    @Subscribe
    public void centerPointQueryChanged(final CenterPointQueryChangeEvent event) {
        if (event.getRadius() > CACHE_RADIUS_THRESHOLD
                || event.getCenterPoint() == previousCenterPoint && event.getRadius() == previousRadius) {
            return;
        }

        previousCenterPoint = event.getCenterPoint();
        previousRadius = event.getRadius();

        cache.keySet().retainAll(event.getNodeIds());

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(() -> addNewItemsToCache(event.getNodeIds()));

        thread.start();
    }

    /**
     * Returns the background thread.
     *
     * @return the background thread
     */
    @RequiresNonNull("thread")
    Thread getThread() {
        return thread;
    }

    /**
     * Adds new items to the cache if they are not present yet.
     * <p>
     * If the thread is interrupted, the process is stopped.
     *
     * @param nodeIds set of node ids that should be in the cache after executing this method
     */
    private void addNewItemsToCache(final Set<Integer> nodeIds) {
        nodeIds.stream().forEach(nodeId -> cache.put(nodeId, graph.getNode(nodeId)));

        retrieveMetadataForCachedNodesWithoutMetadata();
    }

    /**
     * Retrieve metadata for cached nodes that have no metadata set yet.
     */
    @SuppressWarnings("squid:S1166") // No need to log the exception itself, a message is enough.
    private void retrieveMetadataForCachedNodesWithoutMetadata() {
        try {
            final Map<Integer, NodeMetadata> metadata = graph.getGfaFile().parseNodeMetadata(
                    cache.values().stream()
                            .filter(node -> !node.hasMetadata())
                            .sorted(Comparator.comparingInt(Node::getLineNumber))
                            .collect(Collectors.toMap(
                                    Node::getId,
                                    Node::getLineNumber,
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new
                            ))
            );

            metadata.entrySet().stream().forEach(entry -> {
                final Node node = cache.get(entry.getKey());
                if (node != null) {
                    node.setMetadata(entry.getValue());
                }
            });
        } catch (final ParseException e) {
            LOGGER.warn("Node metadata could not be retrieved.", e);
        } catch (final UncheckedIOException e) {
            LOGGER.warn("Retrieving metadata of nodes was interrupted.");
        }
    }

    /**
     * Creates {@link Node} object, retrieves its metadata and stores the {@link Node} in the cache.
     *
     * @param nodeId ID of the node
     * @return {@link Node} with loaded metadata of the given node id
     * @throws ParseException if parsing the metadata fails
     */
    private Node retrieve(final int nodeId) throws ParseException {
        final Node node = graph.getNode(nodeId);
        node.retrieveMetadata();

        cache.put(nodeId, node);

        return node;
    }
}
