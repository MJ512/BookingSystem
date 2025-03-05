package org.bookmyshow.model;


public class Screen {

    private int id;
    private String name;
    private int totalSeat;
    private Theater theater;

    public Screen(int id, String name, int totalSeat, Theater theater) {
        this.id = id;
        this.name = name;
        this.totalSeat = totalSeat;
        this.theater = theater;
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

    public int getTotalSeat() {
        return totalSeat;
    }

    public void setTotalSeat(int totalSeat) {
        this.totalSeat = totalSeat;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }
}
