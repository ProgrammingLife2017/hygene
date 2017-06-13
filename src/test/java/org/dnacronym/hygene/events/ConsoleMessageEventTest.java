package org.dnacronym.hygene.events;

import org.dnacronym.hygene.ui.console.ConsoleMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Unit test for {@link ConsoleMessageEvent}.
 */
class ConsoleMessageEventTest {
    @Test
    void getConsoleMessage() {
        final ConsoleMessageEvent event = new ConsoleMessageEvent(new ConsoleMessage("Sample Message"));

        assertThat(event.getConsoleMessage().getMessage()).isEqualTo("Sample Message");
    }
}
