package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;

import java.util.Set;
import java.util.function.Consumer;

public class Rooms {
    private static WallPositionToRooms WALL_POSITION_TO_ROOMS = new WallPositionToRooms();

    public static ImmutableList<InclusiveSpace> wallPositionsToSpaces(Set<Position> positions) {
        return WALL_POSITION_TO_ROOMS.getSpaces(positions);
    }
    public static ImmutableList<InclusiveSpace> wallPositionsToSpaces(Set<Position> positions, Consumer<String> flightRecorder) {
        WALL_POSITION_TO_ROOMS.setFlightRecorder(flightRecorder);
        ImmutableList<InclusiveSpace> spaces = WALL_POSITION_TO_ROOMS.getSpaces(positions);
        if (spaces.isEmpty()) {
            throw new IllegalStateException("No spaces found for positions: " + positions);
        }
        WALL_POSITION_TO_ROOMS.clearFlightRecorder();
        return spaces;
    }
}
