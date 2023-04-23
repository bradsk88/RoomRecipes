package ca.bradj.roomrecipes.core;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;

import java.util.Objects;
import java.util.Optional;

public class Room {
    public Optional<XWall> getBackXWall() {
        if (doorPos.z == getSpace().getNorthZ()) {
            return Optional.of(getSpace().getSouthXWall());
        }
        if (doorPos.z == getSpace().getSouthZ()) {
            return Optional.of(getSpace().getNorthXWall());
        }
        return Optional.empty();
    }
    public Optional<ZWall> getBackZWall() {
        if (doorPos.x == getSpace().getWestX()) {
            return Optional.of(getSpace().getEastZWall());
        }
        if (doorPos.x == getSpace().getEastX()) {
            return Optional.of(getSpace().getWestZWall());
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Room{" +
                "doorPos=" + doorPos +
                ", space=" + space +
                '}';
    }

    public Position getDoorPos() {
        return doorPos;
    }

    Position doorPos;
    InclusiveSpace space;

    public Room(
            Position doorPos,
            InclusiveSpace space
    ) {
        // TODO: Validate doorPos is in space
        this.doorPos = doorPos;
        this.space = space;
    }

    public InclusiveSpace getSpace() {
        return this.space;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(doorPos, room.doorPos) && Objects.equals(space, room.space);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doorPos, space);
    }
}
