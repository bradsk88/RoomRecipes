package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelRoomDetection_KnobsTest {

    private WallDetector WD(String[][] map) {

        return (Position dp) -> {
            if (dp.x < 0 || dp.z < 0) {
                return false;
            }
            if (dp.x >= map[0].length || dp.z >= map.length) {
                return false;
            }
            return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
        };
    }

    @Test
    public void Test_DetectConjoinedRooms_N() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "D", "W", "D", "W"},
                {"W", "A", "W", "A", "W"},
                {"W", "W", "W", "W", "W"},
                {"W", "A", "A", "A", "A"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 0),
                new Position(3, 0)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(3, 0)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(3, 0)).get().getSpace());

    }

    @Test
    public void Test_DetectConjoinedRooms_E() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(2, 3)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(2, 3)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(2, 3)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(2, 3)).get().getSpace());

    }

    @Test
    public void Test_DetectConjoinedRooms_S() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "A", "W", "A", "W"},
                {"W", "D", "W", "D", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 2),
                new Position(3, 2)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(3, 2)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(3, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(3, 2)).get().getSpace());

    }

    @Test
    public void Test_DetectConjoinedRooms_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"D", "A", "W"},
                {"W", "W", "W"},
                {"D", "A", "W"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1),
                new Position(0, 3)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(0, 3)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(0, 3)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(0, 3)).get().getSpace());

    }
}