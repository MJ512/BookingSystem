package org.bookmyshow.model;

public class Address {

    private int id;
    private String street;
    private String city;
    private String state;

    public Address(final int id, final String street, final String city, final String state) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

}
