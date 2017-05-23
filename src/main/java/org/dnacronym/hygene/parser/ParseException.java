package org.dnacronym.hygene.parser;


/**
 * Indicates that an error has occurred during the parsing of a text.
 */
public final class ParseException extends Exception {
    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link Throwable#initCause(java.lang.Throwable)}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link
     *                Throwable#getMessage()} method.
     */
    public ParseException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with cause is not automatically incorporated in this exception's detail
     * message.
     *
     * @param cause   the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()}
     *                method).
     * @param message the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
     *                {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
