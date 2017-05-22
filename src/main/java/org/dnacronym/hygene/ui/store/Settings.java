package org.dnacronym.hygene.ui.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Settings class. This stores a bunch of commands.
 */
public final class Settings {
    private final ObservableList<Runnable> commands;


    /**
     * Create new {@link Settings} instance.
     */
    public Settings() {
        commands = FXCollections.observableArrayList();
    }


    /**
     * Add an action to the list of commands.
     *
     * @param runnable action to execute
     */
    public void addRunnable(final Runnable runnable) {
        commands.add(runnable);
    }

    /**
     * Execute all actions in queue, and clear the actions afterwards.
     */
    public void executeAll() {
        for (final Runnable runnable : commands) {
            runnable.run();
        }
        clearAll();
    }

    /**
     * Clear all actions in the queue without executing them.
     */
    public void clearAll() {
        commands.clear();
    }

    /**
     * Get the current {@link ObservableList} of commands.
     *
     * @return current {@link ObservableList} of commands
     */
    public ObservableList<Runnable> getCommands() {
        return commands;
    }
}
