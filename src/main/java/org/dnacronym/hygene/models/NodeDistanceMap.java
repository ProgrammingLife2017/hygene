package org.dnacronym.hygene.models;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Stores the distance for each node, and the nodes for each distance.
 * <p>
 * Functions similar to a map, but does not extend the {@link Map} interface to allow for a more fine-tuned interface.
 */
public final class NodeDistanceMap {
    /**
     * The nodes per distance.
     */
    private final SetMultimap<Integer, Integer> distanceNodes;
    /**
     * The distance per node.
     */
    private final Map<Integer, Integer> nodeDistances;


    /**
     * Constructs a new {@link NodeDistanceMap}.
     */
    public NodeDistanceMap() {
        distanceNodes = TreeMultimap.create();
        nodeDistances = new HashMap<>();
    }


    /**
     * Returns the number of nodes in this map.
     *
     * @return the number of nodes in this map
     */
    public int size() {
        return nodeDistances.size();
    }

    /**
     * Returns {@code true} iff. there are no nodes in this map.
     *
     * @return {@code true} iff. there are no nodes in this map
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns {@code true} iff. the node with the given id is in this map.
     *
     * @param node a node id
     * @return {@code true} iff. the node with the given id is in this map
     */
    public boolean containsNode(final Integer node) {
        return nodeDistances.containsKey(node);
    }

    /**
     * Returns the distance of the given node.
     *
     * @param node a node id
     * @return the distance of the given node, or {@code null} if the node is not in this map
     */
    public @Nullable Integer getDistance(final Integer node) {
        return nodeDistances.get(node);
    }

    /**
     * Sets the given node's distance to the given distance, unless the current distance is smaller.
     * <p>
     * If the node was registered to have another distance, the old distance pair will be removed.
     *
     * @param node     a node id
     * @param distance a distance
     * @return the previous distance of the given node, or {@code null} if there was none or the current distance is
     * smaller
     */
    public @Nullable Integer setDistance(final Integer node, final Integer distance) {
        final Integer oldDistance = getDistance(node);
        if (oldDistance != null && oldDistance < distance) {
            return null;
        }

        removeNode(node);
        distanceNodes.put(distance, node);
        nodeDistances.put(node, distance);
        return oldDistance;
    }

    /**
     * Returns a {@link Set} of all nodes with a given distance.
     *
     * @param distance a distance
     * @return a {@link Set} of all nodes with a given distance
     */
    public Set<Integer> getNodes(final Integer distance) {
        return Collections.unmodifiableSet(distanceNodes.get(distance));
    }

    /**
     * Removes the given node from this map.
     *
     * @param node a node id
     * @return the distance of the given node, or {@code null} if the node was not in this map
     */
    public @Nullable Integer removeNode(final Integer node) {
        if (!containsNode(node)) {
            return null;
        }

        final Integer distance = getDistance(node);
        if (distance == null) {
            return null;
        }

        return distanceNodes.remove(distance, node) && nodeDistances.remove(node, distance) ? distance : null;
    }

    /**
     * Removes all node-distance pairs from this map.
     */
    public void clear() {
        distanceNodes.clear();
        nodeDistances.clear();
    }


    /**
     * Returns a {@link Set} of the node ids present in this map.
     *
     * @return a {@link Set} of the node ids present in this map
     */
    public Set<@KeyFor("this.nodeDistances") Integer> keySet() {
        return nodeDistances.keySet();
    }

    /**
     * Returns a {@link Collection} of the distances present in this map.
     *
     * @return a {@link Collection} of the distances present in this map
     */
    public Collection<Integer> values() {
        return nodeDistances.values();
    }

    /**
     * Returns a {@link Set} of the node-distance entries in this map.
     *
     * @return a {@link Set} of the node-distance entries in this map
     */
    public Set<Map.Entry<@KeyFor("this.nodeDistances") Integer, Integer>> entrySet() {
        return nodeDistances.entrySet();
    }
}
