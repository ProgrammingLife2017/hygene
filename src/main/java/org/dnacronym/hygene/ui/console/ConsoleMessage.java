package org.dnacronym.hygene.ui.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;


/**
 * This class is a generic wrapper representing console messages.
 */
public final class ConsoleMessage {
    private static final String GREEN_TEXT_STYLE_CLASS = "green";
    private static final String RED_TEXT_STYLE_CLASS = "red";
    private static final String BLUE_TEXT_STYLE_CLASS = "blue";
    private static final String YELLOW_TEXT_STYLE_CLASS = "yellow";

    private final String message;
    private String styleClass = GREEN_TEXT_STYLE_CLASS;


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
     * {@link LogEvent}.
     *
     * @param event the {@link LogEvent}
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
     * Gets the style class.
     *
     * @return the style class
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Returns a style class corresponding to a certain log level.
     *
     * @param level the log level represent by {@link Level}
     * @return the style class
     */
    static String getColor(final Level level) {
        if (level == null || level.toString() == null) {
            return GREEN_TEXT_STYLE_CLASS;
        }

        switch (level.toString()) {
            case "FATAL":
            case "ERROR":
            case "WARN":
                return RED_TEXT_STYLE_CLASS;
            case "DEBUG":
                return BLUE_TEXT_STYLE_CLASS;
            case "TRACE":
                return YELLOW_TEXT_STYLE_CLASS;
            case "INFO":
            default:
                return GREEN_TEXT_STYLE_CLASS;
        }
    }
}
