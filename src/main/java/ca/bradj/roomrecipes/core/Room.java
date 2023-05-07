package ca.bradj.roomrecipes.core;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;

import java.util.*;

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
    List<InclusiveSpace> space;

    public Room(
            Position doorPos,
            InclusiveSpace space
    ) {
        // TODO: Validate doorPos is in space
        this(doorPos, ImmutableList.of(space));
    }
    public Room(
            Position doorPos,
            Collection<InclusiveSpace> spaces
    ) {
        // TODO: Validate doorPos is in space
        this.doorPos = doorPos;
        this.space = new ArrayList<>(spaces);
    }

    public InclusiveSpace getSpace() {
        return this.space.get(0);
    }

    public Collection<InclusiveSpace> getSpaces() {
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

    public Room withSpace(InclusiveSpace chopped) {
        return new Room(this.getDoorPos(), chopped);
    }

    public Room withExtraSpace(InclusiveSpace inclusiveSpace) {
        this.space.add(inclusiveSpace);
        return this;
    }
}
