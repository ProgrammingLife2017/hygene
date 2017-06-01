package org.dnacronym.hygene.parser;


/**
 * Provides a functional interface for giving parsing progress to interested parties.
 */
@FunctionalInterface
public interface ProgressUpdater {
    /**
     * Updates the interested party with a new progress percentage.
     *
     * @param progress progress indication between 0 and 100
     */
    void updateProgress(int progress, String message);
}
