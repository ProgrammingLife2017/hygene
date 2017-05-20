package org.dnacronym.hygene.ui.store;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Settings class. This stores a bunch of commands.
 */
public final class Settings {
    private final ObservableList<Runnable> commands;


    /**
     * Create new settings instance.
     */
    public Settings() {
        commands = FXCollections.observableArrayList();
    }


    /**
     * Add an action to the queue.
     *
     * @param runnable action to execute
     */
    public void addCallable(final Runnable runnable) {
        commands.add(runnable);
    }

    /**
     * Execute all actions in queue, and clear the actions afterwards.
     */
    public void executeAll() {
        for (Runnable runnable : commands) {
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
