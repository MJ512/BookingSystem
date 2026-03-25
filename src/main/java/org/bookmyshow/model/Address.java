package org.bookmyshow.model;

public class Address {

    private final String street;
    private final String city;
    private final String state;
    private final int zipCode;

    private int id;

    public Address(final int id, final String street, final String city, final String state, final int zipCode) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }
}
