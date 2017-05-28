package org.dnacronym.hygene.ui.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dnacronym.hygene.ui.graph.GraphStore;


/**
 * Settings class.
 * <p>
 * Stores a collection of {@link Runnable}s that can either be executed or discarded.
 */
public final class Settings {
    private final ObservableList<Runnable> commands;
    private final GraphStore graphStore;


    /**
     * Create new {@link Settings} instance.
     *
     * @param graphStore {@link GraphStore} whose {@link org.dnacronym.hygene.parser.GfaFile} will be observed
     */
    public Settings(final GraphStore graphStore) {
        this.graphStore = graphStore;

        commands = FXCollections.observableArrayList();
    }


    /**
     * Add an action to the list of commands.
     *
     * @param runnable action to execute
     */
    void addRunnable(final Runnable runnable) {
        commands.add(runnable);
    }

    /**
     * Execute all actions in queue, and clear the actions afterwards.
     * <p>
     * If the {@link GraphStore} has no {@link org.dnacronym.hygene.parser.GfaFile}, this method does not execute any
     * {@link Runnable}.
     */
    void executeAll() {
        if (graphStore.getGfaFileProperty().isNotNull().get()) {
            for (final Runnable runnable : commands) {
                runnable.run();
            }
        }

        clearAll();
    }

    /**
     * Clear all actions in the queue without executing them.
     */
    void clearAll() {
        commands.clear();
    }

    /**
     * Get the current {@link ObservableList} of commands.
     *
     * @return current {@link ObservableList} of commands
     */
    ObservableList<Runnable> getCommands() {
        return commands;
    }
}
