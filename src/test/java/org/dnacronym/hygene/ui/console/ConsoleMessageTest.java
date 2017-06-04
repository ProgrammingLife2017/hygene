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
 * Units tests for {@link ConsoleMessage}
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
    void testCreateConsoleMessageLog4jEvent() {
        when(logEvent.getMessage().getFormattedMessage()).thenReturn("Error Message!");
        when(logEvent.getLevel()).thenReturn(Level.getLevel("ERROR"));

        final ConsoleMessage consoleMessage = new ConsoleMessage(logEvent);

        assertThat(consoleMessage.getMessage()).isEqualTo("Error Message!");
        assertThat(consoleMessage.getStyleClass()).isEqualTo("red");
    }
}
