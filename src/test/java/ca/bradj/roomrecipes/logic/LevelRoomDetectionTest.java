package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LevelRoomDetectionTest {

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
                {"W", "W", "W", "W", "W"}
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
    public void Test_DetectConjoinedRooms_N_Offset() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "D", "W", "W", "D", "W"},
                {"W", "A", "W", "A", "A", "W"},
                {"W", "W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 0),
                new Position(4, 0)
        ), 5, WD(map));
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(4, 0)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(4, 0)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(5, 2));
        assertEquals(expectedCorners, room.get(new Position(4, 0)).get().getSpace());

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
    public void Test_DetectConjoinedRooms_E_Offset() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"},
                {"W", "A", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(2, 4)
        ), 5, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(2, 4)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(2, 4)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 5));
        assertEquals(expectedCorners, room.get(new Position(2, 4)).get().getSpace());

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

    @Test
    public void Test_DetectConjoinedRooms_N_Alt() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "D", "W", "W", "W"},
                {"W", "A", "W", "A", "W"},
                {"W", "W", "W", "D", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 0),
                new Position(3, 2)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(3, 2)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(3, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(3, 2)).get().getSpace());

    }

    @Test
    public void Test_DetectConjoinedRooms_E_Alt() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"},
                {"D", "A", "W"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(0, 3)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(0, 3)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(0, 3)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(0, 3)).get().getSpace());

    }

    @Test
    public void Test_DetectConjoinedRooms_S_Alt() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "D", "W"},
                {"W", "A", "W", "A", "W"},
                {"W", "D", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 2),
                new Position(3, 0)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(3, 0)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(3, 0)).get().getSpace());

    }

    @Test
    public void Test_DetectConjoinedRooms_W_Alt() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"D", "A", "W"},
                {"W", "W", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1),
                new Position(2, 3)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(2, 3)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(2, 3)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(2, 3)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_E() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "A", "D", "A", "D"},
                {"W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(4, 1)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(4, 1)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(4, 1)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(4, 1)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_N() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "D", "W"},
                {"W", "A", "W"},
                {"W", "D", "W"},
                {"W", "A", "W"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 0),
                new Position(1, 2)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"D", "A", "D", "A", "W"},
                {"W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1),
                new Position(2, 1)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(2, 1)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_S() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "A", "W"},
                {"W", "D", "W"},
                {"W", "A", "W"},
                {"W", "D", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 2),
                new Position(1, 4)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(1, 4)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(1, 4)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(1, 4)).get().getSpace());
    }

@Test
    public void Test_DetectEndToEndRooms_E_RotateOne() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "A", "D", "A", "W"},
                {"W", "D", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(1, 2)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_N_RotateOne() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "A", "W"},
                {"W", "D", "W"},
                {"W", "A", "D"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 3),
                new Position(1, 2)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(2, 3)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(2, 3)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(2, 3)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_W_RotateOne() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "D", "W"},
                {"W", "A", "D", "A", "W"},
                {"W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(3, 0),
                new Position(2, 1)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(3, 0)));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(3, 0)).isPresent());
        assertTrue(room.get(new Position(2, 1)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(3, 0)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_S_RotateOne() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"D", "A", "W"},
                {"W", "D", "W"},
                {"W", "A", "W"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1),
                new Position(1, 2)
        ), 4, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());
    }

    @Test
    public void Test_DetectLShape_N() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_", "_"},
                {"D", "_", "W", "_", "_"},
                {"W", "W", "W", "W", "W"},
                {"W", "_", "W", "_", "W"},
                {"W", "D", "W", "D", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1),
                new Position(1, 4),
                new Position(3, 4)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(1, 4)));
        assertTrue(room.containsKey(new Position(3, 4)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(1, 4)).isPresent());
        assertTrue(room.get(new Position(3, 4)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(1, 4)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 2), new Position(4, 4));
        assertEquals(expectedCorners, room.get(new Position(3, 4)).get().getSpace());
    }

    @Test
    public void Test_DetectLShape_N2() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_", "_", "_"},
                {"D", "_", "W", "_", "_", "_"},
                {"W", "W", "W", "W", "W", "W"},
                {"W", "_", "W", "_", "_", "W"},
                {"W", "D", "W", "D", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1),
                new Position(1, 4),
                new Position(3, 4)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(1, 4)));
        assertTrue(room.containsKey(new Position(3, 4)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(1, 4)).isPresent());
        assertTrue(room.get(new Position(3, 4)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(1, 4)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 2), new Position(5, 4));
        assertEquals(expectedCorners, room.get(new Position(3, 4)).get().getSpace());
    }

    @Test
    public void Test_Detect_OpenLShape_N() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_", "_", "_"},
                {"W", "_", "W", "_", "_", "_"},
                {"W", "_", "W", "W", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "D", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 4)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(1, 4)));

        assertTrue(room.get(new Position(1, 4)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(1, 4)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 0), new Position(2, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(2, 2), new Position(4, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_N2() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_", "_", "_"},
                {"W", "_", "D", "_", "_", "_"},
                {"W", "_", "W", "W", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "W", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(2, 1)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 1)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 2), new Position(4, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_N_Broken() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_", "_", "_"},
                {"W", "_", "W", "_", "_", "_"},
                {"W", "_", "_", "W", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "D", "W", "W", "W", "_"}
        }; // Missing inside corner

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 4)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(1, 4)));

        assertFalse(room.get(new Position(1, 4)).isPresent());
    }

    @Test
    public void Test_Detect_OpenLShape_E() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W", "_"},
                {"D", "_", "_", "_", "W", "_"},
                {"W", "_", "W", "W", "W", "_"},
                {"W", "_", "W", "_", "_", "_"},
                {"W", "W", "W", "_", "_", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));

        assertTrue(room.get(new Position(0, 1)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(0, 1)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 0), new Position(4, 2));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 2), new Position(2, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }
    @Test
    public void Test_Detect_OpenLShape_E2() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "_", "W", "D", "W", "_"},
                {"W", "_", "W", "_", "_", "_"},
                {"W", "W", "W", "_", "_", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(3, 2)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(3, 2)));

        assertTrue(room.get(new Position(3, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 2)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 0), new Position(2, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_S() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "D", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "W", "W", "_", "W", "_"},
                {"_", "_", "W", "_", "W", "_"},
                {"_", "_", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(3, 0)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(3, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 0)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(2, 0), new Position(4, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_S2() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "W", "W", "_", "W", "_"},
                {"_", "_", "D", "_", "W", "_"},
                {"_", "_", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 3)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(2, 3)));

        assertTrue(room.get(new Position(2, 3)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 3)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(2, 2), new Position(4, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 0), new Position(4, 2));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_W() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "W", "W", "W", "_"},
                {"_", "_", "W", "_", "W", "_"},
                {"W", "W", "W", "_", "W", "_"},
                {"W", "_", "_", "_", "D", "_"},
                {"W", "W", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(4, 3)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(4, 3)));

        assertTrue(room.get(new Position(4, 3)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(4, 3)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 2), new Position(4, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(2, 0), new Position(4, 2));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_W2() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "W", "W", "W", "_"},
                {"_", "_", "W", "_", "W", "_"},
                {"W", "D", "W", "_", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "W", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 2)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(1, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(1, 2)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(2, 0), new Position(4, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Disabled("Low priority")
    @Test
    public void Test_Detect_OpenLShape_Pinched_N() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_", "_", "_"},
                {"W", "_", "W", "_", "_", "_"},
                {"W", "_", "W", "W", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "_", "W", "_", "W", "_"},
                {"W", "D", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 5)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(1, 5)));

        assertTrue(room.get(new Position(1, 5)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(1, 5)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 0), new Position(2, 5));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(2, 2), new Position(4, 5));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Disabled("Low priority")
    @Test
    public void Test_Detect_OpenLShape_Pinched_E() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W", "W"},
                {"D", "_", "_", "_", "_", "W"},
                {"W", "W", "_", "W", "W", "W"},
                {"W", "_", "_", "W", "_", "_"},
                {"W", "W", "W", "W", "_", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 1)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(0, 1)));

        assertTrue(room.get(new Position(0, 1)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(0, 1)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 0), new Position(5, 2));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 2), new Position(3, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Disabled("Low priority")
    @Test
    public void Test_Detect_OpenLShape_Pinched_S() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "D", "W", "_"},
                {"W", "_", "W", "_", "W", "_"},
                {"W", "_", "_", "_", "W", "_"},
                {"W", "W", "W", "_", "W", "_"},
                {"_", "_", "W", "_", "W", "_"},
                {"_", "_", "W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(3, 0)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(3, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 0)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(2, 0), new Position(4, 5));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 0), new Position(2, 3));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Disabled("Low priority")
    @Test
    public void Test_Detect_OpenLShape_Pinched_W() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "W", "W", "W", "W"},
                {"_", "_", "W", "_", "_", "W"},
                {"W", "W", "W", "_", "W", "W"},
                {"W", "_", "_", "_", "_", "D"},
                {"W", "W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(5, 3)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(5, 3)));

        assertTrue(room.get(new Position(5, 3)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(5, 3)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 2), new Position(5, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(2, 0), new Position(5, 2));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_DetectJoiningDoor() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "_", "D", "_", "W"},
                {"W", "D", "W", "D", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 2),
                new Position(3, 2),
                new Position(2, 1)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(3, 2)));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(3, 2)).isPresent());
        assertFalse(room.get(new Position(2, 1)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(new Position(3, 2)).get().getSpace());
    }
    @Test
    public void Test_DetectJoiningDoor_E() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "_", "D"},
                {"W", "D", "W"},
                {"W", "_", "D"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(1, 2),
                new Position(2, 3)
        ), 10, WD(map));
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(2, 3)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(2, 3)).isPresent());
        assertFalse(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = new InclusiveSpace(new Position(0, 2), new Position(2, 4));
        assertEquals(expectedCorners, room.get(new Position(2, 3)).get().getSpace());
    }

    @Test
    public void Test_DetectDoubleEntrance_N() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "D", "W", "D", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(1, 2),
                new Position(3, 2)
        ), 10, WD(map));
        assertEquals(2, room.size());

        // Hard to know which door it will choose
        List<Map.Entry<Position, Optional<Room>>> present = room.entrySet()
                .stream()
                .filter(v -> v.getValue().isPresent()).toList();
        assertEquals(1, present.size());

        Object onlyKey = present.get(0).getKey();

        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(4, 2));
        assertEquals(expectedCorners, room.get(onlyKey).get().getSpace());
    }
    @Test
    public void Test_DetectDoubleEntrance_E() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W"},
                {"W", "_", "D"},
                {"W", "_", "W"},
                {"W", "_", "D"},
                {"W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 1),
                new Position(2, 3)
        ), 10, WD(map));
        assertEquals(2, room.size());

        // Hard to know which door it will choose
        List<Map.Entry<Position, Optional<Room>>> present = room.entrySet()
                .stream()
                .filter(v -> v.getValue().isPresent()).toList();
        assertEquals(1, present.size());

        Object onlyKey = present.get(0).getKey();
        InclusiveSpace expectedCorners = new InclusiveSpace(new Position(0, 0), new Position(2, 4));
        assertEquals(expectedCorners, room.get(onlyKey).get().getSpace());
    }

    @Test
    public void Test_DetectNarrowEntrance_E() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "_"},
                {"W", "_", "W", "W"},
                {"W", "_", "_", "D"},
                {"W", "_", "W", "W"},
                {"W", "W", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(3, 2)
        ), 10, WD(map));
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(3, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 2)).get().getSpaces());
        assertEquals(3, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 1), new Position(3, 3));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 0), new Position(2, 1));
        InclusiveSpace expectedCorners3 = new InclusiveSpace(new Position(0, 3), new Position(2, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
        assertEquals(expectedCorners3, spaces.get(2));
    }
    @Test
    public void Test_DetectNarrowEntrance_W() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "W", "W"},
                {"W", "W", "_", "W"},
                {"D", "_", "_", "W"},
                {"W", "W", "_", "W"},
                {"_", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(0, 2)
        ), 10, WD(map));
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(0, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(0, 2)).get().getSpaces());
        assertEquals(3, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(0, 1), new Position(3, 3));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(1, 0), new Position(3, 1));
        InclusiveSpace expectedCorners3 = new InclusiveSpace(new Position(1, 3), new Position(3, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
        assertEquals(expectedCorners3, spaces.get(2));
    }
    @Test
    public void Test_DetectNarrowEntrance_S() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "W", "_", "W", "W"},
                {"_", "W", "D", "W", "_"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 4)
        ), 10, WD(map));
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(2, 4)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 4)).get().getSpaces());
        assertEquals(3, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(1, 0), new Position(3, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 0), new Position(1, 3));
        InclusiveSpace expectedCorners3 = new InclusiveSpace(new Position(3, 0), new Position(4, 3));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
        assertEquals(expectedCorners3, spaces.get(2));
    }
    @Test
    public void Test_DetectNarrowEntrance_N() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "D", "W", "_"},
                {"W", "W", "_", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 0)
        ), 10, WD(map));
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(2, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 0)).get().getSpaces());
        assertEquals(3, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(1, 0), new Position(3, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 1), new Position(1, 4));
        InclusiveSpace expectedCorners3 = new InclusiveSpace(new Position(3, 1), new Position(4, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
        assertEquals(expectedCorners3, spaces.get(2));
    }
    @Test
    public void Test_DetectInsetCorners_N() { // TODO: East,South,West
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "D", "W", "W"},
                {"W", "W", "_", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "W", "_", "W", "W"},
                {"W", "W", "W", "W", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 0)
        ), 10, WD(map));
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(2, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 0)).get().getSpaces());
        assertEquals(3, spaces.size());

        InclusiveSpace expectedCorners1 = new InclusiveSpace(new Position(1, 0), new Position(3, 4));
        InclusiveSpace expectedCorners2 = new InclusiveSpace(new Position(0, 1), new Position(1, 4));
        InclusiveSpace expectedCorners3 = new InclusiveSpace(new Position(3, 1), new Position(4, 4));

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
        assertEquals(expectedCorners3, spaces.get(2));
    }
    @Test
    public void Test_DetectNoDoor_N() { // TODO: East,South,West
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "_", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "W", "_", "W", "W"},
                {"W", "_", "W", "_", "W"}
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                new Position(2, 0)
        ), 10, WD(map));
        assertEquals(1, room.size());

        assertFalse(room.get(new Position(2, 0)).isPresent());
    }

    @Test
    public void Test_Regression_SkinnyRoomInsideOtherRoom() {
        String[][] map = {
                {"?", "?", "?", "?", "?", "_", "_", "_", " ", "_", "_", "_", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "w", "W", "W", "W", "W", "W", "w", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "w", "W", " ", " ", " ", "_", "w", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "_", " ", "_", " ", "_", "_", "_", "w", "w", "?"},
                {"_", "w", "?", "?", "w", "w", "W", " ", " ", " ", "W", "w", "W", "w", "?"},
                {" ", "w", "_", "_", "_", " ", "D", " ", "_", "w", "w", "w", "W", "w", "?"},
                {"_", "w", "W", "W", "W", "W", "W", " ", " ", "W", "_", "_", "W", "w", "?"},
                {"?", "_", "W", " ", " ", " ", " ", " ", " ", "W", "_", "_", "w", "w", "?"},
                {"?", "_", "W", "W", "W", "W", "W", "W", "W", "W", "w", "?", "?", "?", "?"},
                {"?", "_", "w", "_", "_", "_", "_", "w", "w", "w", "w", "?", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "_", "_", "w", "?", "?", "?", "?", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "_", " ", "w", "?", "?", "?", "?", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "w", "w", "w", "?", "?", "?", "?", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?"},
                {"?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?"},
        };
        Position doorPos = new Position(6, 5);

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(ImmutableList.of(
                doorPos
        ), 20, WD(map));
        assertEquals(1, room.size());
        assertTrue(room.get(doorPos).isPresent());
        assertTrue(room.get(doorPos).get().getBackZWall().isPresent());
    }
}