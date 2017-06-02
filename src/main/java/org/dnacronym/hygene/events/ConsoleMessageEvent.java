package org.dnacronym.hygene.events;

import org.dnacronym.hygene.ui.console.ConsoleMessage;


/**
 * This event represent the creation of a new {@link ConsoleMessage}.
 */
public final class ConsoleMessageEvent {
    private final ConsoleMessage consoleMessage;

    /**
     * Instantiates a new Console Message Event.
     *
     * @param consoleMessage the {@link ConsoleMessage}
     */
    public ConsoleMessageEvent(final ConsoleMessage consoleMessage) {
        this.consoleMessage = consoleMessage;
    }

    /**
     * Gets console message.
     *
     * @return the console message
     */
    public ConsoleMessage getConsoleMessage() {
        return consoleMessage;
    }
}
