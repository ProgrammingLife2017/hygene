package org.dnacronym.hygene.graph;


/**
 * The directions in which a sequence can be considered.
 * <p>
 * This enum is applicable at multiple levels: It may be the direction an iterator goes in, it may be the
 * direction of which the neighbours of a node are considered, etc.
 */
public enum SequenceDirection {
    /**
     * From left to right.
     */
    RIGHT,
    /**
     * From right to left.
     */
    LEFT;


    /**
     * Returns parameter {@code left} if this {@link SequenceDirection} is {@link #LEFT} and returns parameter {@code
     * right} if this {@link SequenceDirection} is {@link #RIGHT}.
     *
     * @param left  the value to return if this {@link SequenceDirection} is {@link #LEFT}
     * @param right the value to return if this {@link SequenceDirection} is {@link #RIGHT}
     * @param <T>   the type of the given values
     * @return parameter {@code left} if this {@link SequenceDirection} is {@link #LEFT} and returns parameter {@code
     * right} if this {@link SequenceDirection} is {@link #RIGHT}.
     */
    public <T> T ternary(final T left, final T right) {
        switch (this) {
            case LEFT:
                return left;
            case RIGHT:
                return right;
            default:
                throw new IllegalArgumentException("Unknown enum value.");
        }
    }

    /**
     * Runs the {@link Runnable} {@code left} if this {@link SequenceDirection} is {@link #LEFT} and runs {@link
     * Runnable} {@code right} if this {@link SequenceDirection} is {@link #RIGHT}.
     *
     * @param left  the {@link Runnable} to run if this {@link SequenceDirection} is {@link #LEFT}
     * @param right the {@link Runnable} to run if this {@link SequenceDirection} is {@link #RIGHT}
     */
    public void ternary(final Runnable left, final Runnable right) {
        switch (this) {
            case LEFT:
                left.run();
                break;
            case RIGHT:
                right.run();
                break;
            default:
                throw new IllegalArgumentException("Unknown enum value.");
        }
    }

    /**
     * Returns the opposite direction.
     *
     * @return the opposite direction
     */
    public SequenceDirection opposite() {
        return this.ternary(RIGHT, LEFT);
    }
}
