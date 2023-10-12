package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.Wall;
import ca.bradj.roomrecipes.rooms.XWall;

import java.util.Optional;

public class Walls {
    public static boolean isConnected(
            Direction n,
            Wall pNorthWall,
            WallDetector wd
    ) {
        return switch (n) {
            case NORTH, SOUTH -> XWalls.isConnected(pNorthWall.toXWall(), wd);
            case WEST, EAST -> ZWalls.isConnected(pNorthWall.toZWall(), wd);
        };
    }

    public static Optional<Wall<?>> findOpening(
            Direction n,
            Wall<?> metaNorthWall,
            WallDetector wd
    ) {
        return switch (n) {
            case NORTH, SOUTH -> XWalls.findOpening(metaNorthWall.toXWall(), wd).map(v -> v);
            case WEST, EAST -> ZWalls.findOpening(metaNorthWall.toZWall(), wd).map(v -> v);
        };
    }
}
