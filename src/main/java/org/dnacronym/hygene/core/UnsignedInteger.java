package org.dnacronym.hygene.core;


/**
 * Represents an 32 bit unsigned integer, allowing to store numbers between 0 and 2^32.
 */
public final class UnsignedInteger {
    /**
     * The maximum value of an {@link UnsignedInteger}, 2 * max integer value + 1.
     */
    public static final long MAX_VALUE = 2L * Integer.MAX_VALUE + 1;


    /**
     * Hides public implicit constructor.
     */
    private UnsignedInteger() {
    }


    /**
     * Converts a normal non-negative long to a regularized integer.
     *
     * @param value a non-negative long
     * @return a regularized integer between {@code Integer.MIN_VALUE} and {@code Integer.MAX_VALUE}
     */
    public static int fromLong(final long value) {
        if (value < 0) {
            throw new IllegalArgumentException("A negative number cannot be converted to an unsigned integer.");
        }
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Number cannot be higher than " + MAX_VALUE + ".");
        }
        return (int) value + Integer.MIN_VALUE;
    }

    /**
     * Converts a regularized integer to a non-negative long.
     *
     * @param regularizedInteger a regularized integer between {@code Integer.MIN_VALUE} and {@code Integer.MAX_VALUE}
     * @return a non-negative long
     */
    public static long toLong(final int regularizedInteger) {
        return (long) regularizedInteger - Integer.MIN_VALUE;
    }
}
