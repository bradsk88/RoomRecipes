package ca.bradj.roomrecipes.core.space;

public class ThreePosition extends Position {
    private final int y;

    public ThreePosition(
            int x,
            int y,
            int z
    ) {
        super(x, z);
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
}
