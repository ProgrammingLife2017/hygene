package org.dnacronym.hygene.ui.store;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Settings class. This stores a bunch of commands.
 */
public final class Settings {
    private final ObservableList<Runnable> commands;


    public Settings() {
        commands = FXCollections.observableArrayList();
    }

    public void addCallable(Runnable runnable) {
        commands.add(runnable);
    }

    public void executeAll() {
        for (Runnable runnable : commands) {
            runnable.run();
        }
        clearAll();
    }

    public void clearAll() {
        commands.clear();
    }

    public ObservableList<Runnable> getCommands() {
        return commands;
    }
}
