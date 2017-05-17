package org.dnacronym.hygene.persistence;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link DatabaseTable} class.
 */
final class DatabaseTableTest {
    private static final String TABLE_NAME = "just_another_table";

    private DatabaseTable databaseTable;


    @BeforeEach
    void setUp() {
        databaseTable = new DatabaseTable(TABLE_NAME);
    }


    @Test
    void testGetName() {
        assertThat(databaseTable.getName()).isEqualTo(TABLE_NAME);
    }

    @Test
    void testGetColumns() {
        assertThat(databaseTable.getColumns()).isEmpty();
    }

    @Test
    void testAddColumn() {
        final Pair<String, String> column = new Pair<>("test_key", "test_value");
        databaseTable.addColumn(column.getKey(), column.getValue());

        assertThat(databaseTable.getColumns().size()).isEqualTo(1);
        assertThat(databaseTable.getColumns()).contains(column);
    }
}
