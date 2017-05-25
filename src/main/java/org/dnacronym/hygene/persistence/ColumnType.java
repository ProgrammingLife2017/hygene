package org.dnacronym.hygene.persistence;


/**
 * Enum representing the SQLite type of a table column.
 */
public enum ColumnType {
    INTEGER("INTEGER"),
    TEXT("TEXT");

    private String typeString;


    /**
     * Constructs a new {@link ColumnType} instance.
     *
     * @param typeString the SQLite name of this type (capitalized)
     */
    ColumnType(final String typeString) {
        this.typeString = typeString;
    }


    /**
     * Returns the a {@link String} representing this type.
     *
     * @return the type string
     */
    public final String getTypeString() {
        return typeString;
    }
}
