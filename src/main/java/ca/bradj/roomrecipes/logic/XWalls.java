package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.XWall;

import java.util.Optional;

public class XWalls {
    public static boolean isConnected(XWall wall, WallDetector wd) {
        if (wall.westCorner.z != wall.eastCorner.z) {
            return false;
        }
        for (int i = wall.westCorner.x; i <= wall.eastCorner.x; i++) {
            Position shifted = wall.westCorner.WithX(i);
            if (wd.IsWall(shifted)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static Optional<XWall> eastFromCorner(
            WallDetector wd,
            Position northCorner,
            int maxDistFromDoor
    ) {
        int eastX = northCorner.x;
        for (int i = 0; i < maxDistFromDoor; i++) {
            if (!wd.IsWall(northCorner.WithX(northCorner.x + i))) {
                break;
            }
            eastX = northCorner.x + i;
        }
        if (eastX == northCorner.x)
            return Optional.empty();
        return Optional.of(
                new XWall(northCorner, northCorner.WithX(eastX))
        );
    }
    public static Optional<XWall> westFromCorner(
            WallDetector wd,
            Position northCorner,
            int maxDistFromDoor
    ) {
        int westX = northCorner.x;
        for (int i = 0; i > -maxDistFromDoor; i--) {
            if (!wd.IsWall(northCorner.WithX(northCorner.x + i))) {
                break;
            }
            westX = northCorner.x + i;
        }
        if (westX == northCorner.x)
            return Optional.empty();
        return Optional.of(
                new XWall(northCorner.WithX(westX), northCorner)
        );
    }
}
