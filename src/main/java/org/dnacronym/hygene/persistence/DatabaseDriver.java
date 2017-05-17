package org.dnacronym.hygene.persistence;

import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class responsible for reading and writing from the file-database.
 */
final class DatabaseDriver {
    static final String DB_FILE_EXTENSION = ".db";

    private final Connection connection;


    /**
     * Constructs a DatabaseDriver.
     *
     * @param fileName the file name of the corresponding GFA-file
     * @throws SQLException in the case of erroneous SQL behaviour
     */
    DatabaseDriver(final String fileName) throws SQLException {
        final boolean databaseAlreadyExisted = (new File(fileName + DB_FILE_EXTENSION)).exists();
        connection = DriverManager.getConnection("jdbc:sqlite:" + fileName + DB_FILE_EXTENSION);
    }


    /**
     * Creates the given tables in the database.
     *
     * @param table the table to be set up
     * @throws SQLException in the case of erroneous SQL behaviour
     */
    void setupTable(final DatabaseTable table) throws SQLException {
        final Statement statement = connection.createStatement();

        final String columnList = String.join(", ", table.getColumns().stream().map(
                (Pair<String, String> column) -> column.getKey() + " " + column.getValue()
        ).collect(Collectors.toList()));

        statement.executeUpdate("CREATE TABLE " + table.getName() + "(" + columnList + ")");

        statement.close();
    }

    void insertRow(final String tableName, final List<String> values) throws SQLException {
        final Statement statement = connection.createStatement();

        final String concatenatedValues = String.join(", ", values.stream().map(
                value -> {
                    if (StringUtils.isNumeric(value)) {
                        return value;
                    } else {
                        return "'" + value + "'";
                    }
                }
        ).collect(Collectors.toList()));
        statement.executeUpdate("INSERT INTO " + tableName + " VALUES (" + concatenatedValues + ")");

        statement.close();
    }

    Connection getConnection() {
        return connection;
    }
}
