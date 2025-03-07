package org.bookmyshow.model;

import java.time.LocalDateTime;

public class Theater {

    private int id;
    private String name;
    private Address address;
    private LocalDateTime openingHour;
    private String contact;

    public Theater(final int id, final String name, final Address address, final LocalDateTime openingHour, final String contact) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHour = openingHour;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final Address getAddress() {
        return address;
    }

    public final LocalDateTime getOpeningHour() {
        return openingHour;
    }

    public final String getContact() {
        return contact;
    }

}
