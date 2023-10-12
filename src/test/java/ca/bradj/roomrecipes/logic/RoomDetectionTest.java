package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class RoomDetectionTest {

    @Test
    public void Test_DetectSimpleRoom_N() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "D", "W", "A"},
                {"W", "A", "W", "A"},
                {"W", "W", "W", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoom_E() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "A"},
                {"W", "A", "D", "A"},
                {"W", "W", "W", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoom_S() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "A"},
                {"W", "A", "W", "A"},
                {"W", "D", "W", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoom_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "A"},
                {"D", "A", "W", "A"},
                {"W", "W", "W", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomDestroyed() {
        // A = air
        // W = wall
        // D = door
        String[][] map1 = {
                {"W", "D", "W", "A"},
                {"W", "A", "W", "A"},
                {"W", "W", "W", "A"}
        };
        String[][] map2 = {
                {"W", "D", "W", "A"},
                {"W", "A", "A", "A"},
                {"W", "W", "W", "A"}
        };

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
                new Position(1, 0),
                4,
                0,
                position -> wdFn.apply(map1).test(position)
        );
        assertTrue(room.isPresent());

        room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, position -> wdFn.apply(map2).test(position));
        assertFalse(room.isPresent());

    }

    @Test
    public void Test_DetectSimpleRoomWithAirAround() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"A", "A", "A", "A", "A"},
                {"A", "W", "D", "W", "A"},
                {"A", "W", "A", "W", "A"},
                {"A", "W", "W", "W", "A"},
                {"A", "A", "A", "A", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(2, 1), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(1, 1), new Position(3, 3));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomWithAirAndWallAround() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"A", "A", "A", "A", "A", "A"},
                {"W", "A", "W", "D", "W", "A"},
                {"A", "A", "W", "A", "W", "A"},
                {"A", "A", "W", "W", "W", "A"},
                {"A", "A", "A", "A", "A", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(3, 1), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(2, 1), new Position(4, 3));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectOpenSouthWall() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"A", "A", "A", "A", "A", "A"},
                {"W", "A", "W", "D", "W", "A"},
                {"A", "A", "W", "A", "W", "A"},
                {"A", "A", "W", "A", "W", "A"},
                {"A", "A", "A", "A", "A", "A"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(3, 1), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertFalse(room.isPresent());
    }

    @Test
    public void Test_DetectShapeLikeLetterA() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "D", "W"},
                {"W", "A", "W"},
                {"W", "W", "W"},
                {"W", "A", "W"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 5, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());
    }

    @Test
    public void Test_DetectShapeLikeLetterA_NoCorners() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"A", "D", "A"},
                {"W", "A", "W"},
                {"W", "W", "W"},
                {"W", "A", "W"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 5, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());
    }

    @Test
    public void Test_DetectShapeLikeLetterA_90() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W"},
                {"D", "A", "W", "A"},
                {"W", "W", "W", "W"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(0, 1), 5, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());
    }

    @Test
    public void Test_DetectShapeLikeLetterA_180() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "A", "W"},
                {"W", "W", "W"},
                {"W", "A", "W"},
                {"W", "D", "W"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 3), 5, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 1), new Position(2, 3));
        assertEquals(expectedCorners, room.get().getSpace());
    }

    @Test
    public void Test_DetectShapeLikeLetterA_270() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W"},
                {"A", "W", "A", "D"},
                {"W", "W", "W", "W"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(3, 1), 5, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(1, 0), new Position(3, 2));
        assertEquals(expectedCorners, room.get().getSpace());
    }

    @Test
    public void Test_DetectSimpleRoomWithSeparateWestWall_EquidistantToEast() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "_", "_", "_", "_", "_"},
                {"W", "_", "W", "W", "W", "W", "W"},
                {"W", "_", "W", "_", "_", "_", "W"},
                {"W", "_", "W", "D", "W", "W", "W"},
                {"_", "_", "_", "_", "_", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(3, 3), 10, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(2, 1), new Position(6, 3));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomWithSeparateWestWall_EquidistantToEast_NoCorners() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "_", "_", "_", "_", "_"},
                {"W", "_", "_", "W", "W", "W", "_"},
                {"W", "_", "W", "_", "_", "_", "W"},
                {"W", "_", "_", "D", "W", "W", "_"},
                {"_", "_", "_", "_", "_", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(3, 3), 10, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(2, 1), new Position(6, 3));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomWithSeparateEastWall_EquidistantToWest() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "_", "_", "_", "_", "_"},
                {"W", "W", "W", "W", "W", "_", "W"},
                {"W", "_", "_", "_", "W", "_", "W"},
                {"W", "W", "W", "D", "W", "_", "W"},
                {"_", "_", "_", "_", "_", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(3, 3), 10, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 1), new Position(4, 3));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomWithSeparateNorthWall_EquidistantToSouth() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "D", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(4, 3), 10, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(1, 2), new Position(4, 6));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomWithSeparateSouthWall_EquidistantToNorth() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 3), 10, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(1, 0), new Position(4, 4));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoomWithSeparateSouthWall_EquidistantToNorth_NoCorners() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "W", "W", "_", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "W", "_", "_", "W", "_", "_"},
                {"_", "D", "_", "_", "W", "_", "_"},
                {"_", "_", "W", "W", "_", "_", "_"},
                {"_", "_", "_", "_", "_", "_", "_"},
                {"_", "W", "W", "W", "W", "_", "_"},
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 3), 10, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(1, 0), new Position(4, 4));
        assertEquals(expectedCorners, room.get().getSpace());

    }


    @Test
    public void Test_DetectSimpleRoom_WithNoCorners_N() {
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "D", "_", "_"},
                {"W", "_", "W", "_"},
                {"_", "W", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());
    }

    @Test
    public void Test_DetectSimpleRoom_WithNoCorners_E() {
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "_", "_"},
                {"W", "_", "D", "_"},
                {"_", "W", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoom_WithNoCorners_S() {
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "_", "_"},
                {"W", "_", "W", "_"},
                {"_", "D", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

    @Test
    public void Test_DetectSimpleRoom_WithNoCorners_W() {
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "_", "_"},
                {"D", "_", "W", "_"},
                {"_", "W", "_", "_"}
        };

        Optional<Room> room = RoomDetection.findRoomForDoor(new Position(1, 0), 4, 0, (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        });
        assertTrue(room.isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get().getSpace());

    }

}