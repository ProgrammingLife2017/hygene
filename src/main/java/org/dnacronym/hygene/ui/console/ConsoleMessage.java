package org.dnacronym.hygene.ui.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;

import java.nio.charset.StandardCharsets;


/**
 * This class is a generic wrapper representing console messages.
 */
public final class ConsoleMessage {
    private final String message;
    private String styleClass = "green";


    /**
     * Constructor for {@link ConsoleMessage}.
     *
     * @param message the message
     */
    public ConsoleMessage(final String message) {
        this.message = message;
    }

    /**
     * Constructor for {@link ConsoleMessage} used when creating a {@link ConsoleMessage} from a Log4J2
     * {@link LogEvent}. An {@link Appender} is passed such that the {@link LogEvent} can be properly parsed.
     *
     * @param appender the appender
     * @param event    the {@link LogEvent}
     */
    public ConsoleMessage(final LogEvent event) {
        this(event.getMessage().getFormattedMessage());
        styleClass = getColor(event.getLevel());
    }

    /**
     * Gets the log message.
     *
     * @return the log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets style class.
     *
     * @return the style class
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Returns a styleclass corresponding to a certain log level.
     *
     * @param level the loglevel represent by {@link Level}
     * @return the styleclass
     */
    private static String getColor(final Level level) {
        if (level == null || level.toString() == null) {
            return "default";
        }

        switch (level.toString()) {
            case "FATAL":
            case "ERROR":
            case "WARN":
                return "red";
            case "DEBUG":
                return "blue";
            case "TRACE":
                return "yellow";
            case "INFO;":
            default:
                return "green";
        }
    }
}
