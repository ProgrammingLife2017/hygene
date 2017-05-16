package org.dnacronym.hygene.persistence;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a DB table.
 */
final class DatabaseTable {
    private final String name;
    private final List<Pair<String, String>> columns;


    DatabaseTable(String name) {
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
    List<Pair<String, String>> getColumns() {
        return columns;
    }

    /**
     * Adds the given column.
     *
     * @param name the name of the column
     * @param type the SQL type of it (e.g. integer, string)
     */
    void addColumn(final String name, final String type) {
        columns.add(new Pair<>(name, type));
    }
}
