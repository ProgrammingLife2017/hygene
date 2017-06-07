package org.dnacronym.hygene.events;

import org.dnacronym.hygene.ui.console.ConsoleMessage;


/**
 * This event represent the creation of a new {@link ConsoleMessage}.
 */
public final class ConsoleMessageEvent {
    private final ConsoleMessage consoleMessage;


    /**
     * Instantiates a new {@link ConsoleMessageEvent}.
     *
     * @param consoleMessage the {@link ConsoleMessage}
     */
    public ConsoleMessageEvent(final ConsoleMessage consoleMessage) {
        this.consoleMessage = consoleMessage;
    }


    /**
     * Gets the {@link ConsoleMessage}.
     *
     * @return the console message
     */
    public ConsoleMessage getConsoleMessage() {
        return consoleMessage;
    }
}
