package org.dnacronym.hygene.parser;


/**
 * Provides a functional interface for giving parsing progress to interested parties.
 */
@FunctionalInterface
@SuppressWarnings({"squid:S1214", "squid:S00108"}) // Using a constant is fine here and we want the lambda to be empty
public interface ProgressUpdater {
    ProgressUpdater DUMMY = (progress, message) -> {
    };

    /**
     * Updates the interested party with a new progress percentage.
     *
     * @param progress progress indication between 0 and 100
     * @param message a message indicating the current progress stage
     */
    void updateProgress(int progress, String message);
}
