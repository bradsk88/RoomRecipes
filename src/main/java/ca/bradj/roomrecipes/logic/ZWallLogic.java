package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.rooms.ZWall;

public class ZWallLogic {

    public static boolean isConnected(ZWall wall, RoomDetection.WallDetector wd) {
        for (int i = wall.northCorner.z; i < wall.southCorner.z; i++) {
            if (wd.IsWall(wall.northCorner.WithZ(i))) {
                continue;
            }
            return false;
        }
        return true;
    }

}
