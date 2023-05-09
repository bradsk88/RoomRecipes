package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class RoomDetection {

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            WallDetector wd
    ) {
        return findRoomForDoor(
                doorPos,
                maxDistanceFromDoor,
                new SearchRange(null, null, null, null),
                Optional.empty(),
                WallExclusion.mustHaveAllWalls(),
                wd
        );
    }

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            Optional<InclusiveSpace> findAlternativeTo,
            WallDetector wd
    ) {
        return findRoomForDoor(
                doorPos,
                maxDistanceFromDoor,
                new SearchRange(null, null, null, null),
                findAlternativeTo,
                WallExclusion.mustHaveAllWalls(),
                wd
        );
    }

    private static class SearchRange {
        final Integer minNorthZ;
        final Integer maxSouthZ;
        final Integer minWestZ;
        final Integer maxEastZ;

        public SearchRange(
                @Nullable Integer minNorthZ,
                @Nullable Integer maxSouthZ,
                @Nullable Integer minWestZ,
                @Nullable Integer maxEastZ
        ) {
            this.minNorthZ = minNorthZ;
            this.maxSouthZ = maxSouthZ;
            this.minWestZ = minWestZ;
            this.maxEastZ = maxEastZ;
        }
    }

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            SearchRange range,
            Optional<InclusiveSpace> findAlternativeTo,
            // TODO: Is this needed anymore
            WallExclusion exclusion,
            WallDetector wd
    ) {
        Function<Position, Optional<Room>> findFromBackWall = (Position wallPos) ->
                findRoomFromBackWall(
                        doorPos, wallPos, maxDistanceFromDoor, findAlternativeTo, exclusion, wd
                );

        for (int i = 2; i < maxDistanceFromDoor; i++) {
            Optional<Room> foundRoom = Optional.empty();
            if (!exclusion.allowOpenEastWall) {
                Position offset = doorPos.offset(i, 0);
                if (range.maxEastZ == null || offset.x <= range.maxEastZ) {
                    foundRoom = findFromBackWall.apply(offset);
                    if (foundRoom.isPresent()) {
                        return foundRoom;
                    }
                }
            }
            if (!exclusion.allowOpenWestWall) {
                Position offset = doorPos.offset(-i, 0);
                if (range.minWestZ == null || offset.x >= range.minWestZ) {
                    foundRoom = findFromBackWall.apply(offset);
                    if (foundRoom.isPresent()) {
                        return foundRoom;
                    }
                }
            }
            Position offset = doorPos.offset(0, i);
            if (range.maxSouthZ == null || offset.z <= range.maxSouthZ) {
                foundRoom = findFromBackWall.apply(offset);
                if (foundRoom.isPresent()) {
                    return foundRoom;
                }
            }
            offset = doorPos.offset(0, -i);
            if (range.minNorthZ == null || offset.z >= range.minNorthZ) {
                foundRoom = findFromBackWall.apply(offset);
                if (foundRoom.isPresent()) {
                    return foundRoom;
                }
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
        return findRoomFromBackWall(doorPos, wallPos, maxDistFromDoor, findAlt, WallExclusion.mustHaveAllWalls(), wd);
    }

    private static Optional<Room> findRoomFromBackWall(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            Optional<InclusiveSpace> findAlt,
            WallExclusion exclusion,
            WallDetector wd
    ) {
        if (!wd.IsWall(wallPos)) {
            return Optional.empty();
        }
        Optional<Room> room = findRoomBetween(doorPos, wallPos, maxDistFromDoor, exclusion, wd);
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
        return findRoomBetween(doorPos, wallPos, maxDistFromDoor, WallExclusion.mustHaveAllWalls(), wd);
    }

    private static Optional<Room> findRoomBetween(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            WallDetector wd
    ) {
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            throw new IllegalStateException("Expected straight line between positions");
        }
        int diffX = Math.max(doorPos.x, wallPos.x) - Math.min(doorPos.x, wallPos.x);
        int diffZ = Math.max(doorPos.z, wallPos.z) - Math.min(doorPos.z, wallPos.z);
        if (diffZ > diffX) {
            return findRoomWithNorthOrSouthDoor(doorPos, wallPos, maxDistFromDoor, exclusion, wd);
        }
        return findRoomWithWestOrEastDoor(doorPos, wallPos, maxDistFromDoor, exclusion, wd);
    }

    private static Optional<Room> findRoomWithNorthOrSouthDoor(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            WallDetector wd
    ) {
        ZWall midWall = new ZWall(doorPos, wallPos);
        if (ZWalls.isConnected(midWall, wd)) {
            return Optional.empty();
        }
        Optional<ZWall> westWall = Optional.empty();
        Optional<ZWall> eastWall = Optional.empty();
        Optional<ZWall> eastWallWithOpening = Optional.empty();
        Optional<InclusiveSpace> extra = Optional.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            if (eastWall.isPresent() && westWall.isPresent()) {
                XWall nWall = new XWall(westWall.get().northCorner, eastWall.get().northCorner);
                XWall sWall = new XWall(westWall.get().southCorner, eastWall.get().southCorner);
                if (!XWalls.isConnected(nWall, wd) && !exclusion.allowOpenNorthWall) {
                    continue;
                }
                if (!XWalls.isConnected(sWall, wd) && !exclusion.allowOpenSouthWall) {
                    continue;
                }
                Room room = new Room(doorPos, new InclusiveSpace(
                        westWall.get().northCorner,
                        eastWall.get().southCorner
                ));
                return Optional.of(room);
            }
            if (westWall.isEmpty() && ZWalls.isConnected(midWall.shiftedWest(i), wd)) {
                westWall = Optional.of(midWall.shiftedWest(i));
            }
            if (eastWall.isEmpty() && ZWalls.isConnected(midWall.shiftedEast(i), wd)) {
                eastWall = Optional.of(midWall.shiftedEast(i));
            }
            if (eastWall.isEmpty()) {
                Optional<ZWall> opening = ZWalls.findOpening(midWall.shiftedEast(i), wd);
                if (opening.isPresent()) {
                    extra = findRoomForDoor(
                            opening.get().northCorner.offset(0, 1),
                            maxDistFromDoor,
                            new SearchRange(null, null, null, null), // TODO: set limits
                            Optional.empty(),
                            WallExclusion.allowWestOpen(),
                            wd
                    ).map(Room::getSpace);
                    eastWallWithOpening = Optional.of(midWall.shiftedEast(i));
                }
            }
        }
        if (westWall.isPresent() && eastWall.isEmpty()) {
            if (extra.isPresent() && eastWallWithOpening.isPresent()) {
                return Optional.of(new Room(doorPos, new InclusiveSpace(
                        westWall.get().northCorner,
                        eastWallWithOpening.get().southCorner
                )).withExtraSpace(extra.get()));
            }
        }
        return Optional.empty();
    }

    private static Optional<Room> findRoomWithWestOrEastDoor(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            WallDetector wd
    ) {
        XWall midWall = new XWall(doorPos, wallPos);
        if (XWalls.isConnected(midWall, wd)) {
            return Optional.empty();
        }
        RoomHints roomHints = RoomHints.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            roomHints = findRoomFromWestToEastCenterLine(
                    midWall, i, exclusion, roomHints, wd
            );
            if (roomHints.isRoom(exclusion)) {
                return roomHints.asRoom(doorPos, exclusion);
            }
        }
        if (roomHints.hasAnyOpenings()) {
            Optional<Room> adjoined = findAdjoiningRoom(
                    roomHints, doorPos, maxDistFromDoor, wd
            );

            if (adjoined.isPresent()) {
                return adjoined;
            }
        }
        return Optional.empty();
    }

    private static Optional<Room> findAdjoiningRoom(
            RoomHints roomHints,
            Position doorPos,
            int maxDistFromDoor,
            WallDetector wd
    ) {
        if (roomHints.northOpening != null) {
            Optional<Room> room = findRoomForDoor(
                    roomHints.northOpening.getMidpoint(),
                    maxDistFromDoor,
                    new SearchRange(
                            null,
                            roomHints.northOpening.getZ(),
                            roomHints.northOpening.westCorner.x,
                            roomHints.northOpening.eastCorner.x
                    ),
                    Optional.empty(),
                    WallExclusion.allowSouthOpen(),
                    wd
            );
            if (room.isPresent()) {
                return roomHints.adjoinedTo(doorPos, room.get().getSpace());
            }
        }
        if (roomHints.southOpening != null) {
            Optional<Room> room = findRoomForDoor(
                    roomHints.southOpening.getMidpoint(),
                    maxDistFromDoor,
                    new SearchRange(
                            roomHints.southOpening.getZ(),
                            null,
                            roomHints.southOpening.westCorner.x,
                            roomHints.southOpening.eastCorner.x
                    ),
                    Optional.empty(),
                    WallExclusion.allowNorthOpen(),
                    wd
            );
            if (room.isPresent()) {
                return roomHints.adjoinedTo(doorPos, room.get().getSpace());
            }
        }
        if (roomHints.westOpening != null) {
            Optional<Room> room = findRoomForDoor(
                    roomHints.westOpening.getMidpoint(),
                    maxDistFromDoor,
                    new SearchRange(
                            roomHints.eastOpening.northCorner.z,
                            roomHints.eastOpening.southCorner.z,
                            null,
                            roomHints.westOpening.getMidpoint().x
                    ),
                    Optional.empty(),
                    WallExclusion.allowEastOpen(),
                    wd
            );
            if (room.isPresent()) {
                return roomHints.adjoinedTo(doorPos, room.get().getSpace());
            }
        }
        if (roomHints.eastOpening != null) {
            Optional<Room> room = findRoomForDoor(
                    roomHints.eastOpening.getMidpoint(),
                    maxDistFromDoor,
                    new SearchRange(
                            roomHints.eastOpening.northCorner.z,
                            roomHints.eastOpening.southCorner.z,
                            roomHints.eastOpening.getMidpoint().x,
                            null
                    ),
                    Optional.empty(),
                    WallExclusion.allowWestOpen(),
                    wd
            );
            if (room.isPresent()) {
                return roomHints.adjoinedTo(doorPos, room.get().getSpace());
            }
        }
        return Optional.empty();
    }

    private static RoomHints findRoomFromWestToEastCenterLine(
            XWall midWall,
            int i,
            // Distance from midpoint
            WallExclusion exclusion,
            RoomHints roomHints,
            WallDetector wd
    ) {
        RoomHints hints = roomHints.copy();
        // TODO: Put inside of condition(s)
        XWall pNorthWall = midWall.shiftedNorthBy(i);
        if (hints.northWall == null || hints.northOpening != null && hints.northOpening.sameWidth(hints.northWall)) {
            if (XWalls.isConnected(pNorthWall, wd)) {
                hints = hints.withNorthWall(pNorthWall);
                hints = hints.withNorthOpening(null);
                if (hints.isRoom(exclusion)) {
                    return hints;
                }
            } else {
                Optional<XWall> opening = XWalls.findOpening(pNorthWall, wd);
                if (opening.isPresent()) {
                    if (opening.get().sameWidth(pNorthWall)) {
                        hints = hints.withNorthWall(opening.get());
                        hints = hints.withWestWall(null);
                        hints = hints.withEastWall(null);
                    } else {
                        hints = hints.withNorthWall(pNorthWall);
                    }
                    hints = hints.withNorthOpening(opening.get());
                }
            }
        }
        XWall pSouthWall = midWall.shiftedSouthBy(i);
        if (hints.southWall == null || hints.southOpening != null && hints.southOpening.sameWidth(hints.southWall)) {
            if (XWalls.isConnected(pSouthWall, wd)) {
                hints = hints.withSouthWall(pSouthWall);
                hints = hints.withSouthOpening(null);
                if (hints.isRoom(exclusion)) {
                    return hints;
                }
            } else {
                Optional<XWall> opening = XWalls.findOpening(pSouthWall, wd);
                if (opening.isPresent()) {
                    if (opening.get().sameWidth(pSouthWall)) {
                        hints = hints.withSouthWall(opening.get());
                        hints = hints.withWestWall(null);
                        hints = hints.withEastWall(null);
                    } else {
                        hints = hints.withSouthWall(pSouthWall);
                    }
                    hints = hints.withSouthOpening(opening.get());
                }
            }
        }

        if (
                (hints.northWall == null && hints.northOpening == null)
                        || (hints.southWall == null && hints.southOpening == null)
        ) {
            return hints;
        }
        Position nw = hints.northWall != null ? hints.northWall.westCorner : hints.northOpening.westCorner;
        Position ne = hints.northWall != null ? hints.northWall.eastCorner : hints.northOpening.eastCorner;
        Position sw = hints.southWall != null ? hints.southWall.westCorner : hints.southOpening.westCorner;
        Position se = hints.southWall != null ? hints.southWall.eastCorner : hints.southOpening.eastCorner;

        ZWall pWestWall = new ZWall(nw, sw);
        if (hints.westWall == null && ZWalls.isConnected(pWestWall, wd)) {
            hints = hints.withWestWall(pWestWall);
            if (hints.isRoom(exclusion)) {
                return hints;
            }
        }
        ZWall pEastWall = new ZWall(ne, se);
        if (hints.eastWall == null && ZWalls.isConnected(pEastWall, wd)) {
            hints = hints.withEastWall(pEastWall);
            if (hints.isRoom(exclusion)) {
                return hints;
            }
        }
        return hints;
    }

    public static class WallExclusion {

        public static WallExclusion mustHaveAllWalls() {
            return new WallExclusion(false, false, false, false);
        }

        public static WallExclusion allowWestOpen() {
            return new WallExclusion(true, false, false, false);
        }

        public static WallExclusion allowEastOpen() {
            return new WallExclusion(false, true, false, false);
        }

        public static WallExclusion allowNorthOpen() {
            return new WallExclusion(false, false, true, false);
        }

        public static WallExclusion allowSouthOpen() {
            return new WallExclusion(false, false, false, true);
        }

        public WallExclusion(
                boolean allowOpenWestWall,
                boolean allowOpenEastWall,
                boolean allowOpenNorthWall,
                boolean allowOpenSouthWall
        ) {
            this.allowOpenWestWall = allowOpenWestWall;
            this.allowOpenEastWall = allowOpenEastWall;
            this.allowOpenNorthWall = allowOpenNorthWall;
            this.allowOpenSouthWall = allowOpenSouthWall;
        }

        public final boolean allowOpenWestWall;
        public final boolean allowOpenEastWall;
        public final boolean allowOpenNorthWall;
        public final boolean allowOpenSouthWall;
    }

}
