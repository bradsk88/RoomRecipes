package ca.bradj.roomrecipes.core;

public class RoomSplit {
    Room roomA;
    Room roomB;

    public RoomSplit(
            Room roomA,
            Room roomB
    ) {
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public Room getRoomA() {
        return roomA;
    }

    public Room getRoomB() {
        return roomB;
    }
}
