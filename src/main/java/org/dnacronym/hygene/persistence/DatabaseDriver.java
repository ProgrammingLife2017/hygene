package org.dnacronym.hygene.persistence;

import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
     * @throws SQLException in the case of an error during SQL operations
     */
    void setupTable(final DatabaseTable table) throws SQLException {
        final Statement statement = connection.createStatement();

        final String columnList = String.join(", ", table.getColumns().stream().map(
                (Pair<String, String> column) -> column.getKey() + " " + column.getValue()
        ).collect(Collectors.toList()));

        statement.executeUpdate("CREATE TABLE " + table.getName() + "(" + columnList + ")");

        statement.close();
    }

    /**
     * Inserts a row of values into the table with given name.
     * <p>
     * These values need to be in the same order as the columns of this table.
     *
     * @param tableName the name of the table the row should be inserted into
     * @param values    the values to be added as a row
     * @throws SQLException in the case of an error during SQL operations
     */
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

    String getSingleValue(final String tableName, final String keyColumnName, final String keyColumnValue,
                          final String valueColumnName) throws SQLException {
        final Statement statement = connection.createStatement();

        final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName
                + " WHERE " + keyColumnName + "='" + keyColumnValue + "'");

        if (!resultSet.next()) {
            throw new SQLException("Expected at least one row in ResultSet.");
        }
        final String value = resultSet.getString(valueColumnName);

        statement.close();

        return value;
    }
}
