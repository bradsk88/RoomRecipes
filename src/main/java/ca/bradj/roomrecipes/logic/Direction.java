package ca.bradj.roomrecipes.logic;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static Direction fromNorthQuarter(int iteration) {
        return switch (iteration % 4) {
            case 0 -> NORTH;
            case 1 -> EAST;
            case 2 -> SOUTH;
            case 3 -> WEST;
            default -> throw new IllegalArgumentException("Invalid iteration: " + iteration);
        };
    }

    public Direction ccw() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
        };
    }

    public Direction cw() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
        };
    }

    public Direction opp() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
        };
    }
}
