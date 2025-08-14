package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

// Attempts to detect a room by walking along walls, starting at a door position
// and turning clockwise whenever possible.
public class WallWalkingRoomDetection {

    public static Search<ImmutableSet<Position>> tryFindWalls(
            Position doorPos,
            int iteration,
            @Nullable Consumer<String> flightRecorder,
            WallDetector wd
    ) {
        List<Position> visitedSink = new ArrayList<>();

        if (flightRecorder == null) {
            flightRecorder = s -> {
            };
        }

        if (!wd.IsWall(doorPos)) {
            return Search.empty();
        }

        // If iteration is 0, start north of the door
        // If iteration is 1, start east of the door, etc.
        Direction dir = Direction.fromNorthQuarter(iteration);
        Position checkPos = doorPos.relative(dir);
        flightRecorder.accept(String.format("Searching %s at %s for iteration %d from %s", dir, sm(checkPos), iteration, sm(doorPos)));
        if (wd.IsWall(checkPos)) {
            flightRecorder.accept(String.format("Found wall at %s, continuing facing %s", sm(checkPos), dir));

            Crawl.Checks checks = new Crawl.Checks() {
                @Override
                public boolean isWall(Position pos) {
                    return wd.IsWall(pos);
                }

                @Override
                public boolean isOrigin(Position pos) {
                    return pos.equals(doorPos.relative(dir.opp())) || pos.equals(doorPos);
                }
            };

            List<Crawl> crawls = Crawl.forwardSpread(checkPos, dir, checks, ImmutableSet.of(doorPos, checkPos), visitedSink);
            Search<ImmutableSet<Position>> result = tryFindWalls(crawls, flightRecorder, 0, visitedSink);
            if (result.isEnd()) {
                return result;
            }
        } else {
        }

        return Search.empty();

    }

    private static String sm(Position checkPos) {
        return checkPos.getUIString();
    }

    private static Search<ImmutableSet<Position>> tryFindWalls(
            Collection<Crawl> crawls,
            Consumer<String> flightRecorder,
            int depth,
            List<Position> visitedSink
    ) {
        if (depth > 1000) {
            flightRecorder.accept("Reached max depth of 1000, giving up.");
            return Search.empty();
        }

        ImmutableList.Builder<Crawl> b = ImmutableList.builder();

        Set<Position> positions = new HashSet<>();

        for (Crawl crawl : crawls) {
            flightRecorder.accept(crawl.toString());

            if (crawl.isOrigin()) {

                ImmutableSet<Position> ps = crawl.getCheckedPositions();
                String allPositions = ps.stream()
                                               .sorted()
                                               .map(Position::getUIString)
                                               .reduce((a, b1) -> a + ", " + b1)
                                               .orElse("none");
                if (crawl.getWidth() < 3 || crawl.getHeight() < 3) {
                    flightRecorder.accept("Area too small. Ignoring crawl: " + allPositions);
                    continue;
                }

                flightRecorder.accept("Reached origin, recording wall positions:" + allPositions);
                // Instead of returning immediately, we collect positions from
                // (potentially) multiple parallel crawls. This allows us to
                // handle mirrored rooms with a door in the adjoining wall.
                positions.addAll(ps);
                continue;
            }

            ImmutableList<Crawl> nextSteps = crawl.getNextSteps(visitedSink);
            if (nextSteps.isEmpty()) {
                continue;
            }
            flightRecorder.accept("--> Detected wall <--");
            b.addAll(nextSteps);
        }

        if (!positions.isEmpty()) {
            String allPositions = positions.stream()
                    .sorted()
                    .map(Position::getUIString)
                    .reduce((a, b1) -> a + ", " + b1)
                    .orElse("none");
            flightRecorder.accept("One or more rooms found connected to door. Returning combined positions: " + allPositions);
            return Search.end(ImmutableSet.copyOf(positions));
        }

        crawls = b.build();
        if (crawls.isEmpty()) {
            return Search.empty();
        }

        return tryFindWalls(crawls, flightRecorder, depth + 1, visitedSink);
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
