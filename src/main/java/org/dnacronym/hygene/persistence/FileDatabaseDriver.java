package org.dnacronym.hygene.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

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
@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE") // not relevant for a local, isolated file database
final class FileDatabaseDriver {
    static final String DB_FILE_EXTENSION = ".db";

    private final Connection connection;


    /**
     * Constructs a FileDatabaseDriver.
     *
     * @param fileName the file name of the corresponding GFA-file
     * @throws SQLException in the case of erroneous SQL behaviour
     */
    FileDatabaseDriver(final String fileName) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + fileName + DB_FILE_EXTENSION);
    }


    /**
     * Creates the given tables in the database.
     *
     * @param table the table to be set up
     * @throws SQLException in the case of an error during SQL operations
     */
    void setupTable(final FileDatabaseTable table) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            final String columnList = String.join(", ", table.getColumns().stream().map(
                    (Pair<String, String> column) -> column.getKey() + " " + column.getValue()
            ).collect(Collectors.toList()));

            statement.executeUpdate("CREATE TABLE " + table.getName() + "(" + columnList + ")");
        }
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
        try (final Statement statement = connection.createStatement()) {
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
        }
    }

    /**
     * Queries the database for the value of a single key.
     *
     * @param tableName       the name of the table
     * @param keyColumnName   the name of the key column
     * @param keyColumnValue  the key to be queried for
     * @param valueColumnName the name of the column in which the result value lies
     * @return the value corresponding to that key
     * @throws SQLException in the case of an error during SQL operations
     */
    String getSingleValue(final String tableName, final String keyColumnName, final String keyColumnValue,
                          final String valueColumnName) throws SQLException {
        final String sql = "SELECT * FROM " + tableName + " WHERE " + keyColumnName + "='" + keyColumnValue + "'";

        try (final Statement statement = connection.createStatement()) {
            try (final ResultSet resultSet = statement.executeQuery(sql)) {
                if (!resultSet.next()) {
                    throw new SQLException("Expected at least one row in ResultSet.");
                }
                final String value = resultSet.getString(valueColumnName);
                if (value == null) {
                    throw new SQLException("Expected at non-null ResultSet value.");
                }

                return value;
            }
        }
    }

    /**
     * Closes the database connection.
     * <p>
     * To be called when this instance is no longer needed.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    void close() throws SQLException {
        connection.close();
    }
}
