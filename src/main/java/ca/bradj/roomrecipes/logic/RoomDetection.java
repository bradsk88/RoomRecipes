package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;

import java.util.Optional;
import java.util.function.Function;

public class RoomDetection {

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            WallDetector wd
    ) {
        return findRoomForDoor(doorPos, maxDistanceFromDoor, Optional.empty(), wd);
    }

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            Optional<InclusiveSpace> findAlternativeTo,
            WallDetector wd
    ) {
        Function<Position, Optional<Room>> findFromBackWall = (Position wallPos) ->
                findRoomFromBackWall(
                        doorPos, wallPos, maxDistanceFromDoor, findAlternativeTo, wd
                );

        for (int i = 2; i < maxDistanceFromDoor; i++) {
            Optional<Room> foundRoom = findFromBackWall.apply(doorPos.offset(i, 0));
            if (foundRoom.isPresent()) {
                return foundRoom;
            }
            foundRoom = findFromBackWall.apply(doorPos.offset(-i, 0));
            if (foundRoom.isPresent()) {
                return foundRoom;
            }
            foundRoom = findFromBackWall.apply(doorPos.offset(0, i));
            if (foundRoom.isPresent()) {
                return foundRoom;
            }
            foundRoom = findFromBackWall.apply(doorPos.offset(0, -i));
            if (foundRoom.isPresent()) {
                return foundRoom;
            }
        }
        return Optional.empty();
    }

    private static Optional<Room> findRoomFromBackWall(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            Optional<InclusiveSpace> findAlt,
            WallDetector wd
    ) {
        if (!wd.IsWall(wallPos)) {
            return Optional.empty();
        }
        Optional<Room> room = findRoomBetween(doorPos, wallPos, maxDistFromDoor, wd);
        if (room.isEmpty()) {
            return Optional.empty();
        }
        if (findAlt.isPresent() && room.get().getSpace().equals(findAlt.get())) {
            return Optional.empty();
        }
        return room;
    }

    private static Optional<Room> findRoomBetween(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallDetector wd
    ) {
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            throw new IllegalStateException("Expected straight line between positions");
        }
        int diffX = Math.max(doorPos.x, wallPos.x) - Math.min(doorPos.x, wallPos.x);
        int diffZ = Math.max(doorPos.z, wallPos.z) - Math.min(doorPos.z, wallPos.z);
        if (diffZ > diffX) {
            ZWall midWall = new ZWall(doorPos, wallPos);
            if (ZWalls.isConnected(midWall, wd)) {
                return Optional.empty();
            }
            Optional<ZWall> westWall = Optional.empty();
            Optional<ZWall> eastWall = Optional.empty();
            for (int i = 1; i < maxDistFromDoor; i++) {
                if (eastWall.isPresent() && westWall.isPresent()) {
                    XWall nWall = new XWall(westWall.get().northCorner, eastWall.get().northCorner);
                    XWall sWall = new XWall(westWall.get().southCorner, eastWall.get().southCorner);
                    if (!XWalls.isConnected(nWall, wd)) {
                        continue;
                    }
                    if (!XWalls.isConnected(sWall, wd)) {
                        continue;
                    }
                    return Optional.of(new Room(doorPos, new InclusiveSpace(
                            westWall.get().northCorner,
                            eastWall.get().southCorner
                    )));
                }
                if (ZWalls.isConnected(midWall.shiftedWest(i), wd)) {
                    westWall = Optional.of(midWall.shiftedWest(i));
                }
                if (ZWalls.isConnected(midWall.shiftedEast(i), wd)) {
                    eastWall = Optional.of(midWall.shiftedEast(i));
                }
            }
        } else {
            XWall midWall = new XWall(doorPos, wallPos);
            if (XWalls.isConnected(midWall, wd)) {
                return Optional.empty();
            }
            Optional<XWall> northWall = Optional.empty();
            Optional<XWall> southWall = Optional.empty();
            for (int i = 1; i < maxDistFromDoor; i++) {
                if (southWall.isPresent() && northWall.isPresent()) {
                    ZWall wWall = new ZWall(northWall.get().westCorner, southWall.get().westCorner);
                    ZWall eWall = new ZWall(northWall.get().eastCorner, southWall.get().eastCorner);
                    if (!ZWalls.isConnected(wWall, wd)) {
                        continue;
                    }
                    if (!ZWalls.isConnected(eWall, wd)) {
                        continue;
                    }
                    return Optional.of(new Room(doorPos, new InclusiveSpace(
                            northWall.get().westCorner,
                            southWall.get().eastCorner
                    )));
                }
                if (XWalls.isConnected(midWall.shiftedNorthBy(i), wd)) {
                    northWall = Optional.of(midWall.shiftedNorthBy(i));
                }
                if (XWalls.isConnected(midWall.shiftedSouthBy(i), wd)) {
                    southWall = Optional.of(midWall.shiftedSouthBy(i));
                }
            }
        }
        return Optional.empty();
    }

}
