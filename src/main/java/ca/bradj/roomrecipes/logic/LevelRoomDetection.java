package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.adapter.Positions;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.RoomSplit;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.stream.Stream;

public class LevelRoomDetection {

    public interface BlockChecker extends WallDetector {
        boolean IsEmpty(Position dp);

        boolean IsDoor(Position dp);
    }

    public static ImmutableMap<Position, Optional<Room>> findRooms(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            WallDetector checker
    ) {
        Map<Position, Optional<Room>> detectedRooms = new HashMap<>();
        for (Position doorPos : ImmutableList.copyOf(currentDoors)) {
            detectedRooms.put(doorPos, RoomDetection.findRoomForDoor(
                doorPos, maxDistanceFromDoor, Optional.empty(), checker
            ));
        }
        detectedRooms.values().forEach(r -> RoomRecipes.LOGGER.debug("Detected room: " + r));
        int attempts = 0;
        int corrections = 1;
        while (corrections > 0 && attempts <= 3) {
            attempts++;
            Stream<Optional<Room>> onlyPresent = detectedRooms.values().stream().filter(Optional::isPresent);
            List<Room> rooms = onlyPresent.map(Optional::get).toList();
            corrections = 0;
            for (Room r1 : rooms) {
                if (corrections > 0) {
                    break;
                }
                for (Room r2 : rooms) {
                    if (r1.equals(r2)) {
                        continue;
                    }
                    if (r1.getSpace().equals(r2.getSpace())) {
                        Optional<Room> alternate = RoomDetection.findRoomForDoor(
                                r2.getDoorPos(), maxDistanceFromDoor, Optional.of(r1.getSpace()), checker
                        );
                        if (alternate.isPresent()) {
                            RoomRecipes.LOGGER.debug("Using alternate room: " + alternate.get());
                            detectedRooms.put(r2.getDoorPos(), alternate);
                            corrections++;
                            break;
                        }
                        alternate = RoomDetection.findRoomForDoor(
                                r1.getDoorPos(), maxDistanceFromDoor, Optional.of(r2.getSpace()), checker
                        );
                        if (alternate.isPresent()) {
                            detectedRooms.put(r1.getDoorPos(), alternate);
                            corrections++;
                            RoomRecipes.LOGGER.debug("Using alternate room: " + alternate.get());
                            break;
                        }
                    } else {
                        if (InclusiveSpaces.overlapOnXZPlane(r1.getSpace(), r2.getSpace())) {
                            double a1 = InclusiveSpaces.calculateArea(r1.getSpace());
                            double a2 = InclusiveSpaces.calculateArea(r2.getSpace());
                            if (a1 > a2) {
                                RoomRecipes.LOGGER.debug("Chopping " + r2 + " off of " + r1);
                                InclusiveSpace chopped = r1.getSpace().chopOff(r2.getSpace());
                                detectedRooms.put(r1.getDoorPos(), Optional.of(r1.withSpace(chopped)));
                                corrections++;
                                break;
                            }
                            if (a2 > a1) {
                                RoomRecipes.LOGGER.debug("Chopping " + r1 + " off of " + r2);
                                InclusiveSpace chopped = r2.getSpace().chopOff(r1.getSpace());
                                detectedRooms.put(r2.getDoorPos(), Optional.of(r2.withSpace(chopped)));
                                corrections++;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return ImmutableMap.copyOf(detectedRooms);
    }

    private static Optional<RoomSplit> splitRooms(
            Position doorPos1,
            Position doorPos2,
            InclusiveSpace space,
            WallDetector wd
    ) {
        Position westDP = doorPos1;
        Position eastDP = doorPos2;
        if (westDP.x > eastDP.x) {
            westDP = doorPos2;
            eastDP = doorPos1;
        }
        int xDoorDiff = eastDP.x - westDP.x;
        int zDoorDiff = Math.max(doorPos1.z, doorPos2.z) - Math.min(doorPos1.z, doorPos2.z);
        if (xDoorDiff >= zDoorDiff) {
            ZWall westWall = space.getWestZWall();
            ZWall eastWall = space.getEastZWall();
            Optional<ZWall> middleWall = findMiddleZWall(westDP.x, eastDP.x, space, wd);
            if (middleWall.isPresent()) {
                return Optional.of(new RoomSplit(
                        new Room(westDP, Positions.getInclusiveSpace(ImmutableList.of(
                                westWall.northCorner,
                                middleWall.get().northCorner,
                                middleWall.get().southCorner,
                                westWall.southCorner
                        ))),
                        new Room(eastDP, Positions.getInclusiveSpace(ImmutableList.of(
                                middleWall.get().northCorner,
                                eastWall.northCorner,
                                eastWall.southCorner,
                                middleWall.get().southCorner
                        )))
                ));
            }
        }
        Position northDP = doorPos1;
        Position southDP = doorPos2;
        if (northDP.z > southDP.z) {
            northDP = doorPos2;
            southDP = doorPos1;
        }
        XWall northWall = space.getNorthXWall();
        XWall southWall = space.getSouthXWall();
        Optional<XWall> middleWall = findMiddleXWall(northDP.z, southDP.z, space, wd);
        if (middleWall.isPresent()) {
            return Optional.of(new RoomSplit(
                    new Room(northDP, Positions.getInclusiveSpace(ImmutableList.of(
                            northWall.westCorner,
                            northWall.eastCorner,
                            middleWall.get().eastCorner,
                            middleWall.get().westCorner
                    ))),
                    new Room(southDP, Positions.getInclusiveSpace(ImmutableList.of(
                            middleWall.get().westCorner,
                            middleWall.get().eastCorner,
                            southWall.eastCorner,
                            southWall.westCorner
                    )))
            ));
        }
        return Optional.empty();
    }

    private static Optional<ZWall> findMiddleZWall(
            int westX,
            int eastX,
            InclusiveSpace space,
            WallDetector wd
    ) {
        for (int i = 1; i < eastX - westX; i++) {
            ZWall middleWall = new ZWall(
                    new Position(westX + i, space.getNorthZ()),
                    new Position(westX + i, space.getSouthZ())
            );
            if (ZWalls.isConnected(middleWall, wd)) {
                return Optional.of(middleWall);
            }
        }
        return Optional.empty();
    }

    private static Optional<XWall> findMiddleXWall(
            int northZ,
            int southZ,
            InclusiveSpace space,
            WallDetector wd
    ) {
        for (int i = 1; i < southZ - northZ; i++) {
            XWall middleWall = new XWall(
                    new Position(space.getWestX(), northZ + i),
                    new Position(space.getEastX(), northZ + i)
            );
            if (XWalls.isConnected(middleWall, wd)) {
                return Optional.of(middleWall);
            }
        }
        return Optional.empty();
    }

    // TODO: Handle room merging (e.g. removing a dividing wall)

}
