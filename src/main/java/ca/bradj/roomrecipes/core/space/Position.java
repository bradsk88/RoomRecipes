package ca.bradj.roomrecipes.core.space;

import java.util.Objects;

public class Position {
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
}
