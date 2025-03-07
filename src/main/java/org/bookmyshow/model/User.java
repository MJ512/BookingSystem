package org.bookmyshow.model;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;

    public User(){}

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
