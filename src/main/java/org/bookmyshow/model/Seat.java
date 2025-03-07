package org.bookmyshow.model;


public class Seat {

    private int id;
    private char row;
    private int number;
    private Screen screen;
    private boolean isBooked;

    public Seat(final int id, final char row, final int number, final Screen screen, final boolean isBooked) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.screen = screen;
        this.isBooked = false;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final char getRow() {
        return row;
    }

    public final int getNumber() {
        return number;
    }

    public final Screen getScreen() {
        return screen;
    }

    public final boolean isBooked() {
        return isBooked;
    }

}
