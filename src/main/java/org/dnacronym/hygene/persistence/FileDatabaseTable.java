package org.dnacronym.hygene.persistence;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a database table.
 */
final class FileDatabaseTable {
    private final String name;
    private final List<Pair<String, ColumnType>> columns;


    /**
     * Constructs a new {@link FileDatabaseTable} instance.
     *
     * @param name the name of the table this instance should correspond to
     */
    FileDatabaseTable(final String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }


    /**
     * Returns the name of the table.
     *
     * @return the name
     */
    String getName() {
        return name;
    }

    /**
     * Returns the columns.
     *
     * @return the columns
     */
    List<Pair<String, ColumnType>> getColumns() {
        return columns;
    }

    /**
     * Adds a new column with given name and type to the table.
     *
     * @param name the name of the column
     * @param type the SQLite type of it
     */
    void addColumn(final String name, final ColumnType type) {
        columns.add(new Pair<>(name, type));
    }
}
