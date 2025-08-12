package ca.bradj.roomrecipes.core.space;

import ca.bradj.roomrecipes.logic.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Position implements Comparable<Position>{
    public Position(
            int x,
            int z
    ) {
        this.x = x;
        this.z = z;
    }

    public final int x;
    public final int z;

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }

    public Position offset(
            int x,
            int z
    ) {
        return new Position(this.x + x, this.z + z);
    }

    public Position WithX(int x) {
        return new Position(x, this.z);
    }

    public Position WithZ(int z) {
        return new Position(this.x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position doorPos = (Position) o;
        return x == doorPos.x && z == doorPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    public String getUIString() {
        return String.format("[%d, %d]", x, z);
    }

    public Position relative(Direction initDirection) {
        switch (initDirection) {
            case NORTH:
                return offset(0, -1);
            case EAST:
                return offset(1, 0);
            case SOUTH:
                return offset(0, 1);
            case WEST:
                return offset(-1, 0);
            default:
                throw new IllegalArgumentException("Invalid direction: " + initDirection);
        }
    }

    @Override
    public int compareTo(@NotNull Position o) {
        if (this.x != o.x) {
            return Integer.compare(this.x, o.x);
        } else {
            return Integer.compare(this.z, o.z);
        }
    }
}
