package org.bookmyshow.model;


public class Screen {

    private final String name;
    private final int totalSeat;
    private final int theaterId;

    private int id;

    public Screen(final int id, final String name, final int totalSeat, final int theaterId) {
        this.id = id;
        this.name = name;
        this.totalSeat = totalSeat;
        this.theaterId = theaterId;
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

    public final int getTheater() {
        return theaterId;
    }

}
