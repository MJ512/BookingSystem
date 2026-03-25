package org.bookmyshow.repository;

public enum DatabaseColumn {
    ID("id"),
    EMAIL("email"),
    PHONE("phone");

    private final String columnName;

    DatabaseColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
