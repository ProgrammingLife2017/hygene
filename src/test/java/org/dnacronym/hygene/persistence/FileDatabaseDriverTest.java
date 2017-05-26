package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


/**
 * Test suite for the {@link FileDatabaseDriver} class.
 */
final class FileDatabaseDriverTest extends FileDatabaseBaseTest {
    private FileDatabaseDriver fileDatabaseDriver;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabaseDriver = new FileDatabaseDriver(GFA_FILE_NAME);
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabaseDriver.close();
        super.tearDown();
    }


    /**
     * Runs through the typical process of writing to and reading from the file database.
     * <p>
     * This test is rather smoky in nature, but that is due to the large number of side-effects happening in all of
     * these methods. In other words, it is quite cumbersome to test one without running all of the other.
     *
     * @throws SQLException in the case of an error during SQL operations
     */
    @Test
    void testWriteRead() throws SQLException {
        final String testTableName = "test";
        final FileDatabaseTable fileDatabaseTable;

        fileDatabaseTable = new FileDatabaseTable(testTableName);
        fileDatabaseTable.addColumn("col1", ColumnType.TEXT);
        fileDatabaseTable.addColumn("col2", ColumnType.TEXT);
        fileDatabaseDriver.setUpTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow(testTableName, Arrays.asList("1", "2"));
        assertThat(fileDatabaseDriver.getSingleValue(testTableName, "col1", "1", "col2")).isEqualTo("2");
    }

    @Test
    void testDeleteRow() throws SQLException {
        final FileDatabaseTable fileDatabaseTable = new FileDatabaseTable("test");
        fileDatabaseTable.addColumn("col1", ColumnType.TEXT);
        fileDatabaseDriver.setUpTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow("test", Collections.singletonList("val1"));
        fileDatabaseDriver.deleteRow("test", "col1", "val1");

        assertThat(fileDatabaseDriver.hasRow("test", "col1", "val1")).isFalse();
    }

    @Test
    void testHasRowFalse() throws SQLException {
        final FileDatabaseTable fileDatabaseTable = new FileDatabaseTable("test");
        fileDatabaseTable.addColumn("col1", ColumnType.TEXT);
        fileDatabaseDriver.setUpTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow("test", Collections.singletonList("val1"));

        assertThat(fileDatabaseDriver.hasRow("test", "col1", "val2")).isFalse();
    }

    @Test
    void testHasRowTrue() throws SQLException {
        final FileDatabaseTable fileDatabaseTable = new FileDatabaseTable("test");
        fileDatabaseTable.addColumn("col1", ColumnType.TEXT);
        fileDatabaseDriver.setUpTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow("test", Collections.singletonList("val1"));

        assertThat(fileDatabaseDriver.hasRow("test", "col1", "val1")).isTrue();
    }

    @Test
    void testGetAllOfTable() throws SQLException {
        final String testTableName = "test";

        final FileDatabaseTable fileDatabaseTable = new FileDatabaseTable(testTableName);
        fileDatabaseTable.addColumn("col1", ColumnType.TEXT);
        fileDatabaseDriver.setUpTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow(testTableName, Collections.singletonList("val1"));

        final int[] count = {0};
        fileDatabaseDriver.forEachRow(testTableName, resultSet -> {
            count[0]++;
            try {
                assertThat(resultSet.getString("col1")).isEqualTo("val1");
            } catch (final SQLException e) {
                fail(e.getMessage());
            }
        });

        assertThat(count[0]).isEqualTo(1);
    }

    @Test
    void testDeleteAllFromTable() throws SQLException {
        final String testTableName = "test";

        final FileDatabaseTable fileDatabaseTable = new FileDatabaseTable(testTableName);
        fileDatabaseTable.addColumn("col1", ColumnType.TEXT);
        fileDatabaseDriver.setUpTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow(testTableName, Collections.singletonList("val1"));
        assertThat(fileDatabaseDriver.hasRow("test", "col1", "val1")).isTrue();

        fileDatabaseDriver.deleteAllFromTable(testTableName);
        assertThat(fileDatabaseDriver.hasRow("test", "col1", "val1")).isFalse();
    }
}
