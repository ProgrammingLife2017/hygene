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
    LEFT
}
