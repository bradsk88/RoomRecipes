package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class XWallsTest {

    @Test
    public void Test_DetectOpening() {
        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "_", "_", "W"},
        };

        Optional<XWall> opening = XWalls.findOpening(
                new XWall(new Position(0, 0), new Position(4, 0)),
                (Position dp) -> {
                    if (dp.x < 0 || dp.z < 0) {
                        return false;
                    }
                    if (dp.x >= map[0].length || dp.z >= map.length) {
                        return false;
                    }
                    return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
                }
        );
        assertTrue(opening.isPresent());
        assertEquals(new Position(1, 0), opening.get().westCorner);
        assertEquals(new Position(4, 0), opening.get().eastCorner);
    }

    @Test
    public void Test_DetectGarbage() {
        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "W", "_", "_", "W"},
        };

        Optional<ZWall> opening = ZWalls.findOpening(
                new ZWall(new Position(0, 0), new Position(4, 0)),
                (Position dp) -> {
                    if (dp.x < 0 || dp.z < 0) {
                        return false;
                    }
                    if (dp.x >= map[0].length || dp.z >= map.length) {
                        return false;
                    }
                    return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
                }
        );
        assertFalse(opening.isPresent());
    }
    @Test
    public void Test_DetectGarbage_2() {
        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "W"},
        };

        Optional<ZWall> opening = ZWalls.findOpening(
                new ZWall(new Position(0, 0), new Position(2, 0)),
                (Position dp) -> {
                    if (dp.x < 0 || dp.z < 0) {
                        return false;
                    }
                    if (dp.x >= map[0].length || dp.z >= map.length) {
                        return false;
                    }
                    return "W".equals(map[dp.z][dp.x]) || "D".equals(map[dp.z][dp.x]);
                }
        );
        assertFalse(opening.isPresent());
    }
}