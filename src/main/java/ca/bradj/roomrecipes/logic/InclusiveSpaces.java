package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class InclusiveSpaces {

    public static boolean overlapOnXZPlane(
            InclusiveSpace space1,
            InclusiveSpace space2
    ) {
        int space1MinX = space1.getWestX();
        int space1MaxX = space1.getEastX();
        int space2MinX = space2.getWestX();
        int space2MaxX = space2.getEastX();
        int space1MinZ = space1.getNorthZ();
        int space1MaxZ = space1.getSouthZ();
        int space2MinZ = space2.getNorthZ();
        int space2MaxZ = space2.getSouthZ();

        // Check for overlap on x-axis
        boolean overlapX = false;
        if (space1MaxX > space2MinX && space2MaxX > space1MinX) {
            overlapX = true;
        }

        // Check for overlap on z-axis
        boolean overlapZ = false;
        if (space1MaxZ > space2MinZ && space2MaxZ > space1MinZ) {
            overlapZ = true;
        }

        // Check if there is overlap on both axes
        return overlapX && overlapZ;
    }

    public static double calculateArea(InclusiveSpace space) {
        Position cornerA = space.getCornerA();
        Position cornerB = space.getCornerB();
        int length = Math.abs(cornerA.x - cornerB.x) + 1;
        int width = Math.abs(cornerA.z - cornerB.z) + 1;

        return Math.max(0, (double) length * width);
    }

    public static double calculateArea(Collection<? extends InclusiveSpace> spaces) {
        return spaces.stream().mapToDouble(InclusiveSpaces::calculateArea).sum();
    }

    public static Position getRandomEnclosedPosition(
            InclusiveSpace space,
            Function<Integer, Integer> randomInt
    ) {
        int minX = space.getWestX() + 1;
        int maxX = space.getEastX();
        int minZ = space.getNorthZ() + 1;
        int maxZ = space.getSouthZ();
        int width = maxX - minX;
        int height = maxZ - minZ;
        return new Position(minX + randomInt.apply(width), minZ + randomInt.apply(height));
    }

    public static Collection<Position> getAllEnclosedPositions(InclusiveSpace space) {
        int minX = space.getWestX() + 1;
        int maxX = space.getEastX();
        int minZ = space.getNorthZ() + 1;
        int maxZ = space.getSouthZ();
        ImmutableList.Builder<Position> b = ImmutableList.builder();
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                b.add(new Position(x, z));
            }
        }
        return b.build();
    }

    public static Position getMidpoint(InclusiveSpace space) {
        int leftInside = space.getWestX() + 1;
        int rightInside = space.getEastX() - 1;
        int halfWidth = (rightInside - leftInside) / 2;
        int northInside = space.getNorthZ() + 1;
        int southInside = space.getSouthZ() - 1;
        int halfHeight = (southInside - northInside) / 2;
        return new Position(leftInside + halfWidth, northInside + halfHeight);
    }

    public static boolean contains(
            Iterable<? extends InclusiveSpace> spaces,
            Position pos
    ) {
        for (InclusiveSpace space : spaces) {
            if (contains(space, pos)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(
            InclusiveSpace space,
            Position pos
    ) {
        if (pos.x < space.getWestX() + 1) {
            return false;
        }
        if (pos.x > space.getEastX()) {
            return false;
        }
        if (pos.z < space.getNorthZ() + 1) {
            return false;
        }
        if (pos.z > space.getSouthZ()) {
            return false;
        }
        return true;
    }

    // Returns true if the space has all four outside walls intact and no
    // internal dividing walls.
    public static boolean isWhole(
            InclusiveSpace space,
            Predicate<Position> isWallO,
            boolean requireCorners
    ) {
        Map<Position, Boolean> cache = new HashMap<>();
        Predicate<Position> isWall = p -> cache.compute(p, (p2, r) -> r != null ? r : isWallO.test(p2));
        if (!hasNorthAndSouthWalls(space, isWall, requireCorners)) return false;
        if (!hasWestAndEastWalls(space, isWall, requireCorners)) return false;
        for (int x = space.getWestX() + 1; x < space.getEastX() - 1; x++) {
            int xx = x;
            if (hasZWall(space, z -> isWall.test(new Position(xx, z)), requireCorners)) {
                return false;
            }
        }
        for (int z = space.getNorthZ() + 1; z < space.getSouthZ() - 1; z++) {
            int zz = z;
            if (hasZWall(space, x -> isWall.test(new Position(x, zz)), requireCorners)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasWestAndEastWalls(
            InclusiveSpace space,
            Predicate<Position> isWall,
            boolean requireCorners
    ) {
        return hasZWall(space, z -> {
            Position west = new Position(space.getWestX(), z);
            Position east = new Position(space.getWestX(), z);
            return isWall.test(west) && isWall.test(east);
        }, requireCorners);
    }

    private static boolean hasZWall(
            InclusiveSpace space,
            Predicate<Integer> test,
            boolean requireCorners
    ) {

        int top = requireCorners ? space.getNorthZ(): space.getNorthZ() + 1;
        int bot = requireCorners ? space.getSouthZ() : space.getSouthZ() - 1;
        for (int z = top; z < bot; z++) {
            if (!test.test(z)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasXWall(
            InclusiveSpace space,
            Predicate<Integer> test,
            boolean requireCorners
    ) {

        int neg = requireCorners ? space.getWestX() : space.getWestX() + 1;
        int pos = requireCorners ? space.getEastX() : space.getEastX() - 1;
        for (int x = neg; x < pos; x++) {
            if (!test.test(x)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasNorthAndSouthWalls(
            InclusiveSpace space,
            Predicate<Position> isWall,
            boolean requireCorners
    ) {

        return hasXWall(space, x -> {
            Position north = new Position(x, space.getNorthZ());
            Position south = new Position(x, space.getSouthZ());
            return isWall.test(north) && isWall.test(south);
        }, requireCorners);
    }

    public static boolean fullyContains(
            InclusiveSpace inclusiveSpace,
            InclusiveSpace ss
    ) {
        if (ss.getWestX() < inclusiveSpace.getWestX()) {
            return false;
        }
        if (ss.getNorthZ() < inclusiveSpace.getNorthZ()) {
            return false;
        }
        if (ss.getEastX() > inclusiveSpace.getEastX()) {
            return false;
        }
        if (ss.getSouthZ() > inclusiveSpace.getSouthZ()) {
            return false;
        }
        return true;
    }
}
