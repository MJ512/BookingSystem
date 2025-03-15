package org.bookmyshow.model;

public class User {
    private final int userId;
    private final String name;
    private final String email;
    private final String phone;

    private String password;


    public User(final int userId, final String name, final String email, final String phone, final String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(final int userId, final String name, final String email, final String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public final int getUserId() {
        return userId;
    }

    public final String getName() {
        return name;
    }

    public final String getEmail() {
        return email;
    }
    public final String getPhone() {
        return phone;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(final String password) {
        this.password = password;
    }
}
