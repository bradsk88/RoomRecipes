package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

class RoomComparisonTest {

    private static Room fromMap(
            Position doorPos, String[][] roomMap
    ) {
        Function<String[][], Predicate<Position>> wdFn = (String[][] map) -> (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(
                doorPos, 10, 0,
                position -> wdFn.apply(roomMap).test(position)
        );
        Assertions.assertTrue(room.isPresent());
        return room.get();
    }

    @Test
    void isLikelySame_DoorOnWest_RoomWidened() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "W", "_"},
                {"_", "W", "_", "_", "_", "W", "_"},
                {"_", "W", "_", "_", "_", "W", "_"},
                {"_", "D", "_", "_", "_", "W", "_"},
                {"_", "W", "W", "W", "W", "W", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnWest_RoomWidenedAndDoorMoved() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(0, 3), new String[][]{
                {"W", "W", "W", "W", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"D", "_", "_", "_", "W", "_", "_"},
                {"W", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnWest_RoomMadeTaller() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnWest_RoomMadeTaller_ComplexShape() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(1, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "W", "W", "_", "_"},
                {"_", "W", "W", "W", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnEast_RoomWidened() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(4, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(4, 3), new String[][]{
                {"W", "W", "W", "W", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"W", "_", "_", "_", "D", "_", "_"},
                {"W", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnEast_RoomWidenedAndDoorMoved() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(4, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(5, 3), new String[][]{
                {"_", "W", "W", "W", "W", "W", "_"},
                {"_", "W", "_", "_", "_", "W", "_"},
                {"_", "W", "_", "_", "_", "W", "_"},
                {"_", "W", "_", "_", "_", "D", "_"},
                {"_", "W", "W", "W", "W", "W", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnEast_RoomMadeTaller() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(4, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(4, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnEast_RoomMadeTaller_ComplexShape() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(4, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(4, 3), new String[][]{
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "W", "_", "W", "_", "_"},
                {"_", "_", "W", "W", "W", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnNorth_RoomWidened() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(2, 0), new String[][]{
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(2, 0), new String[][]{
                {"W", "W", "D", "W", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"W", "_", "_", "_", "W", "_", "_"},
                {"W", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnNorth_RoomWidened_ComplexShape() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(2, 0), new String[][]{
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(2, 0), new String[][]{
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "W", "_"},
                {"_", "W", "_", "_", "_", "W", "_"},
                {"_", "W", "W", "W", "W", "W", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnNorth_RoomMadeTaller() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(2, 1), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(2, 1), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_DoorOnNorth_RoomMadeTaller_AndDoorMoved() {
        // A = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(2, 1), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(2, 0), new String[][]{
                {"_", "W", "D", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertTrue(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_ShouldReturnFalse_SharedWallWithDoor() {
        // _ = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(3, 2), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "_", "_", "W", "W", "W", "_"},
                {"_", "_", "_", "D", "_", "W", "_"},
                {"_", "_", "_", "W", "W", "W", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(3, 2), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "W", "W", "_", "_", "_"},
                {"_", "W", "_", "D", "_", "_", "_"},
                {"_", "W", "W", "W", "_", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertFalse(RoomComparison.isLikelySame(room1, room2));
    }

    @Test
    void isLikelySame_ShouldReturnFalse_SharedWallNoDoor() {
        // _ = air
        // W = wall
        // D = door
        Room room1 = fromMap(new Position(3, 2), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "_", "_", "W", "W", "W", "_"},
                {"_", "_", "_", "W", "_", "W", "_"},
                {"_", "_", "_", "W", "D", "W", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });
        Room room2 = fromMap(new Position(3, 2), new String[][]{
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "W", "W", "_", "_", "_"},
                {"_", "W", "_", "W", "_", "_", "_"},
                {"_", "W", "D", "W", "_", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
        });

        Assertions.assertFalse(RoomComparison.isLikelySame(room1, room2));
    }
}