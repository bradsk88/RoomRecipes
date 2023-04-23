package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;

import java.util.Optional;

public class WallDetection {

    public static Optional<ZWall> findNorthToSouthWall(
            int maxDistFromDoor,
            RoomDetection.WallDetector wd,
            Position doorPos
    ) {
        int northCornerZ = Integer.MAX_VALUE, southCornerZ = -Integer.MAX_VALUE;
        boolean started = false;
        for (int i = 0; i < maxDistFromDoor; i++) {
            Position op = doorPos.offset(0, i);
            if (wd.IsWall(op)) {
                started = true;
                northCornerZ = Math.min(northCornerZ, op.z);
                southCornerZ = Math.max(southCornerZ, op.z);
            } else if (started) {
                break;
            }
        }
        for (int i = 0; i < maxDistFromDoor; i++) {
            Position op = doorPos.offset(0, -i);
            if (wd.IsWall(op)) {
                started = true;
                northCornerZ = Math.min(northCornerZ, op.z);
                southCornerZ = Math.max(southCornerZ, op.z);
            } else if (started) {
                break;
            }
        }
        if (!started) {
            return Optional.empty();
        }
        if (Math.abs(southCornerZ - northCornerZ) < 2) {
            return Optional.empty();
        }
        return Optional.of(
                new ZWall(doorPos.WithZ(northCornerZ), doorPos.WithZ(southCornerZ))
        );
    }



    public static Optional<XWall> findEastToWestWall(
            int maxDistFromDoor,
            RoomDetection.WallDetector wd,
            Position doorPos
    ) {
        int westCornerX = Integer.MAX_VALUE, eastCornerX = -Integer.MAX_VALUE;
        boolean started = false;
        for (int i = 1; i < maxDistFromDoor; i++) {
            Position op = doorPos.offset(i, 0);
            if (wd.IsWall(op)) {
                started = true;
                westCornerX = Math.min(westCornerX, op.x);
                eastCornerX = Math.max(eastCornerX, op.x);
            } else if (started) {
                break;
            }
        }
        for (int i = 1; i < maxDistFromDoor; i++) {
            Position op = doorPos.offset(-i, 0);
            if (wd.IsWall(op)) {
                started = true;
                westCornerX = Math.min(westCornerX, op.x);
                eastCornerX = Math.max(eastCornerX, op.x);
            } else if (started) {
                break;
            }
        }
        if (!started) {
            return Optional.empty();
        }
        if (Math.abs(eastCornerX - westCornerX) < 2) {
            return Optional.empty();
        }
        return Optional.of(
                new XWall(doorPos.WithX(westCornerX), doorPos.WithX(eastCornerX))
        );
    }

    public static Optional<ZWall> findParallelRoomZWall(
            int maxDistFromDoor,
            ZWall doorWall,
            RoomDetection.WallDetector wd
        ) {
        return findParallelRoomZWall(maxDistFromDoor, doorWall, Optional.empty(), wd);
    }
    public static Optional<ZWall> findParallelRoomZWall(
            int maxDistFromDoor,
            ZWall doorWall,
            Optional<Room> findAlternativeTo,
            RoomDetection.WallDetector wd
        ) {
        if (!alreadyFoundEast(findAlternativeTo)) {
            Optional<ZWall> northWall = findEastParallelZWall(doorWall, maxDistFromDoor, wd);
            if (northWall.isPresent()) {
                return northWall;
            }
        }
        return findWestParallelZWall(doorWall, maxDistFromDoor, wd);
    }

    private static Optional<ZWall> findEastParallelZWall(
            ZWall doorWall,
            int maxDistFromDoor,
            RoomDetection.WallDetector wd
    ) {
        for (int i = 2; i < maxDistFromDoor; i++) {
            ZWall shifted = doorWall.shiftedEast(i);
            if (ZWalls.isConnected(shifted, wd)) {
                return Optional.of(shifted);
            }
        }
        return Optional.empty();
    }

    private static boolean alreadyFoundEast(
            Optional<Room> room
    ) {
        if (room.isEmpty()) {
            return false;
        }
        return room.get().getSpace().getEastX() > room.get().getDoorPos().x;
    }

    private static Optional<ZWall> findWestParallelZWall(
            ZWall doorWall,
            int maxDistFromDoor,
            RoomDetection.WallDetector wd
    ) {
        for (int i = 2; i < maxDistFromDoor; i++) {
            ZWall shifted = doorWall.shiftedWest(i);
            if (ZWalls.isConnected(shifted, wd)) {
                return Optional.of(shifted);
            }
        }
        return Optional.empty();
    }

    public static Optional<XWall> findParallelRoomXWall(
            int maxDistFromDoor,
            RoomDetection.WallDetector wd,
            XWall wall
    ) {
        return findParallelRoomXWall(maxDistFromDoor, Optional.empty(), wd, wall);
    }

    public static Optional<XWall> findParallelRoomXWall(
            int maxDistFromDoor,
            Optional<Room> findAlt,
            RoomDetection.WallDetector wd,
            XWall wall
    ) {
        if (!alreadyFoundNorth(findAlt)) {
            Optional<XWall> northWall = findNorthParallelXWall(maxDistFromDoor, wall, wd);
            if (northWall.isPresent()) {
                return northWall;
            }
        }
        return findSouthParallelXWall(maxDistFromDoor, wall, wd);
    }

    private static boolean alreadyFoundNorth(
            Optional<Room> room
    ) {
        if (room.isEmpty()) {
            return false;
        }
        return room.get().getSpace().getNorthZ() < room.get().getDoorPos().z;
    }

    private static Optional<XWall> findSouthParallelXWall(
            int maxDistFromDoor,
            XWall wall,
            RoomDetection.WallDetector wd
    ) {
        for (int i = 2; i < maxDistFromDoor; i++) {
            XWall shifted = wall.shiftedSouthBy(i);
            if (XWalls.isConnected(shifted, wd)) {
                return Optional.of(shifted);
            }
        }
        return Optional.empty();
    }

    private static Optional<XWall> findNorthParallelXWall(
            int maxDistFromDoor,
            XWall wall,
            RoomDetection.WallDetector wd
    ) {
        for (int i = 2; i < maxDistFromDoor; i++) {
            XWall shifted = wall.shiftedNorthBy(i);
            if (XWalls.isConnected(shifted, wd)) {
                return Optional.of(shifted);
            }
        }
        return Optional.empty();
    }
}
