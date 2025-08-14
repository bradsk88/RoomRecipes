package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WallPositionToRooms {

    private final int limit;

    public WallPositionToRooms() {
        this(1000);
    }

    public WallPositionToRooms(int limit) {
        this.limit = limit;
    }

    public ImmutableList<InclusiveSpace> getSpaces(Set<Position> positions) {

        ImmutableList.Builder<InclusiveSpace> b = ImmutableList.builder();

        Set<Position> coveredSoFar = new HashSet<>();

        ImmutableSet<Position> initPositions = ImmutableSet.copyOf(positions);

        InclusiveSpace rect = getRect(positions, b);
        if (rect != null) {
            b.add(rect);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(rect));
            Sets.SetView<Position> difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                return b.build();
            }
            positions = ImmutableSet.<Position>builder()
                                    .addAll(difference)
                                    .addAll(ZWalls.getWallPositions(rect.getEastZWall()))
                                    .build();
        }
        rect = getRect(positions.stream().map(v -> new Position(-v.x, -v.z)).toList(), b);
        if (rect != null) {
            Position a = rect.getCornerA();
            Position bb = rect.getCornerB();
            InclusiveSpace unspun = InclusiveSpace.from(-a.x, -a.z).to(-bb.x, -bb.z);
            b.add(unspun);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(unspun));
            Sets.SetView<Position> difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                return b.build();
            }
        }

        b = ImmutableList.builder();
        positions = ImmutableSet.copyOf(initPositions);
        rect = getRect(positions.stream().map(v -> new Position(v.z, v.x)).toList(), b);
        if (rect != null) {
            Position a = rect.getCornerA();
            Position bb = rect.getCornerB();
            InclusiveSpace unspun = InclusiveSpace.from(a.z, a.x).to(bb.z, bb.x);
            b.add(unspun);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(unspun));
            Sets.SetView<Position> difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                return b.build();
            }
        }
        rect = getRect(positions.stream().map(v -> new Position(-v.z, -v.x)).toList(), b);
        if (rect != null) {
            Position a = rect.getCornerA();
            Position bb = rect.getCornerB();
            InclusiveSpace unspun = InclusiveSpace.from(-a.z, -a.x).to(-bb.z, -bb.x);
            b.add(unspun);
            coveredSoFar.addAll(InclusiveSpaces.getWallPositions(unspun));
            Sets.SetView<Position> difference = Sets.difference(positions, coveredSoFar);
            if (difference.isEmpty()) {
                return b.build();
            }
        }
        return simplify(b.build());
    }

    private ImmutableList<InclusiveSpace> simplify(ImmutableList<InclusiveSpace> build) {
        if (build.size() != 2) {
            return build;
        }
        InclusiveSpace a = build.get(0);
        InclusiveSpace b = build.get(1);

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
        return build;
    }

    private @Nullable InclusiveSpace getRect(
            Collection<Position> positions,
            ImmutableList.Builder<InclusiveSpace> b
    ) {
        InclusiveSpace rect = null;
        OptionalInt leftmost = positions.stream().mapToInt(p -> p.x).min();
        List<Position> leftside = positions.stream()
                                           .filter(p -> p.x == leftmost.getAsInt())
                                           .sorted(Comparator.comparingInt(a -> a.z))
                                           .toList();
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
            // We have detected the top-left and bottom-left of a rectangle
            // Find the rest of the rectangle before moving on.
            rect = findRectangle(positions, dir, topOfLeft, lastChecked);
            if (rect != null) {
                return rect;
            }

            topOfLeft = null;
            lastChecked = null;
        }

        if (topOfLeft != null) {
            rect = findRectangle(positions, dir, topOfLeft, lastChecked);
        }
        return rect;
    }

    private InclusiveSpace findRectangle(
            Collection<Position> positions,
            Direction dir,
            Position top,
            Position bot
    ) {
        if (positions.size() <= 2) {
            return null;
        }
        Direction nextDir = dir.cw();
        Position nextTop = top, nextBot = bot;
        for (int i = 0; i < limit; i++) {
            Position prevBot = nextBot;
            nextTop = nextTop.relative(nextDir);
            nextBot = nextBot.relative(nextDir);
            if (positions.contains(nextTop) && positions.contains(nextBot)) {
                continue;
            }
            if (i == 0) {
                break;
            }
            return InclusiveSpace.from(top).to(prevBot);
        }
        nextBot = bot.relative(nextDir.cw());
        for (int i = 0; i < limit; i++) {
            Position prevBot = nextBot;
            nextTop = nextTop.relative(nextDir);
            nextBot = nextBot.relative(nextDir);
            if (positions.contains(nextTop) && positions.contains(nextBot)) {
                continue;
            }
            return InclusiveSpace.from(top).to(prevBot);
        }
        return null;
    }
}
