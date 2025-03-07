package org.bookmyshow.repository;

import org.bookmyshow.model.User;
import java.sql.SQLException;

public interface UserDAOInterface {

    boolean saveUser(final User user) throws SQLException;

    User getUserByEmailOrPhone(final String loginInput) throws SQLException;
}
