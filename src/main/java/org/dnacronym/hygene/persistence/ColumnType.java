package org.dnacronym.hygene.persistence;


/**
 * Enum representing the SQLite type of a table column.
 */
public enum ColumnType {
    INTEGER("INTEGER"),
    TEXT("TEXT"),
    BLOB("BLOB");

    private String typeString;


    /**
     * Constructs a new {@link ColumnType} instance.
     *
     * @param typeString the SQLite name of this type (capitalized)
     */
    ColumnType(final String typeString) {
        this.typeString = typeString;
    }


    @Override
    public String toString() {
        return typeString;
    }
}
