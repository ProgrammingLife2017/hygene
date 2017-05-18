package org.dnacronym.hygene.persistence;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link FileDatabaseTable} class.
 */
final class FileDatabaseTableTest {
    private static final String TABLE_NAME = "just_another_table";

    private FileDatabaseTable fileDatabaseTable;


    @BeforeEach
    void setUp() {
        fileDatabaseTable = new FileDatabaseTable(TABLE_NAME);
    }


    @Test
    void testGetName() {
        assertThat(fileDatabaseTable.getName()).isEqualTo(TABLE_NAME);
    }

    @Test
    void testGetColumns() {
        assertThat(fileDatabaseTable.getColumns()).isEmpty();
    }

    @Test
    void testAddColumn() {
        final Pair<String, String> column = new Pair<>("test_key", "test_value");
        fileDatabaseTable.addColumn(column.getKey(), column.getValue());

        assertThat(fileDatabaseTable.getColumns().size()).isEqualTo(1);
        assertThat(fileDatabaseTable.getColumns()).contains(column);
    }
}
