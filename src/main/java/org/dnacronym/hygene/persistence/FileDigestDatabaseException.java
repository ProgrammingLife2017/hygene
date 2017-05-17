package org.dnacronym.hygene.persistence;

import java.io.IOException;


/**
 * Exception class used to indicate mismatch between the stored hash of a file and its computed equivalent.
 */
public class FileDigestDatabaseException extends IOException {
    public FileDigestDatabaseException(String message) {
        super(message);
    }
}
