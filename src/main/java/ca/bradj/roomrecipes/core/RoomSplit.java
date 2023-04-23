package ca.bradj.roomrecipes.core;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "RoomSplit{" +
                "roomA=" + roomA +
                ", roomB=" + roomB +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomSplit roomSplit = (RoomSplit) o;
        return Objects.equals(roomA, roomSplit.roomA) && Objects.equals(roomB, roomSplit.roomB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomA, roomB);
    }
}
