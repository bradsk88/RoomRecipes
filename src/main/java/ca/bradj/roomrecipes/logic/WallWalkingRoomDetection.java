package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

// Attempts to detect a room by walking along walls, starting at a door position
// and turning clockwise whenever possible.
public class WallWalkingRoomDetection {

    public static Search<ImmutableSet<Position>> tryFindWalls(
            Position doorPos,
            int iteration,
            @Nullable Consumer<String> flightRecorder,
            WallDetector wd
    ) {
        if (flightRecorder == null) {
            flightRecorder = s -> {
            };
        }

        // If iteration is 0, start north of the door
        // If iteration is 1, start east of the door, etc.
        Direction dir = Direction.fromNorthQuarter(iteration);
        Position checkPos = doorPos.relative(dir);
        flightRecorder.accept(String.format("Searching %s at %s for iteration %d from %s", dir, sm(checkPos), iteration, sm(doorPos)));
        if (wd.IsWall(checkPos)) {
            flightRecorder.accept(String.format("Found wall at %s, continuing %s", sm(checkPos), dir));
            Search<ImmutableSet<Position>> result = tryFindWalls(
                    checkPos, Direction::cw, dir, flightRecorder, wd, ImmutableSet.of(
                            doorPos, checkPos
                    ), p -> p.equals(doorPos.relative(dir.opp())),
                    0
            );
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept(String.format("No wall found at %s. Ending iteration.", sm(checkPos)));
        }

        return Search.empty();

    }

    private static String sm(Position checkPos) {
        return checkPos.getUIString();
    }

    private static Search<ImmutableSet<Position>> tryFindWalls(
            Position prevPos,
            Function<Direction, Direction> rotate,
            Direction dir,
            Consumer<String> flightRecorder,
            WallDetector wd,
            Set<Position> wallsSoFar,
            Predicate<Position> isPastStart,
            int depth
    ) {
        if (depth > 1000) {
            flightRecorder.accept("Reached max depth of 1000, giving up.");
            return Search.empty();
        }

        Position checkPos = prevPos.relative(dir);
        flightRecorder.accept(String.format("Checking %s at %s", dir, checkPos));
        if (wd.IsWall(checkPos)) {
            Search<ImmutableSet<Position>> result = tfw(rotate, dir, flightRecorder, wd, wallsSoFar, isPastStart, depth, checkPos);
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept("No wall at " + checkPos);
        }
        // Try the 45 degree diagonal
        dir = rotate.apply(dir);
        checkPos = checkPos.relative(dir);
        flightRecorder.accept(String.format("Checking diagonal %s%s at %s", dir.ccw(), dir, checkPos));
        if (wd.IsWall(checkPos)) {
            Search<ImmutableSet<Position>> result = tfw(rotate, dir, flightRecorder, wd, wallsSoFar, isPastStart, depth, checkPos);
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept("No wall at " + checkPos);
        }
        // Try 90
        checkPos = prevPos.relative(dir);
        flightRecorder.accept(String.format("Checking 90 degrees (%s) at %s", dir, checkPos));
        if (wd.IsWall(checkPos)) {
            Search<ImmutableSet<Position>> result = tfw(rotate, dir, flightRecorder, wd, wallsSoFar, isPastStart, depth, checkPos);
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept("No wall at " + checkPos);
        }
        // Try 135 degree diagonal
        dir = rotate.apply(dir);
        checkPos = checkPos.relative(dir);
        flightRecorder.accept(String.format("Checking 135 degrees (%s) at %s", dir, checkPos));
        if (wd.IsWall(checkPos)) {
            Search<ImmutableSet<Position>> result = tfw(rotate, dir, flightRecorder, wd, wallsSoFar, isPastStart, depth, checkPos);
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept("No wall at " + checkPos);
        }
        // Skip 180
        checkPos = prevPos.relative(dir);
        flightRecorder.accept(String.format("Skipping 180 degrees (%s) at %s", dir, checkPos));

        // Try 225 degree diagonal
        dir = rotate.apply(dir);
        checkPos = checkPos.relative(dir);
        flightRecorder.accept(String.format("Skipping 225 degrees (%s) at %s", dir, checkPos));

        // Try 270
        checkPos = prevPos.relative(dir);
        flightRecorder.accept(String.format("Checking 270 degrees (%s) at %s", dir, checkPos));
        if (wd.IsWall(checkPos)) {
            Search<ImmutableSet<Position>> result = tfw(rotate, dir, flightRecorder, wd, wallsSoFar, isPastStart, depth, checkPos);
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept("No wall at " + checkPos);
        }
        // Try 315 degree diagonal
        dir = rotate.apply(dir);
        checkPos = checkPos.relative(dir);
        flightRecorder.accept(String.format("Checking 315 degrees (%s) at %s", dir, checkPos));
        if (wd.IsWall(checkPos)) {
            Search<ImmutableSet<Position>> result = tfw(rotate, dir, flightRecorder, wd, wallsSoFar, isPastStart, depth, checkPos);
            if (result.isEnd()) {
                return result;
            }
        } else {
            flightRecorder.accept("No wall at " + checkPos);
        }

        return Search.empty();
    }

    private static Search<ImmutableSet<Position>> tfw(
            Function<Direction, Direction> rotate,
            Direction dir,
            Consumer<String> flightRecorder,
            WallDetector wd,
            Set<Position> wallsSoFar,
            Predicate<Position> isPastStart,
            int depth,
            Position checkPos
    ) {
        ImmutableSet<Position> wsf = ImmutableSet.<Position>builder().addAll(wallsSoFar).add(checkPos).build();
        if (isPastStart.test(checkPos)) {
            flightRecorder.accept("Passed start position. Ending.");
            ImmutableSet.Builder<Position> b = ImmutableSet.builder();
            return Search.end(wsf);
        }
        flightRecorder.accept(String.format("Found wall at %s, continuing %s", checkPos, dir));
        ImmutableSet.Builder<Position> b = ImmutableSet.builder();
        Search<ImmutableSet<Position>> result = tryFindWalls(
                checkPos, rotate, dir, flightRecorder,
                wd, wsf,
                isPastStart, depth + 1
        );
        return result;
    }

    public static Search<Room> tryFind(
            Position nextDoor,
            int iteration,
            @Nullable Consumer<String> flightRecorder,
            WallDetector wd
    ) {
        Search<ImmutableSet<Position>> walls = tryFindWalls(nextDoor, iteration, flightRecorder, wd);
        return walls.map(Rooms::wallPositionsToSpaces).map(v -> new Room(nextDoor, v));
    }
}
