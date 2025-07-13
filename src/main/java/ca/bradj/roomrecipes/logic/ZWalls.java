package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.ZWall;

import java.util.Optional;

public class ZWalls {

    public static boolean isConnected(ZWall wall, WallDetector wd) {
        if (wall.northCorner.x != wall.southCorner.x) {
            return false;
        }
        for (int i = wall.northCorner.z; i <= wall.southCorner.z; i++) {
            Position shifted = wall.northCorner.WithZ(i);
            if (wd.IsWall(shifted)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static Optional<ZWall> findOpening(
            ZWall zWall,
            WallDetector wd,
            boolean requireNorthWall,
            boolean requireSouthWall
    ) {
        boolean northInvalid = requireNorthWall && !wd.IsWall(zWall.northCorner);
        boolean southInvalid = requireSouthWall && !wd.IsWall(zWall.southCorner);
        if (northInvalid || southInvalid) {
            return Optional.empty();
        }

        boolean wasWall = true;
        int zTop = 0;
        for (int i = zWall.northCorner.z; i <= zWall.southCorner.z; i++) {
            if (wd.IsWall(zWall.northCorner.WithZ(i))) {
                if (wasWall) {
                    zTop = i;
                    continue;
                }
                return Optional.of(new ZWall(zWall.northCorner.WithZ(zTop), zWall.southCorner.WithZ(i)));
            }
            if (i == 0) {
                return Optional.empty();
            }
            if (i == zWall.southCorner.z) {
                if (wasWall) {
                    return Optional.of(new ZWall(zWall.northCorner.WithZ(zTop+1), zWall.southCorner));
                }
                return Optional.empty();
            }
            wasWall = false;
        }
        return Optional.empty();
    }
}
