package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DebugArtTest {

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

        Position doorPos1 = new Position(1, 0);
        Position doorPos2 = new Position(3, 0);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1, doorPos2),
                20,
                1000,
                WD(map),
                true
        );
        for (int i = 0; i < 1000; i++) {
            d.proceed();
        }

        ImmutableMap<Position, String> art = d.getDebugArt(true);

        Assertions.assertEquals(
                """
                        {
                            {"_", "_", "_", "_", "_", "_", "_"},
                            {"_", " ", "w", "D", "W", "W", "w"},
                            {"_", "_", "W", " ", "W", "_", "w"},
                            {"?", "_", "w", "W", "w", "w", "?"},
                            {"?", "?", "_", "_", "_", "?", "?"}
                        }""",
                art.get(doorPos1)
        );

    }
    @Test
    public void Test_Room_N() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {"_", "_", "_", "_", "_"},
                {"_", "W", "D", "W", "_"},
                {"_", "W", "A", "W", "_"},
                {"_", "W", "W", "W", "_"},
                {"_", "_", "_", "_", "_"},
        };

        Position doorPos1 = new Position(2, 1);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                true
        );
        for (int i = 0; i < 1000; i++) {
            d.proceed();
        }

        ImmutableMap<Position, String> art = d.getDebugArt(true);

        Assertions.assertEquals(
                """
                        {
                            {"_", "_", "_", "_", "_", "_", "_"},
                            {"_", " ", "w", "D", "w", " ", "_"},
                            {"_", "_", "W", " ", "W", "_", "_"},
                            {"?", "_", "w", "W", "w", "_", "?"},
                            {"?", "?", "_", "_", "_", "?", "?"}
                        }""",
                art.get(doorPos1)
        );

    }
}