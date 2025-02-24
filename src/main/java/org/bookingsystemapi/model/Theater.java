package org.bookingsystemapi.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class Theater {

    private int id;
    private String name;
    private Address address;
    private LocalDateTime openingHour;
    private String contact;

    public Theater(int id, String name, Address address, LocalDateTime openingHour, String contact) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingHour = openingHour;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LocalDateTime getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(LocalDateTime openingHour) {
        this.openingHour = openingHour;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
