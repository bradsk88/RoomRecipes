package ca.bradj.roomrecipes.rooms;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.Direction;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;

import java.util.Objects;
import java.util.Optional;

// ZWall is a wall that runs from north to south
public class ZWall implements Wall<ZWall> {
    public final Position northCorner;
    public final Position southCorner;

    public ZWall(
            Position northCorner,
            Position southCorner
    ) {
        Position nc = northCorner;
        Position sc = southCorner;
        if (nc.z > sc.z) {
            nc = southCorner;
            sc = northCorner;
        }
        this.northCorner = nc;
        this.southCorner = sc;
    }
    public static Optional<ZWall> northFromCorner(
            WallDetector wd,
            Position cornerPos,
            int maxDistFromCorner
    ) {
        int southCornerZ = -Integer.MAX_VALUE, northCornerZ = Integer.MAX_VALUE;
        boolean started = false;
        for (int i = 0; i < maxDistFromCorner; i++) {
            Position op = cornerPos.offset(0, -i);
            if (wd.IsWall(op)) {
                started = true;
                southCornerZ = Math.max(southCornerZ, op.z);
                northCornerZ = Math.min(northCornerZ, op.z);
            } else if (started) {
                break;
            }
        }
        if (!started) {
            return Optional.empty();
        }
        if (Math.abs(northCornerZ - southCornerZ) < 2) {
            return Optional.empty();
        }
        return Optional.of(
                new ZWall(cornerPos.WithZ(southCornerZ), cornerPos.WithZ(northCornerZ))
        );
    }

    public static Optional<ZWall> southFromCorner(
            WallDetector wd,
            Position doorPos,
            int maxDistFromCorner
    ) {
        int southCornerZ = -Integer.MAX_VALUE, northCornerZ = Integer.MAX_VALUE;
        boolean started = false;
        for (int i = 0; i < maxDistFromCorner; i++) {
            Position op = doorPos.offset(0, i);
            if (wd.IsWall(op)) {
                started = true;
                southCornerZ = Math.max(southCornerZ, op.z);
                northCornerZ = Math.min(northCornerZ, op.z);
            } else if (started) {
                break;
            }
        }
        if (!started) {
            return Optional.empty();
        }
        if (Math.abs(northCornerZ - southCornerZ) < 2) {
            return Optional.empty();
        }
        return Optional.of(
                new ZWall(doorPos.WithZ(southCornerZ), doorPos.WithZ(northCornerZ))
        );
    }

    public int getLength() {
        return southCorner.z - northCorner.z;
    }

    public ZWall shortenSouthEnd(int i) {
        return new ZWall(northCorner, southCorner.WithZ(southCorner.z - i));
    }
    public ZWall shortenNorthEnd(int i) {
        return new ZWall(northCorner.WithZ(northCorner.z + i), southCorner);
    }

    public ZWall extendSouthEnd(int i) {
        return shortenSouthEnd(-i);
    }
    public ZWall extendNorthEnd(int i) {
        return shortenNorthEnd(-i);
    }

    public ZWall shiftedWest(int i) {
        return shiftedEast(-i);
    }

    public ZWall shiftedEast(int i) {
        return new ZWall(northCorner.offset(i, 0), southCorner.offset(i, 0));
    }

    @Override
    public String toString() {
        return "ZWall{" +
                "northCorner=" + northCorner +
                ", southCorner=" + southCorner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZWall zWall = (ZWall) o;
        return Objects.equals(northCorner, zWall.northCorner) && Objects.equals(
                southCorner,
                zWall.southCorner
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(northCorner, southCorner);
    }

    public Position getMidpoint() {
        return new Position(northCorner.x, (northCorner.z + southCorner.z) / 2);
    }

    public boolean sameHeight(ZWall wall) {
        return this.northCorner.z == wall.northCorner.z && this.southCorner.z == wall.southCorner.z;
    }

    public boolean isSameContentAs(
            ZWall shifted,
            WallDetector wd
    ) {
        for (int i = 0; i < shifted.southCorner.z; i++) {
            boolean isWall = wd.IsWall(northCorner.offset(0, i));
            boolean shiftedIsWall = wd.IsWall(shifted.northCorner.offset(0, i));
            if (isWall != shiftedIsWall) {
                return false;
            }
        }
        return true;
    }

    public int getX() {
        return northCorner.x;
    }

    public boolean isLargerThan(ZWall zWall) {
        return southCorner.z - northCorner.z > zWall.southCorner.z - zWall.northCorner.z;
    }

    @Override
    public boolean sameLengthOnAxis(Wall<?> wall) {
        return sameHeight(wall.toZWall());
    }

    @Override
    public ZWall shiftedNegative(int i) {
        return shiftedWest(i);
    }

    @Override
    public ZWall shiftedPositive(int i) {
        return shiftedEast(i);
    }

    @Override
    public ZWall extendNegative(int i) {
        return extendNorthEnd(i);
    }

    @Override
    public ZWall extendPositive(int i) {
        return extendSouthEnd(i);
    }

    @Override
    public XWall toXWall() {
        throw new IllegalStateException("Cannot convert ZWall to XWall");
    }

    @Override
    public ZWall toZWall() {
        return this;
    }

    @Override
    public boolean isLargerOnAxis(Wall<?> wall) {
        return isLargerThan(wall.toZWall());
    }

    @Override
    public boolean isSameContentOnAxis(
            Wall<?> w,
            WallDetector wd
    ) {
        return isSameContentAs(w.toZWall(), wd);
    }

    @Override
    public Wall<?> shifted(
            Direction s,
            int i
    ) {
        return switch (s) {
            case NORTH, SOUTH -> throw new IllegalStateException("ZWalls cannot be shifted north or south");
            case WEST -> shiftedWest(i);
            case EAST -> shiftedEast(i);
        };
    }

    @Override
    public Position negativeCorner() {
        return northCorner;
    }

    @Override
    public Position positiveCorner() {
        return southCorner;
    }

    @Override
    public ZWall shortenNegative(int i) {
        return shortenNorthEnd(i);
    }

    @Override
    public ZWall shortenPositive(int i) {
        return shortenSouthEnd(i);
    }

    @Override
    public int getLengthOnAxis() {
        return getLength();
    }

    @Override
    public String toShortString() {
        return String.format("%s->%s", northCorner.getUIString(), southCorner.getUIString());
    }
}
