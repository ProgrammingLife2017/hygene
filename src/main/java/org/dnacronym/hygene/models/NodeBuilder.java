package org.dnacronym.hygene.models;


/**
 * The node builder provides an easy way to construct a new node
 * based on parameters that are converted into a node array.
 */
public final class NodeBuilder {
    private int nodeId = 0;
    private int lineNumber = 0;
    private NodeColor color = NodeColor.RED;
    private int unscaledXPosition = 0;
    private int unscaledYPosition = 0;

    /**
     * Creates a new instance of the builder.
     *
     * @return a new instance of the builder.
     */
    public static NodeBuilder start() {
        return new NodeBuilder();
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
     * Creates an array representation of the currently known node details.
     *
     * @return array representation of the currently known node details.
     */
    public int[] toArray() {
        return new int[]{
                lineNumber,
                color.ordinal(),
                unscaledXPosition,
                unscaledYPosition,
                0 // outgoing edges size, will be added later, at the moment we don't have
        };
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
