package org.dnacronym.hygene.persistence;

import java.io.IOException;


/**
 * Exception class used to indicate database version incompatibility.
 */
public class IncompatibleDatabaseVersionException extends IOException {
    /**
     * Constructs the exception instance.
     *
     * @param message the exception message
     */
    public IncompatibleDatabaseVersionException(final String message) {
        super(message);
    }
}
