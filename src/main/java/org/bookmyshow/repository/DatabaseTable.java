package org.bookmyshow.repository;

public enum DatabaseTable {
    MOVIE_SHOW("movie_show"),
    USERS("users");

    private final String tableName;

    DatabaseTable(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
