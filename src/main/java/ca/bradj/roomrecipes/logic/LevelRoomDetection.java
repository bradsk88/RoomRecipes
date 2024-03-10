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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class LevelRoomDetection {

    public static ImmutableMap<Position, Optional<Room>> findRooms(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            WallDetector checker
    ) {
        return findRooms(currentDoors, maxDistanceFromDoor, null, checker);
    }

    public static ImmutableMap<Position, Optional<Room>> findRooms(
            Collection<Position> currentDoors,
            int maxDistanceFromDoor,
            @Nullable LinkedBlockingQueue<String> fRec,
            WallDetector checker
    ) {
        LevelRoomDetector d = new LevelRoomDetector(
                currentDoors,
                maxDistanceFromDoor,
                1000,
                checker,
                false,
                fRec
        );
        for (int i = 0; i < 2000; i++) {
            @Nullable ImmutableMap<Position, Optional<Room>> result = d.proceed();
            if (result != null) {
                return result;
            }
        }
        throw new IllegalStateException("Room detector should self-close");
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
        int zDoorDiff = Math.max(
                doorPos1.z,
                doorPos2.z
        ) - Math.min(
                doorPos1.z,
                doorPos2.z
        );
        if (xDoorDiff >= zDoorDiff) {
            ZWall westWall = space.getWestZWall();
            ZWall eastWall = space.getEastZWall();
            Optional<ZWall> middleWall = findMiddleZWall(
                    westDP.x,
                    eastDP.x,
                    space,
                    wd
            );
            if (middleWall.isPresent()) {
                return Optional.of(new RoomSplit(
                        new Room(
                                westDP,
                                Positions.getInclusiveSpace(ImmutableList.of(
                                        westWall.northCorner,
                                        middleWall.get().northCorner,
                                        middleWall.get().southCorner,
                                        westWall.southCorner
                                ))
                        ),
                        new Room(
                                eastDP,
                                Positions.getInclusiveSpace(ImmutableList.of(
                                        middleWall.get().northCorner,
                                        eastWall.northCorner,
                                        eastWall.southCorner,
                                        middleWall.get().southCorner
                                ))
                        )
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
        Optional<XWall> middleWall = findMiddleXWall(
                northDP.z,
                southDP.z,
                space,
                wd
        );
        if (middleWall.isPresent()) {
            return Optional.of(new RoomSplit(
                    new Room(
                            northDP,
                            Positions.getInclusiveSpace(ImmutableList.of(
                                    northWall.westCorner,
                                    northWall.eastCorner,
                                    middleWall.get().eastCorner,
                                    middleWall.get().westCorner
                            ))
                    ),
                    new Room(
                            southDP,
                            Positions.getInclusiveSpace(ImmutableList.of(
                                    middleWall.get().westCorner,
                                    middleWall.get().eastCorner,
                                    southWall.eastCorner,
                                    southWall.westCorner
                            ))
                    )
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
                    new Position(
                            westX + i,
                            space.getNorthZ()
                    ),
                    new Position(
                            westX + i,
                            space.getSouthZ()
                    )
            );
            if (ZWalls.isConnected(
                    middleWall,
                    wd
            )) {
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
                    new Position(
                            space.getWestX(),
                            northZ + i
                    ),
                    new Position(
                            space.getEastX(),
                            northZ + i
                    )
            );
            if (XWalls.isConnected(
                    middleWall,
                    wd
            )) {
                return Optional.of(middleWall);
            }
        }
        return Optional.empty();
    }

    // TODO: Handle room merging (e.g. removing a dividing wall)

}
