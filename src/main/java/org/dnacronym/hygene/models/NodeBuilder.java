package org.dnacronym.hygene.models;

import org.dnacronym.hygene.core.UnsignedInteger;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;


/**
 * The node builder provides an easy way to construct a new node
 * based on parameters that are converted into a node array.
 */
@SuppressWarnings("PMD.TooManyMethods") // No reasonable refactor possible
public final class NodeBuilder {
    private int nodeId;
    private long byteOffset;
    private int sequenceLength;
    private NodeColor color = NodeColor.BLACK;
    private int unscaledXPosition = -1;
    private int unscaledYPosition = -1;
    private Set<Edge> incomingEdges = new TreeSet<>();
    private Set<Edge> outgoingEdges = new TreeSet<>();


    /**
     * Creates a new instance of the builder.
     *
     * @return a new instance of the builder
     */
    public static NodeBuilder start() {
        return new NodeBuilder();
    }

    /**
     * Creates a new instance of the builder from an existing node vector.
     *
     * @param nodeId    internal node ID
     * @param nodeArray array representation of a node
     * @return a new instance of the builder
     */
    public static NodeBuilder fromArray(final int nodeId, final int[] nodeArray) {
        final Node node = new Node(nodeId, nodeArray, null);

        final NodeBuilder builder = new NodeBuilder();
        builder.nodeId = nodeId;
        builder.byteOffset = node.getByteOffset();
        builder.sequenceLength = node.getSequenceLength();
        builder.color = node.getColor();
        builder.unscaledXPosition = node.getUnscaledXPosition();
        builder.unscaledYPosition = node.getUnscaledYPosition();
        builder.incomingEdges = node.getIncomingEdges();
        builder.outgoingEdges = node.getOutgoingEdges();

        return builder;
    }

    /**
     * Sets the node id for the {@link Node} under construction.
     *
     * @param nodeId the id of the node
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withNodeId(final int nodeId) {
        this.nodeId = nodeId;

        return this;
    }

    /**
     * Sets the byte offset for the {@link Node} under construction.
     *
     * @param byteOffset the byte offset of the node within the GFA file
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withByteOffset(final long byteOffset) {
        this.byteOffset = byteOffset;

        return this;
    }

    /**
     * Sets the sequence length for the {@link Node} under construction.
     *
     * @param sequenceLength the length of the sequence belonging to the node
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withSequenceLength(final int sequenceLength) {
        this.sequenceLength = sequenceLength;

        return this;
    }

    /**
     * Sets the color of the {@link Node} under construction.
     *
     * @param color the color of the node
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withColor(final NodeColor color) {
        this.color = color;

        return this;
    }

    /**
     * Sets the unscaled x position of the {@link Node} under construction.
     *
     * @param unscaledXPosition the unscaled x position of the node
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withUnscaledXPosition(final int unscaledXPosition) {
        this.unscaledXPosition = unscaledXPosition;

        return this;
    }

    /**
     * Sets the unscaled y position of the {@link Node} under construction.
     *
     * @param unscaledYPosition the unscaled y position of the node
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withUnscaledYPosition(final int unscaledYPosition) {
        this.unscaledYPosition = unscaledYPosition;

        return this;
    }

    /**
     * Adds a new incoming edge to the {@link Node} under construction.
     *
     * @param from       ID of the node where the edge is coming from
     * @param byteOffset line number of the edge in the GFA file
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withIncomingEdge(final int from, final int byteOffset) {
        incomingEdges.add(new Edge(from, nodeId, byteOffset, null));

        return this;
    }

    /**
     * Adds a new outgoing edge to the {@link Node} under construction.
     *
     * @param to         ID of the node where the edge is going to
     * @param byteOffset line number of the edge in the GFA file
     * @return current instance of the builder to provide a fluent interface
     */
    public NodeBuilder withOutgoingEdge(final int to, final int byteOffset) {
        outgoingEdges.add(new Edge(nodeId, to, byteOffset, null));

        return this;
    }

    /**
     * Creates an array representation of the currently known node details.
     *
     * @return array representation of the currently known node details
     */
    public int[] toArray() {
        final IntStream detailsArray = Arrays.stream(new int[] {
                UnsignedInteger.fromLong(byteOffset),
                sequenceLength,
                color.ordinal(),
                unscaledXPosition,
                unscaledYPosition,
                outgoingEdges.size()
        });
        final IntStream outgoingEdgesArray = outgoingEdges.stream().flatMapToInt(
                edge -> Arrays.stream(new int[] {edge.getTo(), UnsignedInteger.fromLong(edge.getByteOffset())}));
        final IntStream incomingEdgesArray = incomingEdges.stream().flatMapToInt(
                edge -> Arrays.stream(new int[] {edge.getFrom(), UnsignedInteger.fromLong(edge.getByteOffset())}));

        return IntStream.concat(detailsArray, IntStream.concat(outgoingEdgesArray, incomingEdgesArray)).toArray();
    }

    /**
     * Creates the node instance based on the given parameters.
     *
     * @return the node instance based on the given parameters
     */
    public Node create() {
        return new Node(nodeId, toArray(), null);
    }
}
