package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.function.Function;

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

    public static boolean contains(Iterable<? extends InclusiveSpace> spaces, Position pos) {
        for (InclusiveSpace space : spaces) {
            if (pos.x < space.getWestX() + 1) {
                continue;
            }
            if (pos.x > space.getEastX()) {
                continue;
            }
            if (pos.z < space.getNorthZ() + 1) {
                continue;
            }
            if (pos.z > space.getSouthZ()) {
                continue;
            }
            return true;
        }
        return false;
    }
}
