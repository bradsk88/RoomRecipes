package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.adapter.Positions;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;

public class RoomDetection {
    private final int maxDistFromDoor;
    private ImmutableSet<Position> corners = ImmutableSet.of();

    public boolean isRoom() {
        return corners.size() == 4;
    }

    public ImmutableSet<Position> getCorners() {
        return corners;
    }

    public interface WallDetector {
        boolean IsWall(Position dp);
    }

    public RoomDetection(
            Position dp,
            int maxDistanceFromDoor
    ) {
        this.maxDistFromDoor = maxDistanceFromDoor;
    }

    public static Optional<Room> findRoom(
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd
    ) {
        Optional<Room> room = findNorthOrSouthWallFromDoor(doorPos, maxDistFromDoor, wd);
        if (room.isPresent()) {
            return room;
        }
        return findEastOrWestWallFromDoor(doorPos, maxDistFromDoor, wd);
    }

    public static Optional<Room> findNorthOrSouthWallFromDoor(
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd
    ) {
        Optional<XWall> doorWall = WallDetection.findEastToWestWall(maxDistFromDoor, wd, doorPos);
        if (doorWall.isEmpty()) {
            return Optional.empty();
        }
        Optional<XWall> ewWall = WallDetection.findParallelRoomXWall(maxDistFromDoor, wd, doorWall.get());
        if (ewWall.isEmpty()) {
            return Optional.empty();
        }
        if (ewWall.get().westCorner.z != ewWall.get().eastCorner.z) {
            return Optional.empty();
        }

        if (XWallLogic.isConnected(ewWall.get(), wd)) {
            return Optional.of(new Room(doorPos, Positions.getInclusiveSpace(ImmutableSet.of(
                    doorWall.get().westCorner,
                    doorWall.get().eastCorner,
                    ewWall.get().westCorner,
                    ewWall.get().eastCorner
            ))));
        }
        return Optional.empty();
    }

    public static Optional<Room> findEastOrWestWallFromDoor(
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd
    ) {
        Optional<ZWall> doorWall = WallDetection.findNorthToSouthWall(maxDistFromDoor, wd, doorPos);
        if (doorWall.isEmpty()) {
            return Optional.empty();
        }
        Optional<ZWall> ewWall = WallDetection.findParallelRoomZWall(maxDistFromDoor, wd, doorWall.get());
        if (ewWall.isEmpty()) {
            return Optional.empty();
        }
        if (ewWall.get().northCorner.x != ewWall.get().southCorner.x) {
            return Optional.empty();
        }

        if (ZWallLogic.isConnected(ewWall.get(), wd)) {
            return Optional.of(
                    new Room(doorPos, Positions.getInclusiveSpace(ImmutableSet.of(
                            doorWall.get().northCorner,
                            doorWall.get().southCorner,
                            ewWall.get().northCorner,
                            ewWall.get().southCorner
                    ))));
        }
        return Optional.empty();
    }

    private boolean isConnected(
            XWall wall,
            WallDetector wd
    ) {
        int width = wall.eastCorner.x - wall.westCorner.x;
        for (int i = 0; i < width; i++) {
            if (!wd.IsWall(wall.westCorner.offset(i, 0))) {
                return false;
            }
        }
        return true;
    }

}
