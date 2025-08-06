package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;

import java.util.Set;

public class Rooms {
    private static WallPositionToRooms WALL_POSITION_TO_ROOMS = new WallPositionToRooms();

    public static ImmutableList<InclusiveSpace> wallPositionsToSpaces(Set<Position> positions) {
        return WALL_POSITION_TO_ROOMS.getSpaces(positions);
    }
}
