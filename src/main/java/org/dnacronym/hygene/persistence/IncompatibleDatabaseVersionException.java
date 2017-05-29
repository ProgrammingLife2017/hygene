package org.dnacronym.hygene.persistence;

import java.io.IOException;


/**
 * Exception class used to indicate database version incompatibility.
 */
public final class IncompatibleDatabaseVersionException extends IOException {
    /**
     * Constructs a new {@link IncompatibleDatabaseVersionException}.
     *
     * @param message the exception message
     */
    public IncompatibleDatabaseVersionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@link IncompatibleDatabaseVersionException}.
     *
     * @param message the exception message
     * @param cause   the exception's cause
     */
    public IncompatibleDatabaseVersionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
