package org.dnacronym.hygene.persistence;

import java.io.IOException;


/**
 * Exception class used to indicate database version incompatibility.
 */
public class IncompatibleDatabaseVersionException extends IOException {
    public IncompatibleDatabaseVersionException(String message) {
        super(message);
    }
}
