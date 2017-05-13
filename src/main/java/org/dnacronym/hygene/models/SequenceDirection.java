package org.dnacronym.hygene.models;


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
     * Returns parameter {@code left} if this {@code SequenceDirection} is {@code LEFT} and returns parameter {@code
     * right} if this {@code SequenceDirection} is {@code RIGHT}.
     *
     * @param left  the value to return if this {@code SequenceDirection} is {@code LEFT}
     * @param right the value to return if this {@code SequenceDirection} is {@code RIGHT}
     * @param <T>   the type of the given values
     * @return parameter {@code left} if this {@code SequenceDirection} is {@code LEFT} and returns parameter {@code
     * right} if this {@code SequenceDirection} is {@code RIGHT}.
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
     * Runs the {@code Runnable} {@code left} if this {@code SequenceDirection} is {@code LEFT} and runs {@code
     * Runnable} {@code right} if this {@code SequenceDirection} is {@code RIGHT}.
     *
     * @param left  the {@code Runnable} to run if this {@code SequenceDirection} is {@code LEFT}
     * @param right the {@code Runnable} to run if this {@code SequenceDirection} is {@code RIGHT}
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
     * @return the opposite direction.
     */
    public SequenceDirection opposite() {
        return this.ternary(RIGHT, LEFT);
    }
}
