package org.dnacronym.insertproductname.ui;

/**
 * Indicates that an error has occurred during the initialisation of the UI.
 */
public class UIInitialisationException extends Exception {
    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@code Throwable.initCause(java.lang.Throwable)}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *                {@code Throwable.getMessage()} method.
     */
    public UIInitialisationException(final String message) {
        super(message);
    }
}
