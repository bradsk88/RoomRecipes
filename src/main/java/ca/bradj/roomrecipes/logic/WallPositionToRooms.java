package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.adapter.Positions;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WallPositionToRooms {

    private final int limit;
    private Consumer<String> flightRecorder = (str) -> {};

    public WallPositionToRooms() {
        this(1000);
    }

    public WallPositionToRooms(int limit) {
        this.limit = limit;
    }

    public ImmutableList<InclusiveSpace> getSpaces(Set<Position> positions) {

        ImmutableList<InclusiveSpace> space = getSpaceFromOuterBorder(positions);
        if (!space.isEmpty()) {
            return space;
        }

        return getSpacesByClampingWalls(positions);
    }

    private ImmutableList<InclusiveSpace> getSpaceFromOuterBorder(Set<Position> positions) {
        if (positions.isEmpty()) {
            flightRecorder.accept("No border positions provided. Space cannot be found.");
            return ImmutableList.of();
        }

        OptionalInt leftX = positions.stream().mapToInt(v -> v.x).min();
        OptionalInt topY = positions.stream().mapToInt(v -> v.z).min();
        OptionalInt rightX = positions.stream().mapToInt(v -> v.x).max();
        OptionalInt bottomY = positions.stream().mapToInt(v -> v.z).max();

        if (leftX.getAsInt() >= rightX.getAsInt() - 1) {
            flightRecorder.accept("Parallel sides too close on X axis. Space cannot be found");
            return ImmutableList.of();
        }
        if (topY.getAsInt() >= bottomY.getAsInt() - 1) {
            flightRecorder.accept("Parallel sides too close on Z axis. Space cannot be found");
            return ImmutableList.of();
        }
        InclusiveSpace outer = InclusiveSpace.from(leftX.getAsInt(), topY.getAsInt())
                                             .to(rightX.getAsInt(), bottomY.getAsInt());

        flightRecorder.accept("Checking if the outer walls can be used to define a room: " + InclusiveSpaces.getShortString(outer));

        if (!positions.containsAll(InclusiveSpaces.getWallPositions(outer, false))) {
            flightRecorder.accept("Positions are not a complete room border: " + Positions.getUIString(positions));
            flightRecorder.accept("Space cannot be found");
            return ImmutableList.of();
        }

        InclusiveSpace nextLevel = InclusiveSpace.from(outer.getCornerA().offset(1, 1))
                                                 .to(outer.getCornerB().offset(-1, -1));
        ImmutableSet<Position> nextRing = InclusiveSpaces.getWallPositions(nextLevel, true);
        Set<Position> nextRingWalls = nextRing.stream().filter(positions::contains).collect(Collectors.toSet());
        if (nextRingWalls.isEmpty()) {
            flightRecorder.accept("All spaces just-inside the outer walls are empty. This means the outer walls are a room.");
            return ImmutableList.of(outer);
        }

        flightRecorder.accept("Some spaces just-inside the outer wall are NOT empty. More advanced space detection will be required");

        return ImmutableList.of();
    }

    private @NotNull ImmutableList<InclusiveSpace> getSpacesByClampingWalls(Set<Position> positions) {
        ImmutableList<InclusiveSpace> spaces = findOuterSpaces(positions);
        if (spaces.size() < 2) {
            return spaces;
        }
        spaces = tryToInsertMiddleSpace(spaces);
        return spaces;
    }

    private ImmutableList<InclusiveSpace> tryToInsertMiddleSpace(ImmutableList<InclusiveSpace> spaces) {
        // Find the west-most, east-most, north-most, and south-most spaces
        // Then grab the eastmost coordinate of the west-most space, etc.
        InclusiveSpace westMost = spaces.stream().min(Comparator.comparingInt(InclusiveSpace::getWestX)).orElseThrow();
        InclusiveSpace eastMost = spaces.stream().max(Comparator.comparingInt(InclusiveSpace::getEastX)).orElseThrow();
        InclusiveSpace northMost = spaces.stream().min(Comparator.comparingInt(InclusiveSpace::getNorthZ)).orElseThrow();
        InclusiveSpace southMost = spaces.stream().max(Comparator.comparingInt(InclusiveSpace::getSouthZ)).orElseThrow();

        // Attempt to find a middle space that connects these four spaces
        if (westMost.getEastX() >= eastMost.getWestX()) {
            flightRecorder.accept("No middle space can be found because the east space is connected to the west space");
            return spaces;
        }
        if (northMost.getSouthZ() >= southMost.getNorthZ()) {
            flightRecorder.accept("No middle space can be found because the south space is connected to the north space");
            return spaces;
        }
        InclusiveSpace middle = InclusiveSpace.from(westMost.getEastX(), northMost.getSouthZ())
                                              .to(eastMost.getWestX(), southMost.getNorthZ());
        flightRecorder.accept("Found middle space: " + InclusiveSpaces.getShortString(middle));
        ImmutableList.Builder<InclusiveSpace> builder = ImmutableList.builder();
        return builder.addAll(spaces).add(middle).build();
    }

    private @NotNull ImmutableList<InclusiveSpace> findOuterSpaces(Set<Position> positions) {
        flightRecorder.accept("Applying clamping strategy to find outer walls");
        List<InclusiveSpace> b = new ArrayList<>();

        Set<Position> coveredSoFar = new HashSet<>();

        ImmutableSet<Position> initPositions = ImmutableSet.copyOf(removeCorners(positions));

        flightRecorder.accept("Checking east walls");
        InclusiveSpace rect = clampRect(positions);
        Sets.SetView<Position> difference = Sets.difference(initPositions, initPositions);
        if (rect != null) {
            flightRecorder.accept("A space was detected on the outer border of the west walls: " + InclusiveSpaces.getShortString(rect));
            b.add(rect);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(rect));
            difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                flightRecorder.accept("There are no remaining walls to account for");
                return ImmutableList.copyOf(b);
            }
        }
        flightRecorder.accept("Checking west walls (by negating X/Z plane)");
        rect = clampRect(positions.stream().map(v -> new Position(-v.x, -v.z)).toList());
        if (rect != null) {
            Position a = rect.getCornerA();
            Position bb = rect.getCornerB();
            InclusiveSpace unspun = InclusiveSpace.from(-a.x, -a.z).to(-bb.x, -bb.z);
            flightRecorder.accept("A space was detected on the outer border of the east walls: " + InclusiveSpaces.getShortString(unspun));
            b.add(unspun);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(unspun));
            difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                flightRecorder.accept("There are no remaining walls to account for");
                return ImmutableList.copyOf(b);
            }
        }

        flightRecorder.accept("Checking north walls (by pivoting X/Z plane)");
        positions = ImmutableSet.copyOf(initPositions);
        rect = clampRect(positions.stream().map(v -> new Position(v.z, v.x)).toList());
        if (rect != null) {
            Position a = rect.getCornerA();
            Position bb = rect.getCornerB();
            InclusiveSpace unspun = InclusiveSpace.from(a.z, a.x).to(bb.z, bb.x);
            flightRecorder.accept("A space was detected on the outer border of the north walls: " + InclusiveSpaces.getShortString(unspun));
            b.add(unspun);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(unspun));
            difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                flightRecorder.accept("There are no remaining walls to account for");
                return ImmutableList.copyOf(b);
            }
        }

        flightRecorder.accept("Checking south walls (by pivoting and negating X/Z plane)");
        rect = clampRect(positions.stream().map(v -> new Position(-v.z, -v.x)).toList());
        if (rect != null) {
            Position a = rect.getCornerA();
            Position bb = rect.getCornerB();
            InclusiveSpace unspun = InclusiveSpace.from(-a.z, -a.x).to(-bb.z, -bb.x);
            flightRecorder.accept("A space was detected on the outer border of the south walls: " + InclusiveSpaces.getShortString(unspun));
            b.add(unspun);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(unspun));
            difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                flightRecorder.accept("There are no remaining walls to account for");
                return ImmutableList.copyOf(b);
            }
        }

        for (Position position : difference) {
            flightRecorder.accept("Attempting to find space above position: " + position.getUIString());
            Set<Position> verticallyAligned = positions.stream().filter(v -> v.x == position.x)
                                                       .collect(Collectors.toSet());
            Optional<Position> above = verticallyAligned.stream().filter(v -> v.z < position.z - 1)
                                                        .max(Comparator.comparingInt(v -> v.z));
            if (above.isPresent()) {
                flightRecorder.accept("Found position above: " + above.get().getUIString());
                InclusiveSpace e = InclusiveSpace.from(position.x, above.get().z).to(position.x, position.z);
                flightRecorder.accept("Adding space: " + InclusiveSpaces.getShortString(e));
                b.add(e);
                coveredSoFar.add(position);
                difference = Sets.difference(positions, coveredSoFar);
                if (difference.isEmpty()) {
                    flightRecorder.accept("There are no remaining walls to account for");
                    return ImmutableList.copyOf(b);
                }
            }
        }

        flightRecorder.accept("Unprocessed wall positions remain. Returning empty result. Remaining positions: " + Positions.getUIString(difference.immutableCopy()));
        return ImmutableList.of();
    }

    private ImmutableSet<Position> removeCorners(Set<Position> positions) {
        // Get min and max X/Z coordinates
        OptionalInt minX = positions.stream().mapToInt(p -> p.x).min();
        OptionalInt minZ = positions.stream().mapToInt(p -> p.z).min();
        OptionalInt maxX = positions.stream().mapToInt(p -> p.x).max();
        OptionalInt maxZ = positions.stream().mapToInt(p -> p.z).max();

        return positions.stream()
                .filter(p -> !(p.x == minX.getAsInt() && p.z == minZ.getAsInt())) // Top-left corner
                .filter(p -> !(p.x == maxX.getAsInt() && p.z == minZ.getAsInt())) // Top-right corner
                .filter(p -> !(p.x == minX.getAsInt() && p.z == maxZ.getAsInt())) // Bottom-left corner
                .filter(p -> !(p.x == maxX.getAsInt() && p.z == maxZ.getAsInt())) // Bottom-right corner
                .collect(ImmutableSet.toImmutableSet());
    }

    private ImmutableList<InclusiveSpace> simplify(
            InclusiveSpace a,
            InclusiveSpace b
    ) {

        if (a.getWestX() == b.getWestX() && a.getEastX() == b.getEastX()) {
            // They are on the same X wall
            if (a.getNorthZ() == b.getSouthZ() || a.getSouthZ() == b.getNorthZ()) {
                return ImmutableList.of(InclusiveSpace.from(a.getWestX(), a.getNorthZ())
                                                      .to(a.getEastX(), b.getSouthZ()));
            }
        } else if (a.getNorthZ() == b.getNorthZ() && a.getSouthZ() == b.getSouthZ()) {
            // They are on the same Z wall
            if (a.getWestX() == b.getEastX() || a.getEastX() == b.getWestX()) {
                return ImmutableList.of(InclusiveSpace.from(a.getWestX(), a.getNorthZ())
                                                      .to(b.getEastX(), a.getSouthZ()));
            }
        }
        return ImmutableList.of(a, b);
    }

    private @Nullable InclusiveSpace clampRect(
            Collection<Position> positions
    ) {
        InclusiveSpace rect = null;
        OptionalInt leftmost = positions.stream().mapToInt(p -> p.x).min();
        List<Position> leftside = positions.stream()
                                           .filter(p -> p.x == leftmost.getAsInt())
                                           .sorted(Comparator.comparingInt(a -> a.z))
                                           .toList();
        if (leftside.isEmpty()) {
            return null;
        }
        Position topOfLeft = leftside.get(0);
        Direction dir = Direction.NORTH;
        Position lastChecked = topOfLeft;
        for (Position position : leftside) {
            if (position.equals(topOfLeft)) {
                continue;
            }
            if (position.relative(dir).equals(lastChecked)) {
                lastChecked = position;
                continue;
            }

            rect = clampDown(positions, dir.cw(), topOfLeft, lastChecked);

            // We have detected the top-left and bottom-left of a rectangle
            // Find the rest of the rectangle before moving on.
            rect = findRectangle(positions, dir, rect);
            if (rect != null) {
                return rect;
            }

            topOfLeft = null;
            lastChecked = null;
        }

        if (topOfLeft != null) {
            rect = clampDown(positions, dir.cw(), topOfLeft, lastChecked);

            // We have detected the top-left and bottom-left of a rectangle
            // Find the rest of the rectangle before moving on.
            rect = findRectangle(positions, dir, rect);
        }
        return rect;
    }

    private InclusiveSpace clampDown(
            Collection<Position> positions,
            Direction dir,
            Position topLeft,
            Position bottomLeft
    ) {

        Position topRight = topLeft.relative(dir).relative(dir.ccw());
        Position bottomRight = bottomLeft.relative(dir).relative(dir.cw());

        flightRecorder.accept("Clamping down from " + topRight.getUIString() + " to " + bottomRight.getUIString() + " in " + dir + " direction");
        for (int i = -1; i < bottomRight.z - topRight.z; i++) {
            Position iTR = topRight.relative(dir.cw(), 1);
            Position iBR = bottomRight.relative(dir.ccw(), 1);
            boolean hadTop = positions.contains(iTR);
            if (hadTop) {
                topRight = iTR;
                flightRecorder.accept("Clamped top-right to " + iTR.getUIString());
            }
            boolean hadBot = positions.contains(iBR);
            if (hadBot) {
                bottomRight = iBR;
                flightRecorder.accept("Clamped bottom-right to " + iBR.getUIString());
            }
        }
        InclusiveSpace rect = InclusiveSpace.from(topLeft.WithZ(topRight.z)).to(bottomRight);
        flightRecorder.accept("Found rectangle: " + InclusiveSpaces.getShortString(rect));
        return rect;
    }

    private InclusiveSpace findRectangle(
            Collection<Position> positions,
            Direction dir,
            InclusiveSpace space
    ) {
        if (positions.size() <= 2) {
            return null;
        }
        Direction nextDir = dir.cw();
        flightRecorder.accept("Attempting to extend " + nextDir + "-ward from " + InclusiveSpaces.getShortString(space));
        Position top = space.getEastZWall().northCorner;
        Position bot = space.getEastZWall().southCorner;
        Position nextTop = top, nextBot = bot;
        for (int i = 0; i < limit; i++) {
            Position prevBot = nextBot;
            nextTop = nextTop.relative(nextDir);
            nextBot = nextBot.relative(nextDir);
            if (positions.containsAll(ZWalls.getWallPositions(new ZWall(nextTop, nextBot)))) {
                flightRecorder.accept("Found connection between " + nextTop.getUIString() + " and " + nextBot.getUIString());
                return InclusiveSpace.from(space.getCornerA()).to(nextBot);
            }
            if (positions.contains(nextTop) && positions.contains(nextBot)) {
                continue;
            }
            if (i == 0) {
                break;
            }
            return InclusiveSpace.from(space.getCornerA()).to(prevBot);
        }
        return space;
    }

    public void setFlightRecorder(Consumer<String> flightRecorder) {
        this.flightRecorder = flightRecorder;
    }

    public void clearFlightRecorder() {
        this.flightRecorder = str -> {};
    }
}
