package org.dnacronym.hygene.ui.console;

import javafx.scene.paint.Color;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;

import java.nio.charset.StandardCharsets;


/**
 * This class is a generic wrapper representing console messages.
 */
public final class ConsoleMessage {
    private final String message;
    private String styleClass;


    /**
     * Constructor for {@link ConsoleMessage}.
     *
     * @param message the message
     */
    public ConsoleMessage(final String message) {
        this.message = message;
        this.styleClass = "green";
    }

    /**
     * Constructor for {@link ConsoleMessage} used when creating a {@link ConsoleMessage} from a Log4J2
     * {@link LogEvent}. An {@link Appender} is passed such that the {@link LogEvent} can be properly parsed.
     *
     * @param appender the appender
     * @param event    the {@link LogEvent}
     */
    public ConsoleMessage(final Appender appender, final LogEvent event) {
        this(new String(appender.getLayout().toByteArray(event), StandardCharsets.UTF_8));
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
     * Returns a JavaFX color corresponding to a certain log level.
     *
     * @param level the loglevel represent by {@link Level}
     * @return the {@link Color}
     */
    private static Color getColor(final Level level) {
        if (level == null || level.toString() == null) {
            return Color.BLACK;
        }

        switch (level.toString()) {
            case "FATAL":
            case "ERROR":
            case "WARN":
                return Color.RED;
            case "DEBUG":
                return Color.BLUE;
            case "TRACE":
                return Color.GREEN;
            case "INFO;":
            default:
                return Color.LIGHTGRAY;
        }
    }
}
