package org.dnacronym.hygene.persistence;

import java.io.IOException;


/**
 * Exception class used to indicate mismatch between the stored hash of a file and its computed equivalent.
 */
public class FileDigestDatabaseException extends IOException {
    /**
     * Constructs the exception instance.
     *
     * @param message the exception message
     */
    public FileDigestDatabaseException(final String message) {
        super(message);
    }
}
