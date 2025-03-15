package org.bookmyshow.model;

import java.time.LocalDateTime;

public class Theater {

    private final String name;
    private final int addressId;
    private final LocalDateTime openingHour;
    private final String contact;

    private int id;

    public Theater(final int id, final String name, final int addressId, final LocalDateTime openingHour, final String contact) {
        this.id = id;
        this.name = name;
        this.addressId = addressId;
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

    public final int getAddress() {
        return addressId;
    }

    public final LocalDateTime getOpeningHour() {
        return openingHour;
    }

    public final String getContact() {
        return contact;
    }

}
