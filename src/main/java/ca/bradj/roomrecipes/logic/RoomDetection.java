package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class RoomDetection {

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            int depth,
            WallDetector wd
    ) {
        return findRoomForDoor(
                doorPos,
                maxDistanceFromDoor,
                new SearchRange(null, null, null, null),
                Optional.empty(),
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
                wd
        );
    }

    public static Optional<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            Optional<InclusiveSpace> findAlternativeTo,
            int depth,
            WallDetector wd
    ) {
        return findRoomForDoor(
                doorPos,
                maxDistanceFromDoor,
                new SearchRange(null, null, null, null),
                findAlternativeTo,
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
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
            int depth,
            WallDetector wd
    ) {
        Function<Position, Optional<Room>> findFromBackWall = (Position wallPos) ->
                findRoomFromBackWall(
                        doorPos, wallPos, maxDistanceFromDoor, findAlternativeTo, exclusion, depth + 1, wd
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
            int depth,
            WallDetector wd
    ) {
        return findRoomFromBackWall(
                doorPos,
                wallPos,
                maxDistFromDoor,
                findAlt,
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
                wd
        );
    }

    private static Optional<Room> findRoomFromBackWall(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            Optional<InclusiveSpace> findAlt,
            WallExclusion exclusion,
            int depth,
            WallDetector wd
    ) {
        if (!wd.IsWall(wallPos)) {
            return Optional.empty();
        }
        Optional<Room> room = findRoomBetween(doorPos, wallPos, maxDistFromDoor, exclusion, depth + 1, wd);
        if (room.isEmpty()) {
            return Optional.empty();
        }
        if (findAlt.isPresent() && room.get().getSpace().equals(findAlt.get())) {
            return Optional.empty();
        }

        return room.map(v -> RoomDetection.simplify(v));
    }

    private static Room simplify(Room v) {
        if (v.getSpaces().size() == 2) {
            ImmutableList<InclusiveSpace> s = ImmutableList.copyOf(v.getSpaces());
            ZWall ew = s.get(0).getEastZWall();
            ZWall ww = s.get(1).getWestZWall();
            if (ew.northCorner.equals(ww.northCorner) && ew.southCorner.equals(ww.southCorner)) {
                ZWall nww = s.get(0).getWestZWall();
                ZWall eww = s.get(1).getEastZWall();
                return new Room(v.getDoorPos(), new InclusiveSpace(nww.northCorner, eww.southCorner));
            }
        }
        return v;
    }

    private static Optional<Room> findRoomBetween(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            int depth,
            WallDetector wd
    ) {
        return findRoomBetween(doorPos, wallPos, maxDistFromDoor, WallExclusion.mustHaveAllWalls(), depth + 1, wd);
    }

    private static Optional<Room> findRoomBetween(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            int depth,
            WallDetector wd
    ) {
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            throw new IllegalStateException("Expected straight line between positions");
        }
        int diffX = Math.max(doorPos.x, wallPos.x) - Math.min(doorPos.x, wallPos.x);
        int diffZ = Math.max(doorPos.z, wallPos.z) - Math.min(doorPos.z, wallPos.z);
        if (diffZ > diffX) {
            return findRoomWithNorthOrSouthDoor(doorPos, wallPos, maxDistFromDoor, exclusion, depth + 1, wd);
        }
        return findRoomWithWestOrEastDoor(doorPos, wallPos, maxDistFromDoor, exclusion, depth + 1, wd);
    }

    private static Optional<Room> findRoomWithNorthOrSouthDoor(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            int depth,
            WallDetector wd
    ) {
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            return Optional.empty();
        }
        ZWall midWall = new ZWall(doorPos, wallPos);
        if (ZWalls.isConnected(midWall, wd)) {
            return Optional.empty();
        }
        RoomHints roomHints = RoomHints.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            roomHints = findRoomFromNorthToSouthCenterLine(
                    midWall, i, exclusion, roomHints, wd
            );
            if (roomHints.isRoom(exclusion)) {
                return roomHints.asRoom(doorPos, exclusion);
            }
        }
        if (roomHints.hasAnyOpenings()) {
            Optional<Room> adjoined = findAdjoiningRoom(
                    roomHints, doorPos, maxDistFromDoor, depth + 1, wd
            );

            if (adjoined.isPresent()) {
                return adjoined;
            }
        }
        return Optional.empty();
    }

    private static Optional<Room> findRoomWithWestOrEastDoor(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            int depth,
            WallDetector wd
    ) {
        if (depth > maxDistFromDoor) {
            RoomRecipes.LOGGER.error("Reached detection limit for room between {} and {}", doorPos, wallPos);
            return Optional.empty();
        }
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            return Optional.empty();
        }
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
                    roomHints, doorPos, maxDistFromDoor, depth + 1, wd
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
            int depth,
            WallDetector wd
    ) {
        if (roomHints.northOpening != null) {
            Optional<RoomHints> space = findRoomForXOpening(
                    roomHints.northOpening, maxDistFromDoor, -1,
                    (XWall wall, RoomHints hints) -> new RoomHints(
                            wall, roomHints.northOpening, null, null,
                            null, roomHints.northOpening, null, null
                    ),
                    WallExclusion.allowSouthOpen(),
                    wd
            );
            if (space.isPresent() && roomHints.hasOneOpening()) {
                return roomHints.adjoinedTo(doorPos, space.get());
            }
        }
        if (roomHints.southOpening != null) {
            Optional<RoomHints> space = findRoomForXOpening(
                    roomHints.southOpening, maxDistFromDoor, 1,
                    (XWall wall, RoomHints hints) -> new RoomHints(
                            roomHints.southOpening, wall, null, null,
                            roomHints.southOpening, null, null, null
                    ),
                    WallExclusion.allowNorthOpen(),
                    wd
            );
            if (space.isPresent() && roomHints.hasOneOpening()) {
                return roomHints.adjoinedTo(doorPos, space.get());
            }
        }
        if (roomHints.westOpening != null) {
            Optional<RoomHints> space = findRoomForZOpening(
                    roomHints.westOpening, maxDistFromDoor, -1,
                    (ZWall wall, RoomHints hints) -> new RoomHints(
                            null, null, wall, roomHints.westOpening,
                            null, null, null, roomHints.westOpening
                    ),
                    WallExclusion.allowEastOpen(),
                    wd
            );
            if (space.isPresent() && roomHints.hasOneOpening()) { // TODO: Add test for multi-opening
                return roomHints.adjoinedTo(doorPos, space.get());
            }
        }
        if (roomHints.eastOpening != null) {
            Optional<RoomHints> space = findRoomForZOpening(
                    roomHints.eastOpening, maxDistFromDoor, 1,
                    (ZWall wall, RoomHints hints) -> new RoomHints(
                            null, null, roomHints.eastOpening, wall,
                            null, null, roomHints.eastOpening, null
                    ),
                    WallExclusion.allowWestOpen(),
                    wd
            );
            if (space.isPresent() && roomHints.hasOneOpening()) {
                return roomHints.adjoinedTo(doorPos, space.get());
            }
        }
        return Optional.empty();
    }

    private interface OpeningXHintFactory {
        RoomHints applyXWall(XWall wall, RoomHints hints);
    }

    private interface OpeningZHintFactory {
        RoomHints applyZWall(ZWall wall, RoomHints hints);
    }

    private static Optional<RoomHints> findRoomForXOpening(
            XWall opening,
            int maxDistFromDoor,
            int z,
            OpeningXHintFactory factory,
            WallExclusion exclusion,
            WallDetector wd
    ) {
        if (z == 0) {
            return Optional.empty();
        }
        RoomHints hints = RoomHints.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int zShift = i * z;
            XWall shifted = opening.shiftedSouthBy(zShift);
            if (XWalls.isConnected(shifted, wd)) {
                hints = factory.applyXWall(shifted, hints);
                break;
            }
            if (!opening.isSameContentAs(shifted, wd)) {
                break;
            }
        }

        Position mp = opening.getMidpoint();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int zShift = i * z;
            Optional<Room> room = findRoomFromBackWall(
                    mp, mp.offset(0, zShift),
                    maxDistFromDoor, Optional.empty(),
                    exclusion,
                    0, wd
            );
            if (room.isPresent()) {
                return room.map(v -> new RoomHints(
                        v.getSpace().getNorthXWall(),
                        v.getSpace().getSouthXWall(),
                        v.getSpace().getWestZWall(),
                        v.getSpace().getEastZWall(),
                        XWalls.findOpening(v.getSpace().getNorthXWall(), wd).orElse(null),
                        XWalls.findOpening(v.getSpace().getSouthXWall(), wd).orElse(null),
                        ZWalls.findOpening(v.getSpace().getWestZWall(), wd).orElse(null),
                        ZWalls.findOpening(v.getSpace().getEastZWall(), wd).orElse(null)
                ));
            }
        }
        if (hints.northWall == null || hints.southWall == null) {
            return Optional.empty();
        }
        ZWall westWall = new ZWall(hints.northWall.westCorner, hints.southWall.westCorner);
        ZWall eastWall = new ZWall(hints.northWall.eastCorner, hints.southWall.eastCorner);
        if (ZWalls.isConnected(westWall, wd) && ZWalls.isConnected(eastWall, wd)) {
            return Optional.of(hints);
        }
        return Optional.empty();
    }

    private static Optional<RoomHints> findRoomForZOpening(
            ZWall opening,
            int maxDistFromDoor,
            int x,
            OpeningZHintFactory factory,
            WallExclusion exclusion,
            WallDetector wd
    ) {
        if (x == 0) {
            return Optional.empty();
        }
        RoomHints hints = RoomHints.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int xShift = i * x;
            ZWall shifted = opening.shiftedEast(xShift);
            if (ZWalls.isConnected(shifted, wd)) {
                hints = factory.applyZWall(shifted, hints);
                break;
            }
            if (!opening.isSameContentAs(shifted, wd)) {
                break;
            }
        }

        Position mp = opening.getMidpoint();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int xShift = i * x;
            Optional<Room> room = findRoomFromBackWall(
                    mp, mp.offset(xShift, 0),
                    maxDistFromDoor, Optional.empty(),
                    exclusion,
                    0, wd
            );
            if (room.isPresent()) {
                return room.map(v -> new RoomHints(
                        v.getSpace().getNorthXWall(),
                        v.getSpace().getSouthXWall(),
                        v.getSpace().getWestZWall(),
                        v.getSpace().getEastZWall(),
                        XWalls.findOpening(v.getSpace().getNorthXWall(), wd).orElse(null),
                        XWalls.findOpening(v.getSpace().getSouthXWall(), wd).orElse(null),
                        ZWalls.findOpening(v.getSpace().getWestZWall(), wd).orElse(null),
                        ZWalls.findOpening(v.getSpace().getEastZWall(), wd).orElse(null)
                ));
            }
        }

        if (hints.westWall == null || hints.eastWall == null) {
            return Optional.empty();
        }
        XWall northWall = new XWall(hints.westWall.northCorner, hints.eastWall.northCorner);
        XWall southWall = new XWall(hints.westWall.southCorner, hints.eastWall.southCorner);
        if (XWalls.isConnected(northWall, wd) && XWalls.isConnected(southWall, wd)) {
            return Optional.of(hints);
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
        if (hints.northWall == null || hints.northOpening != null && hints.northOpening.sameWidth(hints.northWall)) {
            XWall pNorthWall = midWall.shiftedNorthBy(i);
            if (XWalls.isConnected(pNorthWall, wd)) {
                hints = hints.withNorthWall(pNorthWall);
                hints = hints.withNorthOpening(null);
                hints = hints.withWestWall(null);
                hints = hints.withEastWall(null);
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
        if (hints.southWall == null || hints.southOpening != null && hints.southOpening.sameWidth(hints.southWall)) {
            XWall pSouthWall = midWall.shiftedSouthBy(i);
            if (XWalls.isConnected(pSouthWall, wd)) {
                hints = hints.withSouthWall(pSouthWall);
                hints = hints.withSouthOpening(null);
                hints = hints.withWestWall(null);
                hints = hints.withEastWall(null);
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

    private static RoomHints findRoomFromNorthToSouthCenterLine(
            ZWall midWall,
            int i,
            // Distance from midpoint
            WallExclusion exclusion,
            RoomHints roomHints,
            WallDetector wd
    ) {
        RoomHints hints = roomHints.copy();
        if (hints.westWall == null || hints.westOpening != null && hints.westOpening.sameHeight(hints.westWall)) {
            ZWall pWestWall = midWall.shiftedWest(i);
            if (ZWalls.isConnected(pWestWall, wd)) {
                hints = hints.withWestWall(pWestWall);
                hints = hints.withWestOpening(null);
                hints = hints.withNorthWall(null);
                hints = hints.withSouthWall(null);
            } else {
                Optional<ZWall> opening = ZWalls.findOpening(pWestWall, wd);
                if (opening.isPresent()) {
                    if (opening.get().sameHeight(pWestWall)) {
                        hints = hints.withWestWall(opening.get());
                        hints = hints.withNorthWall(null);
                        hints = hints.withSouthWall(null);
                    } else {
                        hints = hints.withWestWall(pWestWall);
                    }
                    hints = hints.withWestOpening(opening.get());
                }
            }
        }
        if (hints.eastWall == null || hints.eastOpening != null && hints.eastOpening.sameHeight(hints.eastWall)) {
            ZWall pEastWall = midWall.shiftedEast(i);
            if (ZWalls.isConnected(pEastWall, wd)) {
                if (hints.eastOpening == null) {
                    hints = hints.withEastWall(pEastWall);
                    hints = hints.withEastOpening(null);
                    hints = hints.withNorthWall(null);
                    hints = hints.withSouthWall(null);
                }
            } else {
                Optional<ZWall> opening = ZWalls.findOpening(pEastWall, wd);
                if (opening.isPresent()) {
                    if (opening.get().sameHeight(pEastWall)) {
                        hints = hints.withEastOpening(opening.get());
                        hints = hints.withNorthWall(null);
                        hints = hints.withSouthWall(null);
                    } else {
//                        hints = hints.withEastWall(pEastWall);
                    }
                }
            }
        }

        if (
                (hints.westWall == null && hints.westOpening == null)
                        || (hints.eastWall == null && hints.eastOpening == null)
        ) {
            return hints;
        }
        Position nw = hints.westWall != null ? hints.westWall.northCorner : hints.westOpening.northCorner;
        Position sw = hints.westWall != null ? hints.westWall.southCorner : hints.westOpening.southCorner;
        Position ne = hints.eastWall != null ? hints.eastWall.northCorner : hints.eastOpening.northCorner;
        Position se = hints.eastWall != null ? hints.eastWall.southCorner : hints.eastOpening.southCorner;

        XWall pNorthWall = new XWall(nw, ne);
        if (hints.northWall == null && XWalls.isConnected(pNorthWall, wd)) {
            hints = hints.withNorthWall(pNorthWall);
            if (hints.isRoom(exclusion)) {
                return hints;
            }
        }
        XWall pSouthWall = new XWall(sw, se);
        if (hints.southWall == null && XWalls.isConnected(pSouthWall, wd)) {
            hints = hints.withSouthWall(pSouthWall);
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
