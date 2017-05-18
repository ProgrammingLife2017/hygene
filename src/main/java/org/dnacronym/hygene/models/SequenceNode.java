package org.dnacronym.hygene.models;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@code SequenceNode} represents a node in a DNA Sequence Alignment Graph.
 * <p>
 * Each node contains a base-sequence, held in the {@code sequence} field. Each node also is associated with one or more
 * reads (runs of DNA analysis). Finally, each node is linked with its adjacent nodes, either backward or forward (left
 * or right).
 */
public final class SequenceNode {
    private final String id;
    private final String sequence;
    private final List<String> readIdentifiers;
    private final List<SequenceNode> leftNeighbours;
    private final List<SequenceNode> rightNeighbours;

    private int horizontalRightEnd = -1;
    private int verticalPosition = -1;

    private int leftHeight = -1;
    private int rightHeight = -1;

    private boolean visited;


    /**
     * Constructs a new {@code SequenceNode}, with empty lists of read-IDs and adjacent nodes.
     *
     * @param id       the ID of this sequence segment
     * @param sequence the sequence of bases this node contains
     */
    public SequenceNode(final String id, final String sequence) {
        this.id = id;
        this.sequence = sequence;
        this.readIdentifiers = new ArrayList<>();
        this.leftNeighbours = new ArrayList<>();
        this.rightNeighbours = new ArrayList<>();
    }


    /**
     * Returns the ID.
     *
     * @return the ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the sequence.
     *
     * @return the sequence.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Returns the sequence length.
     * <p>
     * The size of a node is determined as the length of the sequence string.
     * <p>
     * This method is preferred over calling {@code getSequence().length()} because the definition of a node's length
     * may be changed in future releases.
     *
     * @return the sequence length.
     */
    public int getLength() {
        return sequence.length();
    }

    /**
     * Returns the read identifiers.
     *
     * @return the read identifiers.
     */
    public List<String> getReadIdentifiers() {
        return readIdentifiers;
    }

    /**
     * Adds the given {@code identifier} string to the list of read identifiers of this node.
     *
     * @param identifier the identifier to be added
     */
    public void addReadIdentifier(final String identifier) {
        readIdentifiers.add(identifier);
    }

    /**
     * Returns the left neighbours.
     *
     * @return the left neighbours.
     */
    public List<SequenceNode> getLeftNeighbours() {
        return leftNeighbours;
    }

    /**
     * Returns true iff. the node has 1 or more left neighbours.
     *
     * @return true iff. the node has 1 or more left neighbours.
     */
    public boolean hasLeftNeighbours() {
        return !leftNeighbours.isEmpty();
    }

    /**
     * Adds the given {@code sequenceNode} to the list of left neighbours.
     *
     * @param sequenceNode the node to be added as a left neighbour
     */
    public void addLeftNeighbour(final SequenceNode sequenceNode) {
        leftNeighbours.add(sequenceNode);
    }

    /**
     * Adds the given {@code sequenceNode} to the list of left neighbours and creates a link back from that node.
     *
     * @param sequenceNode the node to be linked on the left side
     */
    public void linkToLeftNeighbour(final SequenceNode sequenceNode) {
        addLeftNeighbour(sequenceNode);
        sequenceNode.addRightNeighbour(this);
    }

    /**
     * Returns the right neighbours.
     *
     * @return the right neighbours.
     */
    public List<SequenceNode> getRightNeighbours() {
        return rightNeighbours;
    }

    /**
     * Returns true iff. the node has 1 or more right neighbours.
     *
     * @return true iff. the node has 1 or more right neighbours.
     */
    public boolean hasRightNeighbours() {
        return !rightNeighbours.isEmpty();
    }

    /**
     * Adds the given {@code sequenceNode} to the list of right neighbours.
     *
     * @param sequenceNode the node to be added as a right neighbour
     */
    public void addRightNeighbour(final SequenceNode sequenceNode) {
        rightNeighbours.add(sequenceNode);
    }

    /**
     * Adds the given {@code sequenceNode} to the list of right neighbours and creates a link back from that node.
     *
     * @param sequenceNode the node to be linked on the right side
     */
    public void linkToRightNeighbour(final SequenceNode sequenceNode) {
        addRightNeighbour(sequenceNode);
        sequenceNode.addLeftNeighbour(this);
    }

    /**
     * Returns horizontal position of the right end of the node as calculated by FAFOSP.
     *
     * @return horizontal position of the right end of the node as calculated by FAFOSP.
     */
    public int getHorizontalRightEnd() {
        return horizontalRightEnd;
    }

    /**
     * Sets the horizontal position of the right end of the node.
     *
     * @param horizontalRightEnd the horizontal position of the right end of the node
     */
    public void setHorizontalRightEnd(final int horizontalRightEnd) {
        this.horizontalRightEnd = horizontalRightEnd;
    }

    /**
     * Returns the vertical position of the centre of the node as calculated by FAFOSP.
     *
     * @return vertical position of the centre of the node as calculated by FAFOSP.
     */
    public int getVerticalPosition() {
        return verticalPosition;
    }

    /**
     * Check if a given horizontal position is in bounds of node, and that the vertical position of node is equal to the
     * given vertical position.
     *
     * @param targetHorizontalPosition position that should be in bounds
     * @param targetVerticalPosition   vertical position node should be in
     * @return true iff in bounds.
     */
    public boolean inBounds(final int targetHorizontalPosition, final int targetVerticalPosition) {
        return targetVerticalPosition == this.verticalPosition
                && targetHorizontalPosition < this.horizontalRightEnd
                && targetHorizontalPosition >= this.horizontalRightEnd - this.sequence.length();
    }

    /**
     * Returns the sum of left heights of its left neighbours.
     * <p>
     * This method has a complexity of O(1) as it returns a precomputed value.
     *
     * @return the sum of left heights of its left neighbours.
     */
    int getLeftHeight() {
        return leftHeight;
    }

    /**
     * Returns the sum of right heights of its right neighbours.
     * <p>
     * This method has a complexity of O(1) as it returns a precomputed value.
     *
     * @return the sum of right heights of its right neighbours.
     */
    int getRightHeight() {
        return rightHeight;
    }


    /**
     * Calculates the optimal horizontal position for this {@code SequenceNode} relative to its left neighbours using
     * FAFOSP.
     */
    void fafospX() {
        int width = 0;
        for (final SequenceNode neighbour : leftNeighbours) {
            if (neighbour.horizontalRightEnd < 0) {
                return;
            }
            final int newWidth = neighbour.horizontalRightEnd + 1;
            if (newWidth > width) {
                width = newWidth;
            }
        }

        horizontalRightEnd = width + getLength();
    }

    /**
     * Calculates the {@code leftHeight} or {@code rightHeight}, depending on the indicated direction.
     * <p>
     * Put simply, the height is defined as the sum of heights of the neighbours, until it is "reset" when there is
     * only one neighbour and this neighbour has multiple neighbours in the opposite direction.
     * <p>
     * Put complexly, the following rules are applied:
     * <ul>
     * <li> If there are no neighbours, the node is a sentinel node and its height is the default of two.
     * <li> If there is a single neighbour and this neighbour only has a single neighbour in the opposite direction,
     * the height of the neighbour is copied.
     * <li> If there is a single neighbour and this neighbour has multiple neighbours, the height is set to the
     * default of two.
     * <li> If there are multiple neighbours, the sum of their heights in the same direction is taken.
     * </ul>
     *
     * @param direction which height to calculate
     */
    void fafospYInit(final SequenceDirection direction) {
        final List<SequenceNode> neighbours = direction.ternary(getLeftNeighbours(), getRightNeighbours());
        final int neighbourSize = neighbours.size();

        final int height;
        if (neighbourSize == 0) {
            height = 2;
        } else if (neighbourSize == 1) {
            final SequenceNode neighbour = neighbours.get(0);
            final int neighbourNeighbourSize
                    = direction.ternary(neighbour.getRightNeighbours(), neighbour.getLeftNeighbours()).size();

            if (neighbourNeighbourSize == 1) {
                height = direction.ternary(neighbour.leftHeight, neighbour.rightHeight);
            } else {
                height = 2;
            }
        } else {
            height = neighbours.stream()
                    .mapToInt(node -> direction.ternary(node.getLeftHeight(), node.getRightHeight()))
                    .sum();
        }

        direction.ternary(
                () -> leftHeight = height,
                () -> rightHeight = height);
    }

    /**
     * Calculates the vertical position for the right neighbour(s) of this {@code SequenceNode}.
     *
     * @param defaultPosition the position a node should be placed at by default
     */
    void fafospYCalculate(final int defaultPosition) {
        if (verticalPosition < 0) {
            verticalPosition = defaultPosition;
        }

        if (rightNeighbours.size() == 1) {
            final SequenceNode neighbour = rightNeighbours.get(0);
            fafospYCalculate(neighbour);
        } else {
            fafospYCalculate(rightNeighbours);
        }
    }

    /**
     * Calculates the vertical position for the single right neighbour of this {@code SequenceNode}.
     *
     * @param neighbour the neighbour of whom the height should be calculated
     */
    private void fafospYCalculate(final SequenceNode neighbour) {
        final List<SequenceNode> neighbourLeftNeighbours = neighbour.getLeftNeighbours();

        if (neighbourLeftNeighbours.size() == 1) {
            // This node and the neighbour are each other's only neighbour.
            neighbour.verticalPosition = this.verticalPosition;
        } else {
            // Neighbour has multiple neighbours
            int neighbourLeftNeighboursHeight = 0;

            for (final SequenceNode neighbourLeftNeighbour : neighbourLeftNeighbours) {
                if (neighbourLeftNeighbour.getId().equals(this.id)) {
                    break;
                }

                neighbourLeftNeighboursHeight += neighbourLeftNeighbour.leftHeight;
            }

            // Calculate the top of the neighbour's left height range
            final int neighbourTop = this.verticalPosition - neighbourLeftNeighboursHeight;
            // Compensate for differences in height
            final int leftDifference = neighbour.leftHeight / 2 - this.leftHeight / 2;

            neighbour.verticalPosition = neighbourTop + leftDifference;
        }
    }

    /**
     * Calculates the vertical positions for each right neighbour of this {@code SequenceNode}.
     *
     * @param neighbours the neighbours of whom the height should be calculated
     */
    private void fafospYCalculate(final List<SequenceNode> neighbours) {
        int relativeHeight = verticalPosition - rightHeight / 2;

        for (final SequenceNode neighbour : neighbours) {
            final List<SequenceNode> neighbourLeftNeighbours = neighbour.getLeftNeighbours();

            if (neighbourLeftNeighbours.size() == 1) {
                // Current neighbour has only one neighbour
                if (neighbour.verticalPosition >= 0) {
                    relativeHeight = neighbour.verticalPosition + neighbour.rightHeight / 2;
                } else {
                    neighbour.verticalPosition = relativeHeight + neighbour.rightHeight / 2;
                    relativeHeight += neighbour.rightHeight;
                }
            } else {
                // Current neighbour has multiple neighbours, so the left width is different from the current node
                if (neighbour.verticalPosition >= 0) {
                    relativeHeight = neighbour.verticalPosition + neighbour.leftHeight / 2;
                } else {
                    neighbour.verticalPosition = relativeHeight + neighbour.leftHeight / 2;
                    relativeHeight += neighbour.leftHeight;
                }
            }
        }
    }

    /**
     * Returns the visited.
     *
     * @return the visited.
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Sets the visited.
     *
     * @param visited whether the node was visited
     */
    public void setVisited(final boolean visited) {
        this.visited = visited;
    }
}