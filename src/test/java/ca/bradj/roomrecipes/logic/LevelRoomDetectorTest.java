package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

class LevelRoomDetectorTest {

    private WallDetector WD(String[][] map) {
        return TestHelpers.WD(map);
    }

    @Test
    public void Test_ShouldOnlyRequireOneIteration_ForKnownRoom_ThatIsUnchanged_LongRoom_E() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", "D", " ", " ", " ", " ", " ", " ", " ", "W", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
        };

        Position doorPos1 = new Position(1, 2);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                null
        );
        Room currentRoom = new Room(doorPos1, InclusiveSpaces.from(1, 1).to(10, 3));
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // The first iteration will return null - the detector only returns AFTER checking all doors
        Assertions.assertNull(res);
        res = d.proceed(currentState);

        // It should be found after one iteration
        Assertions.assertNotNull(res);
        Optional<Room> room = res.get(doorPos1);
        Assertions.assertNotNull(room);
        Assertions.assertTrue(room.isPresent());
    }
    @Test
    public void Test_ShouldOnlyRequireOneIteration_ForKnownRoom_ThatIsUnchanged_LongRoom_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", "W", " ", " ", " ", " ", " ", " ", " ", "D", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
        };

        Position doorPos1 = new Position(10, 2);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                null
        );
        Room currentRoom = new Room(doorPos1, InclusiveSpaces.from(1, 1).to(10, 3));
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // The first iteration will return null - the detector only returns AFTER checking all doors
        Assertions.assertNull(res);
        res = d.proceed(currentState);

        // It should be found after one iteration
        Assertions.assertNotNull(res);
        Optional<Room> room = res.get(doorPos1);
        Assertions.assertNotNull(room);
        Assertions.assertTrue(room.isPresent());
    }
    @Test
    public void Test_ShouldOnlyRequireOneIteration_ForKnownRoom_ThatIsUnchanged_LongRoom_N() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {" ", " ", " ", " ", " "},
                {" ", "W", "D", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", "W", "W", " "},
                {" ", " ", " ", " ", " "},
        };

        Position doorPos1 = new Position(1, 2);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                null
        );
        Room currentRoom = new Room(doorPos1, InclusiveSpaces.from(1, 1).to(3, 10));
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // The first iteration will return null - the detector only returns AFTER checking all doors
        Assertions.assertNull(res);
        res = d.proceed(currentState);

        // It should be found after one iteration
        Assertions.assertNotNull(res);
        Optional<Room> room = res.get(doorPos1);
        Assertions.assertNotNull(room);
        Assertions.assertTrue(room.isPresent());
    }
    @Test
    public void Test_ShouldOnlyRequireOneIteration_ForKnownRoom_ThatIsUnchanged_LongRoom_S() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {" ", " ", " ", " ", " "},
                {" ", "W", "D", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", " ", "W", " "},
                {" ", "W", "D", "W", " "},
                {" ", " ", " ", " ", " "},
        };

        Position doorPos1 = new Position(2, 10);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                null
        );
        Room currentRoom = new Room(doorPos1, InclusiveSpaces.from(1, 1).to(3, 10));
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // The first iteration will return null - the detector only returns AFTER checking all doors
        Assertions.assertNull(res);
        res = d.proceed(currentState);

        // It should be found after one iteration
        Assertions.assertNotNull(res);
        Optional<Room> room = res.get(doorPos1);
        Assertions.assertNotNull(room);
        Assertions.assertTrue(room.isPresent());
    }

    @Test
    public void Test_ShouldReScan_ForKnownRoom_ThatIsDifferent_LongRoom_E() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", "D", " ", " ", " ", " ", " ", " ", " ", "W", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
        };

        Position doorPos1 = new Position(1, 2);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                null
        );
        InclusiveSpace oldSpace = InclusiveSpaces.from(1, 1).to(5, 3); // Only half the width
        Room currentRoom = new Room(doorPos1, oldSpace);
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        for (int i = 0; i < 1000; i++) {
            @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed();
            if (res == null) {
                continue;
            }
            Optional<Room> r = res.get(doorPos1);
            if (r == null) {
                continue;
            }
            if (r.isPresent()) {
                Assertions.assertEquals(7, i);
                break;
            }
        }
    }
    @Test
    public void Test_ShouldReScan_ForKnownRoom_ThatIsDifferent_LongRoom_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", "W", " ", " ", " ", " ", " ", " ", " ", "D", " "},
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "},
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
        };

        Position doorPos1 = new Position(10, 2);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                null
        );
        InclusiveSpace oldSpace = InclusiveSpaces.from(1, 1).to(5, 3); // Only half the width
        Room currentRoom = new Room(doorPos1, oldSpace);
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        for (int i = 0; i < 1000; i++) {
            @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed();
            if (res == null) {
                continue;
            }
            Optional<Room> r = res.get(doorPos1);
            if (r == null) {
                continue;
            }
            if (r.isPresent()) {
                Assertions.assertEquals(7, i);
                break;
            }
        }
    }
}