package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.adapter.Positions;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.RoomSplit;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;

public class LevelRoomDetection {

    public interface BlockChecker extends RoomDetection.WallDetector {
        boolean IsEmpty(Position dp);

        boolean IsDoor(Position dp);
    }

    public static ImmutableMap<Position, Optional<Room>> findRooms(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            RoomDetection.WallDetector checker
    ) {
        Map<Position, Optional<Room>> detectedRooms = new HashMap<>();
        for (Position doorPos : ImmutableList.copyOf(currentDoors)) {
            detectedRooms.put(doorPos, RoomDetection.findRoom(doorPos, maxDistanceFromDoor, checker));
        }
        List<Room> rooms = detectedRooms.values().stream().filter(Optional::isPresent).map(Optional::get).toList();
        for (Room r1 : rooms) {
            for (Room r2 : rooms) {
                if (r1.equals(r2)) {
                    continue;
                }
                if (r1.getSpace().equals(r2.getSpace())) {
                    Optional<Room> alternate = RoomDetection.findRoom(
                            r2.getDoorPos(), maxDistanceFromDoor, checker, r2
                    );
                    if (alternate.isPresent()) {
                        detectedRooms.put(r2.getDoorPos(), alternate);
                        continue;
                    }
                    alternate = RoomDetection.findRoom(
                            r1.getDoorPos(), maxDistanceFromDoor, checker, r1
                    );
                    if (alternate.isPresent()) {
                        detectedRooms.put(r1.getDoorPos(), alternate);
                        continue;
                    }
                    Optional<RoomSplit> split = LevelRoomDetection.splitRooms(
                            r1.getDoorPos(),
                            r2.getDoorPos(),
                            r1.getSpace(),
                            checker
                    );
                    if (split.isPresent()) {
                        RoomSplit roomSplit = split.get();
                        Room a = roomSplit.getRoomA();
                        Room b = roomSplit.getRoomB();
                        detectedRooms.put(a.getDoorPos(), Optional.of(a));
                        detectedRooms.put(b.getDoorPos(), Optional.of(b));
                    } else {
                        // Two doors on a single enclosed space - treat it like one room
                        detectedRooms.remove(r2.getDoorPos());
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
            RoomDetection.WallDetector wd
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
            RoomDetection.WallDetector wd
    ) {
        for (int i = 1; i < eastX - westX; i++) {
            ZWall middleWall = new ZWall(
                    new Position(westX + i, space.getNorthZ()),
                    new Position(westX + i, space.getSouthZ())
            );
            if (ZWallLogic.isConnected(middleWall, wd)) {
                return Optional.of(middleWall);
            }
        }
        return Optional.empty();
    }

    private static Optional<XWall> findMiddleXWall(
            int northZ,
            int southZ,
            InclusiveSpace space,
            RoomDetection.WallDetector wd
    ) {
        for (int i = 1; i < southZ - northZ; i++) {
            XWall middleWall = new XWall(
                    new Position(space.getWestX(), northZ + i),
                    new Position(space.getEastX(), northZ + i)
            );
            if (XWallLogic.isConnected(middleWall, wd)) {
                return Optional.of(middleWall);
            }
        }
        return Optional.empty();
    }

    // TODO: Handle room merging (e.g. removing a dividing wall)

}
