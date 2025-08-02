package ca.bradj.roomrecipes.core.space;

import java.util.Objects;

public class ThreePosition {
    private final int y;
    private final Position delegate;

    public ThreePosition(
            int x,
            int y,
            int z
    ) {
        this.delegate = new Position(x, z);
        this.y = y;
    }

    public static ThreePosition of(
            Position cornerA,
            int yCoord
    ) {
        return new ThreePosition(cornerA.x, yCoord, cornerA.z);
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "ThreePosition{" +
                "x=" + delegate.x +
                ", y=" + y +
                ", z=" + delegate.z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThreePosition that = (ThreePosition) o;
        return y == that.y && Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, delegate);
    }

    public int getX() {
        return delegate.x;
    }

    public int getZ() {
        return delegate.z;
    }

    public Position dropY() {
        return new Position(delegate.x, delegate.z);
    }
}
