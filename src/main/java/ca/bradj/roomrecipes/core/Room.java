package ca.bradj.roomrecipes.core;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;

public class Room {
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
