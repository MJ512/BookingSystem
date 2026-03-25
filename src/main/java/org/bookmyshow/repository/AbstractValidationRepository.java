package org.bookmyshow.repository;

import org.bookmyshow.datasource.PostgreSQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Base class providing a generic existence-check query using
 * type-safe {@link DatabaseTable} and {@link DatabaseColumn} enums.
 */
public abstract class AbstractValidationRepository {

    private static final Logger logger = LoggerFactory.getLogger(AbstractValidationRepository.class);

    protected final boolean existsInDatabase(final DatabaseTable table,
                                              final DatabaseColumn column,
                                              final Object value) {
        final String query = "SELECT COUNT(*) FROM " + table.getTableName()
                + " WHERE " + column.getColumnName() + " = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (value instanceof Integer i) {
                ps.setInt(1, i);
            } else if (value instanceof String s) {
                ps.setString(1, s);
            } else {
                throw new IllegalArgumentException("Unsupported type for existsInDatabase: "
                        + value.getClass().getSimpleName());
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            logger.error("DB error in existsInDatabase [{}.{}]", table, column, e);
            return false;
        }
    }
}
