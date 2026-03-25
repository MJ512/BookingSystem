package org.bookmyshow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents an application user.
 * {@code userId} is 0 for deserialized JSON requests (not yet persisted).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;

    /** No-arg constructor required by Jackson for deserialization. */
    public User() {}

    public User(final int userId, final String name, final String email,
                final String phone, final String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(final int userId, final String name, final String email, final String phone) {
        this(userId, name, email, phone, null);
    }

    public int getUserId()       { return userId; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getPhone()     { return phone; }
    public String getPassword()  { return password; }

    public void setUserId(int userId)        { this.userId = userId; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
}
