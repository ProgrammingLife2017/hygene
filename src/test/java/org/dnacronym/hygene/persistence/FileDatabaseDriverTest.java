package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FileDatabaseDriver} class.
 */
final class FileDatabaseDriverTest extends FileDatabaseTest {
    private FileDatabaseDriver fileDatabaseDriver;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();
        fileDatabaseDriver = new FileDatabaseDriver(GFA_FILE_NAME);
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
        fileDatabaseTable.addColumn("col1", "string");
        fileDatabaseTable.addColumn("col2", "string");
        fileDatabaseDriver.setupTable(fileDatabaseTable);

        fileDatabaseDriver.insertRow(testTableName, Arrays.asList("1", "2"));
        assertThat(fileDatabaseDriver.getSingleValue(testTableName, "col1", "1", "col2")).isEqualTo("2");
    }


    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabaseDriver.close();
        super.tearDown();
    }
}
