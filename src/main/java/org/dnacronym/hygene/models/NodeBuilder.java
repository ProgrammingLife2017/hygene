package org.dnacronym.hygene.models;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 * The node builder provides an easy way to construct a new node
 * based on parameters that are converted into a node array.
 */
public final class NodeBuilder {
    private int nodeId = 0;
    private int lineNumber = 0;
    private NodeColor color = NodeColor.BLACK;
    private int unscaledXPosition = 0;
    private int unscaledYPosition = 0;
    private Set<Edge> incomingEdges = new TreeSet<>();
    private Set<Edge> outgoingEdges = new TreeSet<>();

    /**
     * Creates a new instance of the builder.
     *
     * @return a new instance of the builder.
     */
    public static NodeBuilder start() {
        return new NodeBuilder();
    }

    /**
     * Creates a new instance of the builder from an existing node vector.
     *
     * @return a new instance of the builder.
     */
    public static NodeBuilder fromArray(int nodeId, int[] nodeVector) {
        Node node = new Node(nodeId, nodeVector);

        NodeBuilder builder = new NodeBuilder();
        builder.nodeId = nodeId;
        builder.lineNumber = node.getLineNumber();
        builder.color = node.getColor();
        builder.unscaledXPosition = node.getUnscaledXPosition();
        builder.unscaledYPosition = node.getUnscaledYPosition();
        builder.incomingEdges = node.getIncomingEdges();
        builder.outgoingEdges = node.getOutgoingEdges();

        return builder;
    }

    /**
     * Sets the node id for the {@code Node} under construction.
     *
     * @param nodeId the id of the node
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withNodeId(final int nodeId) {
        this.nodeId = nodeId;

        return this;
    }

    /**
     * Sets the line number for the {@code Node} under construction.
     *
     * @param lineNumber the line number of the GFA file
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;

        return this;
    }

    /**
     * Sets the color of the {@code Node} under construction.
     *
     * @param color the color of the node
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withColor(final NodeColor color) {
        this.color = color;

        return this;
    }

    /**
     * Sets the unscaled y position of the {@code Node} under construction.
     *
     * @param unscaledYPosition the unscaled y position of the node
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withUnscaledYPosition(final int unscaledYPosition) {
        this.unscaledYPosition = unscaledYPosition;

        return this;
    }

    /**
     * Sets the unscaled x position of the {@code Node} under construction.
     *
     * @param unscaledXPosition the unscaled y position of the node
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withUnscaledXPosition(final int unscaledXPosition) {
        this.unscaledXPosition = unscaledXPosition;

        return this;
    }

    /**
     * Adds a new incoming edge to the {@code Node} under construction.
     *
     * @param from ID of the node where the edge is coming from
     * @param lineNumber line number of the edge in the GFA file
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withIncomingEdge(final int from, final int lineNumber) {
        incomingEdges.add(new Edge(from, nodeId, lineNumber));

        return this;
    }

    /**
     * Adds a new outgoing edge to the {@code Node} under construction.
     *
     * @param to ID of the node where the edge is going to
     * @param lineNumber line number of the edge in the GFA file
     * @return current instance of the builder to provide a fluent interface.
     */
    public NodeBuilder withOutgoingEdge(final int to, final int lineNumber) {
        outgoingEdges.add(new Edge(nodeId, to, lineNumber));

        return this;
    }

    /**
     * Creates an array representation of the currently known node details.
     *
     * @return array representation of the currently known node details.
     */
    public int[] toArray() {
        final IntStream detailsArray = Arrays.stream(new int[]{
                lineNumber,
                color.ordinal(),
                unscaledXPosition,
                unscaledYPosition,
                outgoingEdges.size()
        });
        final IntStream outgoingEdgesArray = outgoingEdges.stream()
                .flatMapToInt(edge -> Arrays.stream(new int[]{edge.getTo(), edge.getLineNumber()}));
        final IntStream incomingEdgesArray = incomingEdges.stream()
                .flatMapToInt(edge -> Arrays.stream(new int[]{edge.getFrom(), edge.getLineNumber()}));

        return IntStream.concat(detailsArray, IntStream.concat(outgoingEdgesArray, incomingEdgesArray)).toArray();
    }

    /**
     * Creates the node instance based on the given parameters.
     *
     * @return the node instance based on the given parameters.
     */
    public Node create() {
        return new Node(nodeId, toArray());
    }
}
