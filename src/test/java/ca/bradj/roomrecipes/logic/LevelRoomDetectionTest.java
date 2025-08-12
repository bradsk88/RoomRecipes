package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import ca.bradj.roomrecipes.rooms.ZWall;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"DataFlowIssue", "OptionalGetWithoutIsPresent"})
class LevelRoomDetectionTest {

    private WallDetector WD(String[][] map) {

        return TestHelpers.WD(map);
    }
    private WallDetector WD2(String[] map) {

        return TestHelpers.WD2(map);
    }

    private static class TestRecorder extends LinkedBlockingQueue<String> {
        @Override
        public boolean add(String s) {
            System.out.println(s);
            return super.add(s);
        }
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

        TestRecorder recorder = new TestRecorder();
        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 0),
                        new Position(3, 0)
                ), 4, recorder::add, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(3, 0)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 0),
                        new Position(4, 0)
                ), 5, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(4, 0)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(4, 0)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(5, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(2, 3)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(2, 3)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(2, 3)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(2, 4)
                ), 5, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(2, 4)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(2, 4)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 5);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 2),
                        new Position(3, 2)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(3, 2)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(3, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1),
                        new Position(0, 3)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(0, 3)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(0, 3)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 0),
                        new Position(3, 2)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(3, 2)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(3, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(0, 3)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(0, 3)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(0, 3)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 2),
                        new Position(3, 0)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(3, 0)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1),
                        new Position(2, 3)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(2, 3)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(2, 3)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(4, 1)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(4, 1)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(4, 1)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 0),
                        new Position(1, 2)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 0)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(1, 0)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 0)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1),
                        new Position(2, 1)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(2, 1)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 2),
                        new Position(1, 4)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(1, 4)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(1, 4)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(1, 2)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 3),
                        new Position(1, 2)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 3)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(2, 3)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
        assertEquals(expectedCorners, room.get(new Position(2, 3)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

    }

    @Test
    public void Test_DetectEndToEndRooms_W_RotateOne() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4
                {"W", "W", "W", "D", "W"}, // 0
                {"W", "A", "D", "A", "W"}, // 1
                {"W", "W", "W", "W", "W"}  // 2
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(3, 0),
                        new Position(2, 1)
                ), 4,
                System.out::println,
                WD(map)
        );
        assertTrue(room.containsKey(new Position(3, 0)));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(3, 0)).isPresent());
        assertTrue(room.get(new Position(2, 1)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
        assertEquals(expectedCorners, room.get(new Position(3, 0)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1),
                        new Position(1, 2)
                ), 4, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());
    }

    @Test
    public void Test_DetectSegmentedLShape_N() {
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1),
                        new Position(1, 4),
                        new Position(3, 4)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(1, 4)));
        assertTrue(room.containsKey(new Position(3, 4)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(1, 4)).isPresent());
        assertTrue(room.get(new Position(3, 4)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
        assertEquals(expectedCorners, room.get(new Position(1, 4)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 2).to(4, 4);
        assertEquals(expectedCorners, room.get(new Position(3, 4)).get().getSpace());
    }

    @Test
    public void Test_DetectSegmentedLShape_N2() {
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1),
                        new Position(1, 4),
                        new Position(3, 4)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));
        assertTrue(room.containsKey(new Position(1, 4)));
        assertTrue(room.containsKey(new Position(3, 4)));

        assertTrue(room.get(new Position(0, 1)).isPresent());
        assertTrue(room.get(new Position(1, 4)).isPresent());
        assertTrue(room.get(new Position(3, 4)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(0, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
        assertEquals(expectedCorners, room.get(new Position(1, 4)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 2).to(5, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 4)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 4)));

        assertTrue(room.get(new Position(1, 4)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(1, 4)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = InclusiveSpace.from(0, 0).to(2, 4);
        InclusiveSpace expectedCorners2 = InclusiveSpace.from(2, 2).to(4, 4);

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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(2, 1)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 1)).get().getSpaces());


        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(2, 4),
                InclusiveSpace.from(2, 2).to(4, 4),
                spaces
        );
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 4)
                ), 10, WD(map)
        );
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


        TestRecorder flightRecorder = new TestRecorder();

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1)
                ), 20, flightRecorder::add, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));

        assertTrue(room.get(new Position(0, 1)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(0, 1)).get().getSpaces());

        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 2),
                InclusiveSpace.from(0, 2).to(2, 4),
                spaces
        );
    }

    @Test
    public void Test_Detect_OpenLShape_E2() {
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5
                {"W", "W", "W", "W", "W", "_"}, // 0
                {"W", "_", "_", "_", "W", "_"}, // 1
                {"W", "_", "W", "D", "W", "_"}, // 2
                {"W", "_", "W", "_", "_", "_"}, // 3
                {"W", "W", "W", "_", "_", "_"}  // 4
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(3, 2)
                ), 10, System.out::println, WD(map)
        );
        assertTrue(room.containsKey(new Position(3, 2)));

        assertTrue(room.get(new Position(3, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 2)).get().getSpaces());

        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 2),
                InclusiveSpace.from(0, 2).to(2, 4),
                spaces
        );
    }

    /// x x x
    /// x x x
    /// x x x

    private static void assertSpacesEqual(
            InclusiveSpace lPart1,
            InclusiveSpace lPart2,
            List<InclusiveSpace> spaces
    ) {
        ImmutableSet.Builder<Position> b = ImmutableSet.builder();
        b.addAll(InclusiveSpaces.getAllEnclosedPositions(lPart1));
        b.addAll(InclusiveSpaces.getAllEnclosedPositions(lPart2));
        Set<Position> expected = b.build().stream().sorted().collect(ImmutableSet.toImmutableSet());

        Set<Position> actual = spaces.stream()
                .flatMap(space -> InclusiveSpaces.getAllEnclosedPositions(space).stream())
                .sorted()
                .collect(ImmutableSet.toImmutableSet());

        assertEquals(expected, actual);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(3, 0)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(3, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 0)).get().getSpaces());

        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 2),
                InclusiveSpace.from(2, 2).to(4, 4),
                spaces
        );
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 3)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 3)));

        assertTrue(room.get(new Position(2, 3)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 3)).get().getSpaces());

        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 2),
                InclusiveSpace.from(2, 2).to(4, 4),
                spaces
        );
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(4, 3)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(4, 3)));

        assertTrue(room.get(new Position(4, 3)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(4, 3)).get().getSpaces());

        assertSpacesEqual(
                InclusiveSpace.from(2, 0).to(4, 2),
                InclusiveSpace.from(0, 2).to(4, 4),
                spaces
        );
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 2)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(1, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(1, 2)).get().getSpaces());
        assertEquals(2, spaces.size());

        InclusiveSpace expectedCorners1 = InclusiveSpace.from(0, 2).to(2, 4);
        InclusiveSpace expectedCorners2 = InclusiveSpace.from(2, 0).to(4, 4);

        assertEquals(expectedCorners1, spaces.get(0));
        assertEquals(expectedCorners2, spaces.get(1));
    }

    @Test
    public void Test_Detect_OpenLShape_Pinched_N() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5
                {"W", "W", "W", "_", "_", "_"}, // 0
                {"W", "_", "W", "_", "_", "_"}, // 1
                {"W", "_", "W", "W", "W", "_"}, // 2
                {"W", "_", "_", "_", "W", "_"}, // 3
                {"W", "_", "W", "_", "W", "_"}, // 4
                {"W", "D", "W", "W", "W", "_"} //  5
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 5)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 5)));

        assertTrue(room.get(new Position(1, 5)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(1, 5)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(2, 2),
                InclusiveSpace.from(0, 2).to(4, 5),
                spaces
        );
    }

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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 1)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(0, 1)));

        assertTrue(room.get(new Position(0, 1)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(0, 1)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(5, 2),
                InclusiveSpace.from(0, 2).to(3, 4),
                spaces
        );
    }

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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(3, 0)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(3, 0)));

        assertTrue(room.get(new Position(3, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 0)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 3),
                InclusiveSpace.from(2, 3).to(4, 5),
                spaces
        );
    }

    @Test
    public void Test_Detect_OpenLShape_Pinched_W() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5
                {"_", "_", "W", "W", "W", "W"}, // 0
                {"_", "_", "W", "_", "_", "W"}, // 1
                {"W", "W", "W", "_", "W", "W"}, // 2
                {"W", "_", "_", "_", "_", "D"}, // 3
                {"W", "W", "W", "W", "W", "W"} //  4
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(5, 3)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(5, 3)));

        assertTrue(room.get(new Position(5, 3)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(5, 3)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(2, 0).to(5, 2),
                InclusiveSpace.from(0, 2).to(5, 4),
                spaces
        );
    }

    @Test
    public void Test_DetectJoiningDoor() {
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4
                {"W", "W", "W", "W", "W"}, // 0
                {"W", "_", "D", "_", "W"}, // 1
                {"W", "D", "W", "D", "W"} //  2
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 2),
                        new Position(3, 2),
                        new Position(2, 1)
                ), 10, System.out::println, WD(map)
        );
        assertTrue(room.containsKey(new Position(1, 2)));
        assertTrue(room.containsKey(new Position(3, 2)));
        assertTrue(room.containsKey(new Position(2, 1)));

        assertTrue(room.get(new Position(1, 2)).isPresent());
        assertTrue(room.get(new Position(3, 2)).isPresent());
        assertFalse(room.get(new Position(2, 1)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(1, 2)).get().getSpace());

        expectedCorners = InclusiveSpace.from(2, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(1, 2),
                        new Position(2, 3)
                ), 10, WD(map)
        );
        assertTrue(room.containsKey(new Position(2, 1)));
        assertTrue(room.containsKey(new Position(2, 3)));
        assertTrue(room.containsKey(new Position(1, 2)));

        assertTrue(room.get(new Position(2, 1)).isPresent());
        assertTrue(room.get(new Position(2, 3)).isPresent());
        assertFalse(room.get(new Position(1, 2)).isPresent());

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 2);
        assertEquals(expectedCorners, room.get(new Position(2, 1)).get().getSpace());

        expectedCorners = InclusiveSpace.from(0, 2).to(2, 4);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(1, 2),
                        new Position(3, 2)
                ), 10, WD(map)
        );
        assertEquals(2, room.size());

        // Hard to know which door it will choose
        List<Map.Entry<Position, Optional<Room>>> present = room.entrySet()
                                                                .stream()
                                                                .filter(v -> v.getValue().isPresent()).toList();
        assertEquals(1, present.size());

        Position onlyKey = present.get(0).getKey();

        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(4, 2);
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 1),
                        new Position(2, 3)
                ), 10, WD(map)
        );
        assertEquals(2, room.size());

        // Hard to know which door it will choose
        List<Map.Entry<Position, Optional<Room>>> present = room.entrySet()
                                                                .stream()
                                                                .filter(v -> v.getValue().isPresent()).toList();
        assertEquals(1, present.size());

        Position onlyKey = present.get(0).getKey();
        InclusiveSpace expectedCorners = InclusiveSpace.from(0, 0).to(2, 4);
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
                //0    1    2    3    4    5
                {"W", "W", "W", "_"}, // 0
                {"W", "_", "W", "W"}, // 1
                {"W", "_", "_", "D"}, // 2
                {"W", "_", "W", "W"}, // 3
                {"W", "W", "W", "_"} //  4
        };

        ArrayList<String> recorder = new ArrayList<>();

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(3, 2)
                ), 10,
                System.out::println,
                WD(map)
        );
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(3, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(3, 2)).get().getSpaces());

        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(2, 4),
                InclusiveSpace.from(2, 1).to(3, 3),
                spaces
        );
    }

    @Test
    public void Test_DetectNarrowEntrance_W() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5
                {"_", "W", "W", "W"}, // 0
                {"W", "W", "_", "W"}, // 1
                {"D", "_", "_", "W"}, // 2
                {"W", "W", "_", "W"}, // 3
                {"_", "W", "W", "W"} //  4
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(0, 2)
                ), 10, WD(map)
        );
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(0, 2)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(0, 2)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(0, 1).to(1, 3),
                InclusiveSpace.from(1, 0).to(3, 4),
                spaces
        );
    }

    @Test
    public void Test_DetectNarrowEntrance_S() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5
                {"W", "W", "W", "W", "W"}, // 0
                {"W", "_", "_", "_", "W"}, // 1
                {"W", "_", "_", "_", "W"}, // 2
                {"W", "W", "_", "W", "W"}, // 3
                {"_", "W", "D", "W", "_"} //  4
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 4)
                ), 10, System.out::println, WD(map)
        );
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(2, 4)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 4)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 3),
                InclusiveSpace.from(1, 3).to(3, 4),
                spaces
        );
    }

    @Test
    public void Test_DetectNarrowEntrance_N() {
        // _ = air
        // W = wall
        // D = door
        String[] map = {
                "_WDW_", // 0
                "WW_WW", // 1
                "W___W", // 2
                "W___W", // 3
                "WWWWW" //  4
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 0)
                ), 10, System.out::println, WD2(map)
        );
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(2, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 0)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(1 ,0).to(3, 1),
                InclusiveSpace.from(0, 1).to(4, 4),
                spaces
        );
    }

    @Test
    public void Test_DetectInsetCorners_N() { // TODO: East,South,West
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5
                {"W", "W", "D", "W", "W"}, // 0
                {"W", "W", "_", "W", "W"}, // 1
                {"W", "_", "_", "_", "W"}, // 2
                {"W", "W", "_", "W", "W"}, // 3
                {"W", "W", "W", "W", "W"} //  4
        };

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 0)
                ), 10, WD(map)
        );
        assertEquals(1, room.size());

        assertTrue(room.get(new Position(2, 0)).isPresent());

        List<InclusiveSpace> spaces = ImmutableList.copyOf(room.get(new Position(2, 0)).get().getSpaces());
        assertSpacesEqual(
                InclusiveSpace.from(0, 0).to(4, 4),
                InclusiveSpace.from(0, 0).to(4, 4),
                spaces
        );
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

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        new Position(2, 0)
                ), 10, WD(map)
        );
        assertEquals(1, room.size());

        assertFalse(room.get(new Position(2, 0)).isPresent());
    }

    @Test
    public void Test_Regression_SkinnyRoomInsideOtherRoom() {
        String[][] map = {
                //0    1    2    3    4    5    6    7    8    9   10   11   12   13   14
                {"?", "?", "?", "?", "?", "_", "_", "_", " ", "_", "_", "_", "?", "?", "?"}, // 0
                {"?", "?", "?", "?", "?", "w", "W", "W", "W", "W", "W", "w", "?", "?", "?"}, // 1
                {"?", "?", "?", "?", "?", "w", "W", " ", " ", " ", "_", "w", "?", "?", "?"}, // 2
                {"?", "?", "?", "?", "?", "_", " ", "_", " ", "_", "_", "_", "w", "w", "?"}, // 3
                {"_", "w", "w", "w", "w", "w", "W", " ", " ", " ", "W", "w", "W", "w", "?"}, // 4
                {" ", "w", "_", "_", "_", " ", "D", " ", "_", "w", "w", "w", "W", "w", "?"}, // 5
                {"_", "w", "W", "W", "W", "W", "W", " ", " ", "W", "_", "_", "W", "w", "?"}, // 6
                {"?", "_", "W", " ", " ", " ", " ", " ", " ", "W", "_", "_", "w", "w", "?"}, // 7
                {"?", "_", "W", "W", "W", "W", "W", "W", "W", "W", "w", "?", "?", "?", "?"}, // 8
                {"?", "_", "w", "_", "_", "_", "_", "w", "w", "w", "w", "?", "?", "?", "?"}, // 9
                {"?", "?", "?", "?", "?", "_", "_", "w", "?", "?", "?", "?", "?", "?", "?"}, // 10
                {"?", "?", "?", "?", "?", "_", " ", "w", "?", "?", "?", "?", "?", "?", "?"}, // 11
                {"?", "?", "?", "?", "?", "w", "w", "w", "?", "?", "?", "?", "?", "?", "?"}, // 12
                {"?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?"}, // 13
                {"?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?"}, // 14
        };
        Position doorPos = new Position(6, 5);

        TestRecorder flightRecorder = new TestRecorder();

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        doorPos
                ), 20, flightRecorder::add, WD(map)
        );
        assertEquals(1, room.size());
        assertTrue(room.get(doorPos).isPresent(), () -> blackBox(flightRecorder));
        assertTrue(room.get(doorPos).get().getBackZWall().isPresent());
        assertEquals(
                new ZWall(new Position(1, 4), new Position(1, 6)),
                room.get(doorPos).get().getBackZWall().get()
        );
    }

    @Test
    public void Test_Regression_SkinnyRoom() {
        String[][] map = {
                //0    1    2    3    4    5    6
                {" ", " ", " ", " ", "_", " ", " "}, // 0
                {"W", "W", "W", "w", "w", "W", " "}, // 1
                {"W", " ", "_", "_", " ", "D", " "}, // 2
                {"W", "W", "W", "W", "W", "W", " "}, // 3
                {" ", " ", " ", " ", " ", " ", " "}, // 4
        };
        Position doorPos = new Position(5, 2);

        TestRecorder flightRecorder = new TestRecorder();

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        doorPos
                ), 20, flightRecorder::add, WD(map)
        );
        assertEquals(1, room.size());
        assertTrue(room.get(doorPos).isPresent(), () -> blackBox(flightRecorder));
        assertTrue(room.get(doorPos).get().getBackZWall().isPresent());
    }

    private String blackBox(TestRecorder flightRecorder) {
        StringBuilder b = new StringBuilder();
        flightRecorder.forEach(v -> {
            b.append(v);
            b.append("\n");
        });
        return b.toString();
    }


    @Test
    public void Test_Regression_InfiniteSpiral() {
        String[][] map = {
                //0    1    2    3    4    5    6
                {" ", " ", " ", " ", "_", " ", " "}, // 0
                {" ", "W", "W", "w", "w", "W", " "}, // 1
                {" ", "W", " ", " ", " ", "W", " "}, // 2
                {" ", "W", " ", "W", " ", "W", " "}, // 3
                {" ", "W", " ", " ", " ", "D", " "}, // 4
                {" ", "W", "W", "W", "W", "W", " "}, // 5
                {" ", " ", " ", " ", " ", " ", " "}, // 6
        };
        Position doorPos = new Position(5, 4);

        TestRecorder flightRecorder = new TestRecorder();

        ImmutableMap<Position, Optional<Room>> room = LevelRoomDetection.findRooms(
                ImmutableList.of(
                        doorPos
                ), 20, flightRecorder::add, WD(map)
        );
        assertEquals(1, room.size());
        assertTrue(room.get(doorPos).isPresent(), () -> blackBox(flightRecorder));
        assertTrue(room.get(doorPos).get().getBackZWall().isPresent());
    }
}