package org.bookmyshow.model;


public class Seat {

    private int id;
    private char row;
    private int number;
    private Screen screen;
    private boolean isBooked;

    public Seat(int id, char row, int number, Screen screen, boolean isBooked) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.screen = screen;
        this.isBooked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char getRow() {
        return row;
    }

    public void setRow(char row) {
        this.row = row;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
