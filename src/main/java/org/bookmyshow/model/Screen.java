package org.bookmyshow.model;


public class Screen {

    private int id;
    private String name;
    private int totalSeat;
    private Theater theater;

    public Screen(final int id, final String name, final int totalSeat, final Theater theater) {
        this.id = id;
        this.name = name;
        this.totalSeat = totalSeat;
        this.theater = theater;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final int getTotalSeat() {
        return totalSeat;
    }

    public final Theater getTheater() {
        return theater;
    }

}
