package org.dnacronym.hygene.ui.console;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * This class is a generic wrapper representing console messages.
 */
public class ConsoleMessage {
    private final Text node;


    /**
     * Constructor for {@link ConsoleMessage}.
     *
     * @param message the message
     */
    public ConsoleMessage(final String message) {
        node = new Text(message);
    }

    /**
     * Constructor for {@link ConsoleMessage} used when creating a {@link ConsoleMessage} from a Log4J2
     * {@link LogEvent}. An {@link Appender} is passed such that he {@link LogEvent} can be properly parsed.
     *
     * @param appender the appender
     * @param event    the {@link LogEvent}
     */
    public ConsoleMessage(@Initialized @NonNull final Appender appender, @NonNull final LogEvent event) {
        this(new String(appender.getLayout().toByteArray(event), StandardCharsets.UTF_8));

        node.setFill(getColor(event.getLevel()));
    }

    /**
     * Gets the {@link Text} node.
     *
     * @return the {@link Text} node
     */
    public @NonNull Text getNode() {
        return node;
    }

    /**
     * Returns a JavaFX color corresponding to a certain log level.
     *
     * @param level the loglevel represent by {@link Level}
     * @return the {@link Color}
     */
    private static @Initialized @NonNull Color getColor(final Level level) {
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
                return Color.BLACK;
        }
    }
}
