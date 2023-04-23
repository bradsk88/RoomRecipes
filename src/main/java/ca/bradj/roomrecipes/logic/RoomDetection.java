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
        return findRoom(doorPos, maxDistFromDoor, Optional.empty(), wd);
    }

    private static Optional<Room> findRoom(
            Position doorPos,
            int maxDistFromDoor,
            Optional<Room> findAlternateSpace,
            WallDetector wd
    ) {
        return findRoom(doorPos, maxDistFromDoor, wd, findAlternateSpace);
    }

    public interface AlternateTo {
        boolean IsSame(Position p1, Position p2);
    }

    public static Optional<Room> findRoom(
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd,
            Room findAlternateTo
    ) {
        return findRoom(doorPos, maxDistFromDoor, wd, Optional.of(findAlternateTo));
    }
    private static Optional<Room> findRoom(
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd,
            Optional<Room> findAlternateTo
    ) {
        Optional<ZWall> altZ = Optional.empty();
        if (findAlternateTo.isPresent()) {
            altZ = findAlternateTo.get().getBackZWall();
        }
        Optional<Room> room = findNorthOrSouthRoomFromDoor(
                doorPos, maxDistFromDoor, findAlternateTo, wd
        );
        if (room.isPresent()) {
            return room;
        }
        return findEastOrWestWallFromDoor(doorPos, maxDistFromDoor, findAlternateTo, wd);
    }

    public static Optional<Room> findNorthOrSouthRoomFromDoor(
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd
    ) {
        return findNorthOrSouthRoomFromDoor(doorPos, maxDistFromDoor, Optional.empty(), wd);
    }
    public static Optional<Room> findNorthOrSouthRoomFromDoor(
            Position doorPos,
            int maxDistFromDoor,
            Room findAlternateTo,
            WallDetector wd
    ) {
        return findNorthOrSouthRoomFromDoor(doorPos, maxDistFromDoor, Optional.of(findAlternateTo), wd);
    }

    private static Optional<Room> findNorthOrSouthRoomFromDoor(
            Position doorPos,
            int maxDistFromDoor,
            Optional<Room> findAlt,
            WallDetector wd
    ) {
        Optional<XWall> doorWall = WallDetection.findEastToWestWall(maxDistFromDoor, wd, doorPos);
        if (doorWall.isEmpty()) {
            return Optional.empty();
        }
        Optional<XWall> ewWall = WallDetection.findParallelRoomXWall(maxDistFromDoor, findAlt, wd, doorWall.get());
        if (ewWall.isEmpty()) {
            return Optional.empty();
        }
        if (ewWall.get().westCorner.z != ewWall.get().eastCorner.z) {
            return Optional.empty();
        }
        if (findAlt.isPresent()) {
            if (findAlt.get().getBackXWall().isPresent()) {
                if (findAlt.get().getBackXWall().get().equals(ewWall.get())) {
                    return Optional.empty();
                }
            }
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
            Room findAlternateTo,
            WallDetector wd
    ) {
        return findEastOrWestWallFromDoor(doorPos, maxDistFromDoor, Optional.of(findAlternateTo), wd);
    }
    private static Optional<Room> findEastOrWestWallFromDoor(
            Position doorPos,
            int maxDistFromDoor,
            Optional<Room> findAlternateTo,
            WallDetector wd
    ) {
        Optional<ZWall> doorWall = WallDetection.findNorthToSouthWall(maxDistFromDoor, wd, doorPos);
        if (doorWall.isEmpty()) {
            return Optional.empty();
        }
        Optional<ZWall> ewWall = WallDetection.findParallelRoomZWall(maxDistFromDoor,
                doorWall.get(),
                findAlternateTo,
                wd);
        if (ewWall.isEmpty()) {
            return Optional.empty();
        }
        if (ewWall.get().northCorner.x != ewWall.get().southCorner.x) {
            return Optional.empty();
        }
        if (findAlternateTo.isPresent()) {
            if (findAlternateTo.get().getBackZWall().isPresent()) {
                if (findAlternateTo.get().getBackZWall().get().equals(ewWall.get())) {
                    return Optional.empty();
                }
            }
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
