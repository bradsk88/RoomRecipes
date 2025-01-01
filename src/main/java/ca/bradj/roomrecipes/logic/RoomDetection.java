package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.Wall;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class RoomDetection {

    public static Search<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            int depth,
            WallDetector wd
    ) {
        return findRoomForDoor(
                doorPos,
                maxDistanceFromDoor,
                new SearchRange(
                        null,
                        null,
                        null,
                        null
                ),
                Optional.empty(),
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
                wd
        );
    }

    public static Search<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            Optional<InclusiveSpace> findAlternativeTo,
            int depth,
            WallDetector wd
    ) {
        return findRoomForDoor(
                doorPos,
                maxDistanceFromDoor,
                new SearchRange(
                        null,
                        null,
                        null,
                        null
                ),
                findAlternativeTo,
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
                wd
        );
    }

    public static Search<Room> findRoomForDoorIteration(
            Position nextDoor,
            int i,
            int maxDistanceFromDoor,
            Consumer<String> flightRecorder,
            WallDetector checker
    ) {
        return findRoomForDoorIteration(
                nextDoor,
                i,
                maxDistanceFromDoor,
                RoomDetection.SearchRange.outermost(),
                Optional.empty(),
                RoomDetection.WallExclusion.mustHaveAllWalls(),
                0,
                flightRecorder,
                checker,
                ImmutableList.of()
        );
    }

    static class SearchRange {
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

        public static SearchRange outermost() {
            return new SearchRange(
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    public static Search<Room> findRoomForDoorIteration(
            Position doorPos,
            int i,
            int maxDistanceFromDoor,
            SearchRange range,
            Optional<InclusiveSpace> findAlternativeTo,
            // TODO: Is this needed anymore
            WallExclusion exclusion,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        if (!wd.IsWall(doorPos)) {
            return Search.empty();
        }

        record(flightRecorder, "Searching outward from door position %s [radius %d]", doorPos.getUIString(), i);

        Function<Position, Search<Room>> findFromBackWall = (Position wallPos) ->
                findRoomFromBackWall(
                        doorPos,
                        wallPos,
                        maxDistanceFromDoor,
                        findAlternativeTo,
                        exclusion,
                        depth + 1,
                        (s) -> record(flightRecorder, s),
                        wd,
                        spacesFoundAlready
                );

        Search<Room> foundRoom = Search.empty();
        if (!exclusion.allowOpenEastWall) {
            Position offset = doorPos.offset(
                    i,
                    0
            );
            if (range.maxEastZ == null || offset.x <= range.maxEastZ) {
                record(flightRecorder, "Found a valid coordinate for the east back wall at %s", offset.getUIString());
                foundRoom = findFromBackWall.apply(offset);
                if (foundRoom.isPresent()) {
                    return foundRoom;
                }
            }
        }
        if (!exclusion.allowOpenWestWall) {
            Position offset = doorPos.offset(
                    -i,
                    0
            );
            if (range.minWestZ == null || offset.x >= range.minWestZ) {
                record(flightRecorder, "Found a valid coordinate for the west back wall at %s", offset.getUIString());
                foundRoom = findFromBackWall.apply(offset);
                if (foundRoom.isPresent()) {
                    return foundRoom;
                }
            }
        }
        Position offset = doorPos.offset(
                0,
                i
        );
        if (range.maxSouthZ == null || offset.z <= range.maxSouthZ) {
            record(flightRecorder, "Found a valid coordinate for the south back wall at %s", offset.getUIString());
            foundRoom = findFromBackWall.apply(offset);
            if (foundRoom.isPresent()) {
                return foundRoom;
            }
        }
        offset = doorPos.offset(
                0,
                -i
        );
        if (range.minNorthZ == null || offset.z >= range.minNorthZ) {
            record(flightRecorder, "Found a valid coordinate for the north back wall at %s", offset.getUIString());
            foundRoom = findFromBackWall.apply(offset);
            if (foundRoom.isPresent()) {
                return foundRoom;
            }
        }
        return Search.empty();
    }

    private static void record(
            Consumer<String> flightRecorder,
            String s,
            Object... uiString
    ) {
        if (flightRecorder == null) {
            return;
        }
        flightRecorder.accept(String.format(s, uiString));
    }

    public static Search<Room> findRoomForDoor(
            Position doorPos,
            int maxDistanceFromDoor,
            SearchRange range,
            Optional<InclusiveSpace> findAlternativeTo,
            // TODO: Is this needed anymore
            WallExclusion exclusion,
            int depth,
            WallDetector wd
    ) {
        for (int i = 2; i < maxDistanceFromDoor; i++) {
            Search<Room> res = findRoomForDoorIteration(
                    doorPos,
                    i,
                    maxDistanceFromDoor,
                    range,
                    findAlternativeTo,
                    exclusion,
                    depth,
                    null, // FIXME: Keep recording
                    wd,
                    ImmutableList.of()
            );
            if (res.isPresent()) {
                return res;
            }
        }
        return Search.empty();
    }

    private static Search<Room> findRoomFromBackWall(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            Optional<InclusiveSpace> findAlt,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        return findRoomFromBackWall(
                doorPos,
                wallPos,
                maxDistFromDoor,
                findAlt,
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
                flightRecorder,
                wd,
                spacesFoundAlready
        );
    }

    private static Search<Room> findRoomFromBackWall(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            Optional<InclusiveSpace> findAlt,
            WallExclusion exclusion,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        if (!wd.IsWall(wallPos)) {
            flightRecorder.accept("Was air at " + wallPos.getUIString());
            return Search.empty();
        }
        flightRecorder.accept("Was wall block at " + wallPos.getUIString());
        Search<Room> room = findRoomBetween(
                doorPos,
                wallPos,
                maxDistFromDoor,
                exclusion,
                depth + 1,
                flightRecorder,
                wd,
                spacesFoundAlready
        );
        if (room.isEmpty()) {
            flightRecorder.accept(
                    String.format("No room was found between %s and %s", doorPos.getUIString(), wallPos.getUIString()));
            return Search.empty();
        }
        if (findAlt.isPresent() && room.get()
                                       .getSpace()
                                       .equals(findAlt.get())) {
            return Search.empty();
        }

        return room.map(RoomDetection::simplify);
    }

    private static Room simplify(Room v) {
        if (v.getSpaces()
             .size() == 2) {
            ImmutableList<InclusiveSpace> s = ImmutableList.copyOf(v.getSpaces());
            ZWall ew = s.get(0)
                        .getEastZWall();
            ZWall ww = s.get(1)
                        .getWestZWall();
            if (ew.northCorner.equals(ww.northCorner) && ew.southCorner.equals(ww.southCorner)) {
                ZWall nww = s.get(0)
                             .getWestZWall();
                ZWall eww = s.get(1)
                             .getEastZWall();
                return new Room(
                        v.getDoorPos(),
                        InclusiveSpace.from(nww.northCorner).to(eww.southCorner)
                );
            }
        }
        return v;
    }

    private static Search<Room> findRoomBetween(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        return findRoomBetween(
                doorPos,
                wallPos,
                maxDistFromDoor,
                WallExclusion.mustHaveAllWalls(),
                depth + 1,
                flightRecorder,
                wd,
                spacesFoundAlready
        );
    }

    private static Search<Room> findRoomBetween(
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            throw new IllegalStateException("Expected straight line between positions");
        }
        int diffX = Math.max(
                doorPos.x,
                wallPos.x
        ) - Math.min(
                doorPos.x,
                wallPos.x
        );
        int diffZ = Math.max(
                doorPos.z,
                wallPos.z
        ) - Math.min(
                doorPos.z,
                wallPos.z
        );
        if (diffZ > diffX) {
            return findRoomWithDoor(
                    Direction.NORTH,
                    doorPos,
                    wallPos,
                    maxDistFromDoor,
                    exclusion,
                    depth + 1,
                    flightRecorder,
                    wd,
                    ZWall::new,
                    XWall::new,
                    spacesFoundAlready
            );
        }
        return findRoomWithDoor(
                Direction.WEST,
                doorPos,
                wallPos,
                maxDistFromDoor,
                exclusion,
                depth + 1,
                flightRecorder,
                wd,
                XWall::new,
                ZWall::new,
                spacesFoundAlready
        );
    }

    private static <W extends Wall<W>, W2 extends Wall<W2>> Search<Room> findRoomWithDoor(
            Direction doorSide,
            Position doorPos,
            Position wallPos,
            int maxDistFromDoor,
            WallExclusion exclusion,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            WallFactory<W> midWallFactory,
            WallFactory<W2> endWallsFactory,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        if (doorPos.x != wallPos.x && doorPos.z != wallPos.z) {
            flightRecorder.accept("The door and the wall do not share coordinates. Skipping.");
            return Search.empty();
        }
        W midWall = midWallFactory.makeWall(
                doorPos,
                wallPos
        );

        int initialLength = midWall.getLengthOnAxis();
        if (initialLength > 1) {
            midWall = midWall.shortenNegative(1)
                             .shortenPositive(1);
        } else if (exclusion.allowOpenSouthWall || exclusion.allowOpenEastWall) {
            midWall = midWall.shortenNegative(1);
        } else if (exclusion.allowOpenNorthWall || exclusion.allowOpenWestWall) {
            midWall = midWall.shortenPositive(1);
        }

        flightRecorder.accept(
                String.format(
                        "Staged potential mid-wall %s due to exclusion policy %s",
                        midWall.toShortString(),
                        exclusion.toShortString()
                ));

        if (Walls.isConnected(
                doorSide.cw(),
                midWall,
                wd
        )) {
            flightRecorder.accept("Potential mid-wall is not a wall");
            return Search.empty();
        }
        RoomHints roomHints = RoomHints.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            RoomHints rh = findRoomFromCenterLine(
                    midWall,
                    doorSide,
                    i,
                    exclusion,
                    roomHints,
                    initialLength,
                    flightRecorder,
                    wd,
                    endWallsFactory
            );
            if (rh.equals(roomHints)) {
                break;
            }
            roomHints = rh;
            if (roomHints.isRoom(exclusion)) {
                return Search.from(roomHints.asRoom(
                        doorPos,
                        exclusion
                ));
            }
        }
        Optional<InclusiveSpace> asps = roomHints.asSpace(WallExclusion.allowAllOpen());
        if (asps.isPresent() && alreadyFound(spacesFoundAlready, asps.get())) {
            // TODO: Is this the ONLY alreadyFound check that is needed?
            flightRecorder.accept(
                    "Found adjoining room that is already accounted for. Must have gone in a circle. Stopping now.");
            return Search.of(new Room(doorPos, InclusiveSpace.from(doorPos).to(doorPos)));
        }

        if (roomHints.hasAnyOpenings()) {
            ImmutableList.Builder<InclusiveSpace> b = ImmutableList.builder();
            b.addAll(spacesFoundAlready);
            asps.ifPresent(b::add);
            Search<Room> adjoined = findAdjoiningRoom(
                    roomHints,
                    doorPos,
                    maxDistFromDoor,
                    depth + 1,
                    flightRecorder,
                    wd,
                    spacesFoundAlready
            );

            if (adjoined.isPresent()) {
                return adjoined;
            }
        }
        return Search.empty();
    }

    private static Search<Room> findAdjoiningRoom(
            RoomHints roomHints,
            Position doorPos,
            int maxDistFromDoor,
            int depth,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        Optional<InclusiveSpace> n = Optional.empty();
        Optional<InclusiveSpace> s = Optional.empty();
        Optional<InclusiveSpace> e = Optional.empty();
        Optional<InclusiveSpace> w = Optional.empty();
        Optional<InclusiveSpace> space1 = roomHints.asSpace(WallExclusion.allowAllOpen());
        if (roomHints.northOpening != null) {
            flightRecorder.accept("An opening was detected on the north side of the room");
            ImmutableList.Builder<InclusiveSpace> b = ImmutableList.builder();
            b.addAll(spacesFoundAlready);
            space1.ifPresent(b::add);
            Search<RoomHints> space = findNewRoomForXOpening(
                    roomHints.northOpening,
                    maxDistFromDoor,
                    -1,
                    (XWall wall, RoomHints hints) -> new RoomHints(
                            wall,
                            roomHints.northOpening,
                            null,
                            null,
                            null,
                            roomHints.northOpening,
                            null,
                            null
                    ),
                    flightRecorder,
                    WallExclusion.allowSouthOpen(),
                    wd,
                    b.build()
            );
            if (space.isPresent()) {
                if (isLoopEnd(space)) {
                    return Search.end(roomHints.asRoom(doorPos, WallExclusion.allowAllOpen()).get());
                }
                n = roomHints.adjoinedTo(
                        doorPos,
                        space.get()
                );
            }
        }
        if (roomHints.southOpening != null) {
            flightRecorder.accept(String.format(
                    "An opening was detected on the south side of the room: %s",
                    roomHints.toShortString()
            ));
            ImmutableList.Builder<InclusiveSpace> b = ImmutableList.builder();
            b.addAll(spacesFoundAlready);
            space1.ifPresent(b::add);
            Search<RoomHints> space = findNewRoomForXOpening(
                    roomHints.southOpening,
                    maxDistFromDoor,
                    1,
                    (XWall wall, RoomHints hints) -> new RoomHints(
                            roomHints.southOpening,
                            wall,
                            null,
                            null,
                            roomHints.southOpening,
                            null,
                            null,
                            null
                    ),
                    flightRecorder,
                    WallExclusion.allowNorthOpen(),
                    wd,
                    b.build()
            );
            if (space.isPresent()) {
                if (isLoopEnd(space)) {
                    return Search.end(roomHints.asRoom(doorPos, WallExclusion.allowAllOpen()).get());
                }
                s = roomHints.adjoinedTo(
                        doorPos,
                        space.get()
                );
            }
        }
        if (roomHints.westOpening != null) {
            flightRecorder.accept(String.format(
                    "An opening was detected on the west side of the room: -> %s <-",
                    roomHints.toShortString()
            ));
            ImmutableList.Builder<InclusiveSpace> b = ImmutableList.builder();
            b.addAll(spacesFoundAlready);
            space1.ifPresent(b::add);
            Search<RoomHints> space = findNewRoomForZOpening(
                    roomHints.westOpening,
                    maxDistFromDoor,
                    -1,
                    (ZWall wall, RoomHints hints) -> new RoomHints(
                            null,
                            null,
                            wall,
                            roomHints.westOpening,
                            null,
                            null,
                            null,
                            roomHints.westOpening
                    ),
                    WallExclusion.allowEastOpen(),
                    flightRecorder,
                    wd,
                    b.build()
            );
            if (space.isPresent()) {
                if (isLoopEnd(space)) {
                    return Search.end(roomHints.asRoom(doorPos, WallExclusion.allowAllOpen()).get());
                }
                w = roomHints.adjoinedTo(
                        doorPos,
                        space.get()
                );
            }
        }
        if (roomHints.eastOpening != null) {
            flightRecorder.accept("An opening was detected on the east side of the room");
            ImmutableList.Builder<InclusiveSpace> b = ImmutableList.builder();
            b.addAll(spacesFoundAlready);
            b.add(space1.get());
            Search<RoomHints> space = findNewRoomForZOpening(
                    roomHints.eastOpening,
                    maxDistFromDoor,
                    1,
                    (ZWall wall, RoomHints hints) -> new RoomHints(
                            null,
                            null,
                            null,
                            wall,
                            null,
                            null,
                            roomHints.eastOpening,
                            null
                    ),
                    WallExclusion.allowWestOpen(),
                    flightRecorder,
                    wd,
                    b.build()
            );
            if (space.isPresent()) {
                if (isLoopEnd(space)) {
                    return Search.end(roomHints.asRoom(doorPos, WallExclusion.allowAllOpen()).get());
                }
                e = roomHints.adjoinedTo(
                        doorPos,
                        space.get()
                );
            }
        }

        if (roomHints.northOpening != null && roomHints.southOpening != null && n.isPresent() && s.isPresent()) {
            final InclusiveSpace nn = n.get();
            final InclusiveSpace ss = s.get();
            return Search.from(roomHints.asRoom(
                                                doorPos,
                                                new WallExclusion(
                                                        false,
                                                        false,
                                                        true,
                                                        true
                                                )
                                        )
                                        .map(v -> v.withExtraSpace(nn)
                                                   .withExtraSpace(ss)));
        }
        if (roomHints.northOpening != null && n.isPresent()) {
            final InclusiveSpace nn = n.get();
            return Search.from(roomHints.asRoom(
                                                doorPos,
                                                WallExclusion.allowAllOpen()
                                        )
                                        .map(v -> v.withExtraSpace(nn)));
        }
        if (roomHints.southOpening != null && s.isPresent()) {
            final InclusiveSpace ss = s.get();
            return Search.from(roomHints.asRoom(
                                                doorPos,
                                                // FIXME: THis causes it to use the opening as the wall
                                                WallExclusion.allowAllOpen()
                                        )
                                        .map(v -> v.withExtraSpace(ss)));
        }

        if (roomHints.westOpening != null && roomHints.eastOpening != null && w.isPresent() && e.isPresent()) {
            final InclusiveSpace ww = w.get();
            final InclusiveSpace ee = e.get();
            return Search.from(roomHints.asRoom(
                                                doorPos,
                                                new WallExclusion(
                                                        true,
                                                        true,
                                                        false,
                                                        false
                                                )
                                        )
                                        .map(v -> v.withExtraSpace(ww)
                                                   .withExtraSpace(ee)));
        }
        if (roomHints.westOpening != null && w.isPresent()) {
            final InclusiveSpace ww = w.get();
            return Search.from(
                    roomHints.asRoom(
                                     doorPos,
                                     WallExclusion.allowAllOpen()
                             )
                             .map(v -> v.withExtraSpace(ww))
            );
        }
        if (roomHints.eastOpening != null && e.isPresent()) {
            final InclusiveSpace ee = e.get();
            return Search.from(
                    roomHints.asRoom(
                                     doorPos,
                                     WallExclusion.allowAllOpen()
                             )
                             .map(v -> v.withExtraSpace(ee))
            );
        }

        return Search.empty();
    }

    private static boolean isLoopEnd(
            Search<RoomHints> space
    ) {
        if (space.isEnd()) {
            return true;
        }
        Optional<InclusiveSpace> zpc = space.get().asSpace(WallExclusion.allowAllOpen());
        return InclusiveSpaces.calculateArea(zpc.get()) == 1;
    }

    private static boolean alreadyFound(
            ImmutableList<InclusiveSpace> spacesFoundAlready,
            InclusiveSpace ss
    ) {
        for (InclusiveSpace inclusiveSpace : spacesFoundAlready) {
            if (InclusiveSpaces.fullyContains(inclusiveSpace, ss)) {

                return true;
            }
        }
        return false;
    }

    private interface OpeningXHintFactory {
        RoomHints applyXWall(
                XWall wall,
                RoomHints hints
        );
    }

    private interface OpeningZHintFactory {
        RoomHints applyZWall(
                ZWall wall,
                RoomHints hints
        );
    }

    private static Search<RoomHints> findNewRoomForXOpening(
            XWall opening,
            int maxDistFromDoor,
            int z,
            OpeningXHintFactory factory,
            Consumer<String> flightRecorder,
            WallExclusion exclusion,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        if (z == 0) {
            return Search.empty();
        }

        flightRecorder.accept(String.format(
                "Treating opening %s like a door and searching for a room [Exlusion %s]",
                opening.toShortString(), exclusion.toShortString()
        ));

        RoomHints hints = RoomHints.empty();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int zShift = i * z;
            XWall shifted = opening.shiftedSouthBy(zShift);
            if (XWalls.isConnected(
                    shifted,
                    wd
            )) {
                hints = factory.applyXWall(
                        shifted,
                        hints
                );
                break;
            }
            if (!opening.isSameContentAs(
                    shifted,
                    wd
            )) {
                break;
            }
        }

        Position mp = opening.getMidpoint();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int zShift = i * z;
            Search<Room> room = findRoomFromBackWall(
                    mp,
                    mp.offset(
                            0,
                            zShift
                    ),
                    maxDistFromDoor,
                    Optional.empty(),
                    exclusion,
                    0,
                    flightRecorder,
                    wd,
                    spacesFoundAlready
            );
            if (room.isPresent()) {
                return room.map(v -> new RoomHints(
                        v.getSpace()
                         .getNorthXWall(),
                        v.getSpace()
                         .getSouthXWall(),
                        v.getSpace()
                         .getWestZWall(),
                        v.getSpace()
                         .getEastZWall(),
                        XWalls.findOpening(
                                      v.getSpace()
                                       .getNorthXWall(),
                                      wd
                              )
                              .orElse(null),
                        XWalls.findOpening(
                                      v.getSpace()
                                       .getSouthXWall(),
                                      wd
                              )
                              .orElse(null),
                        ZWalls.findOpening(
                                      v.getSpace()
                                       .getWestZWall(),
                                      wd
                              )
                              .orElse(null),
                        ZWalls.findOpening(
                                      v.getSpace()
                                       .getEastZWall(),
                                      wd
                              )
                              .orElse(null)
                ));
            }
        }
        if (hints.northWall == null || hints.southWall == null) {
            return Search.empty();
        }
        ZWall westWall = new ZWall(
                hints.northWall.westCorner,
                hints.southWall.westCorner
        );
        ZWall eastWall = new ZWall(
                hints.northWall.eastCorner,
                hints.southWall.eastCorner
        );
        if (ZWalls.isConnected(
                westWall,
                wd
        ) && ZWalls.isConnected(
                eastWall,
                wd
        )) {
            return Search.of(hints);
        }
        return Search.empty();
    }

    private static Search<RoomHints> findNewRoomForZOpening(
            ZWall opening,
            int maxDistFromDoor,
            int x,
            OpeningZHintFactory factory,
            WallExclusion exclusion,
            Consumer<String> flightRecorder,
            WallDetector wd,
            ImmutableList<InclusiveSpace> spacesFoundAlready
    ) {
        if (x == 0) {
            return Search.empty();
        }

        flightRecorder.accept(String.format(
                "Treating opening %s like a door and searching for a room [Exlusion %s]",
                opening.toShortString(), exclusion.toShortString()
        ));

        Position mp = opening.getMidpoint();
        for (int i = 1; i < maxDistFromDoor; i++) {
            int xShift = i * x;
            Search<Room> room = findRoomFromBackWall(
                    mp,
                    mp.offset(
                            xShift,
                            0
                    ),
                    maxDistFromDoor,
                    Optional.empty(),
                    exclusion,
                    0,
                    flightRecorder,
                    wd,
                    spacesFoundAlready
            );
            if (room.isPresent()) {
                return room.map(v -> new RoomHints(
                        v.getSpace()
                         .getNorthXWall(),
                        v.getSpace()
                         .getSouthXWall(),
                        v.getSpace()
                         .getWestZWall(),
                        v.getSpace()
                         .getEastZWall(),
                        XWalls.findOpening(
                                      v.getSpace()
                                       .getNorthXWall(),
                                      wd
                              )
                              .orElse(null),
                        XWalls.findOpening(
                                      v.getSpace()
                                       .getSouthXWall(),
                                      wd
                              )
                              .orElse(null),
                        ZWalls.findOpening(
                                      v.getSpace()
                                       .getWestZWall(),
                                      wd
                              )
                              .orElse(null),
                        ZWalls.findOpening(
                                      v.getSpace()
                                       .getEastZWall(),
                                      wd
                              )
                              .orElse(null)
                ));
            }
        }

        return Search.empty();
    }

    private interface WallFactory<W extends Wall<W>> {
        W makeWall(
                Position p,
                Position p2
        );
    }

    private static <W extends Wall<W>, W2 extends Wall<W2>> RoomHints findRoomFromCenterLine(
            W midWall,
            Direction doorWall,
            int i,
            // Distance from midpoint
            WallExclusion exclusion,
            RoomHints roomHints,
            int initialLength,
            Consumer<String> flightRecorder,
            WallDetector wd,
            WallFactory<W2> wf
    ) {
        flightRecorder.accept(String.format("Searching outward from %s [iteration %d]", midWall.toShortString(), i));
        Direction wallA = doorWall.ccw();
        Direction wallB = wallA.opp();
        Direction backWall = doorWall.opp();

        RoomHints hints = roomHints.copy();
        hints = getRoomHints(
                midWall,
                i,
                initialLength,
                exclusion,
                wd,
                hints,
                wallA
        );
        hints = getRoomHints(
                midWall,
                i,
                initialLength,
                exclusion,
                wd,
                hints,
                wallB
        );

        if (
                (hints.getWall(wallA) == null && hints.getOpening(wallA) == null)
                        || (hints.getWall(wallB) == null && hints.getOpening(wallB) == null)
        ) {
            return hints;
        }
        Position nw = hints.getWall(wallA) != null ? hints.getWall(wallA)
                                                          .negativeCorner() : hints.getOpening(wallA)
                                                                                   .negativeCorner();
        Position ne = hints.getWall(wallA) != null ? hints.getWall(wallA)
                                                          .positiveCorner() : hints.getOpening(wallA)
                                                                                   .positiveCorner();
        Position sw = hints.getWall(wallB) != null ? hints.getWall(wallB)
                                                          .negativeCorner() : hints.getOpening(wallB)
                                                                                   .negativeCorner();
        Position se = hints.getWall(wallB) != null ? hints.getWall(wallB)
                                                          .positiveCorner() : hints.getOpening(wallB)
                                                                                   .positiveCorner();

        W2 pWestWall = wf.makeWall(
                nw,
                sw
        );
        pWestWall = pWestWall.shortenNegative(1)
                             .shortenPositive(1);
        if (hints.getWall(doorWall) == null && Walls.isConnected(
                doorWall,
                pWestWall,
                wd
        )) {
            hints = hints.withWall(
                    doorWall,
                    pWestWall.extendNegative(1)
                             .extendPositive(1)
            );
            if (hints.isRoom(exclusion)) {
                return hints;
            }
        }
        W2 pEastWall = wf.makeWall(
                ne,
                se
        );
        pEastWall = pEastWall.shortenNegative(1)
                             .shortenPositive(1);
        if (hints.getWall(backWall) == null && Walls.isConnected(
                backWall,
                pEastWall,
                wd
        )) {
            hints = hints.withWall(
                    backWall,
                    pEastWall.extendNegative(1)
                             .extendPositive(1)
            );
            if (hints.isRoom(exclusion)) {
                return hints;
            }
        }
        return hints;
    }

    private static <W extends Wall<W>> RoomHints getRoomHints(
            W midWall,
            int i,
            int initialLength,
            WallExclusion exclusion,
            WallDetector wd,
            RoomHints hints,
            Direction s
    ) {
        Wall<?> opening1 = hints.getOpening(s);
        if (hints.getWall(s) == null || opening1 != null && opening1.sameLengthOnAxis(hints.getWall(s))) {
            Wall<?> pNorthWall = midWall.shifted(
                    s,
                    i
            );
            Wall<?> metaNorthWall = pNorthWall;
            if (initialLength >= 2) {
                metaNorthWall = metaNorthWall.extendNegative(1)
                                             .extendPositive(1);
            } else if (exclusion.allowOpenSouthWall || exclusion.allowOpenEastWall) {
                metaNorthWall = metaNorthWall.extendNegative(1);
            } else if (exclusion.allowOpenNorthWall || exclusion.allowOpenWestWall) {
                metaNorthWall = metaNorthWall.extendPositive(1);
            }
            if (Walls.isConnected(
                    s,
                    pNorthWall,
                    wd
            )) {
                hints = hints.withWall(
                        s,
                        metaNorthWall
                );
                hints = hints.withOpening(
                        s,
                        null
                );
                hints = hints.withWall(
                        s.ccw(),
                        null
                );
                hints = hints.withWall(
                        s.cw(),
                        null
                );
            } else {
                Optional<Wall<?>> opening = Walls.findOpening(
                        s,
                        metaNorthWall,
                        wd
                );
                if (opening.isPresent()) {
                    if (opening1 != null && opening1.isLargerOnAxis(opening.get())) {
                        hints = hints.withOpening(
                                s,
                                opening.get()
                        );
                        hints = hints.withWall(
                                s,
                                metaNorthWall
                        );
                        hints = hints.withWall(
                                s.ccw(),
                                null
                        );
                        hints = hints.withWall(
                                s.cw(),
                                null
                        );
                    } else if (opening.get()
                                      .sameLengthOnAxis(metaNorthWall)) {
                        hints = hints.withOpening(
                                s,
                                opening.get()
                        );
                        hints = hints.withWall(
                                s.ccw(),
                                null
                        );
                        hints = hints.withWall(
                                s.cw(),
                                null
                        );
                    } else {
                        if (hints.getOpening(s) == null || hints.getOpening(s)
                                                                .isSameContentOnAxis(
                                                                        opening.get(),
                                                                        wd
                                                                )) {
                            hints = hints.withWall(
                                    s,
                                    metaNorthWall
                            );
                            hints = hints.withOpening(
                                    s,
                                    opening.get()
                            );
                        }
                    }
                }
            }
        }
        return hints;
    }

    public static class WallExclusion {

        public static WallExclusion mustHaveAllWalls() {
            return new WallExclusion(
                    false,
                    false,
                    false,
                    false
            );
        }

        public static WallExclusion allowAllOpen() {
            return new WallExclusion(
                    true,
                    true,
                    true,
                    true
            );
        }

        public static WallExclusion allowWestOpen() {
            return new WallExclusion(
                    true,
                    false,
                    false,
                    false
            );
        }

        public static WallExclusion allowEastOpen() {
            return new WallExclusion(
                    false,
                    true,
                    false,
                    false
            );
        }

        public static WallExclusion allowNorthOpen() {
            return new WallExclusion(
                    false,
                    false,
                    true,
                    false
            );
        }

        public static WallExclusion allowSouthOpen() {
            return new WallExclusion(
                    false,
                    false,
                    false,
                    true
            );
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

        public String toShortString() {
            return String.format(
                    "N: %s, S: %s, E: %s, W: %s",
                    allowOpenNorthWall ? "allow-open" : "must-close",
                    allowOpenSouthWall ? "allow-open" : "must-close",
                    allowOpenEastWall ? "allow-open" : "must-close",
                    allowOpenWestWall ? "allow-open" : "must-close"
            );
        }
    }

}
