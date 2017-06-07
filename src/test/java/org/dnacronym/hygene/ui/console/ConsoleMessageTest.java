package org.dnacronym.hygene.ui.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Units tests for {@link ConsoleMessage}.
 */
class ConsoleMessageTest {
    private ConsoleMessage consoleMessage;
    private LogEvent logEvent;
    private Message message;


    @BeforeEach
    void setUp() {
        consoleMessage = new ConsoleMessage("The message");
        logEvent = mock(LogEvent.class);
        message = mock(Message.class);
        when(logEvent.getMessage()).thenReturn(message);
    }


    @Test
    void testGetMessage() {
        assertThat(consoleMessage.getMessage()).isEqualTo("The message");
    }

    @Test
    void testGetStyleClass() {
        assertThat(consoleMessage.getStyleClass()).isEqualTo("green");
    }

    @Test
    void testCreateConsoleMessageLog4jEventERROR() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Error Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("ERROR"));

        consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Error Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("red");
    }

    @Test
    void testCreateConsoleMessageLog4jEventFATAL() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Fatal Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("FATAL"));

        consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Fatal Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("red");
    }

    @Test
    void testCreateConsoleMessageLog4jEventWARN() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Warning Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("WARN"));

        consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Warning Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("red");
    }

    @Test
    void testCreateConsoleMessageLog4jEventINFO() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Info Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("INFO"));

        consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Info Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("green");
    }

    @Test
    void testCreateConsoleMessageLog4jEventDEBUG() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Debug Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("DEBUG"));

        consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Debug Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("blue");
    }

    @Test
    void testCreateConsoleMessageLog4jEventTRACE() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Trace Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("TRACE"));

        consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Trace Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("yellow");
    }

    @Test
    void testCreateConsoleMessageNullCase() {
        assertThat(ConsoleMessage.getColor(null)).isEqualTo("green");
    }

    @Test
    void testCreateConsoleMessageToStringNullCase() {
        Level level = mock(Level.class);
        when(level.toString()).thenReturn(null);

        assertThat(ConsoleMessage.getColor(level)).isEqualTo("green");
    }
}
