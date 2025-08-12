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

import java.util.ArrayList;
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
        Room currentRoom = new Room(doorPos1, InclusiveSpace.from(1, 1).to(10, 3));
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
        Room currentRoom = new Room(doorPos1, InclusiveSpace.from(1, 1).to(10, 3));
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
        Room currentRoom = new Room(doorPos1, InclusiveSpace.from(1, 1).to(3, 10));
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
                {" ", "W", "W", "W", " "},
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
        Room currentRoom = new Room(doorPos1, InclusiveSpace.from(1, 1).to(3, 10));
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
        InclusiveSpace oldSpace = InclusiveSpace.from(1, 1).to(5, 3); // Only half the width
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
        InclusiveSpace oldSpace = InclusiveSpace.from(1, 1).to(5, 3); // Only half the width
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
    public void Test_ShouldFindRoomWithMissingCornersOneSideAndInsetCorner() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5    6    7    8    9   10
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 0
                {" ", "W", "W", "W", "W", "W", "W", "W", "W", "W", " "}, // 1
                {" ", "W", " ", " ", " ", " ", " ", " ", " ", "W", " "}, // 2
                {" ", "D", " ", " ", " ", " ", " ", " ", " ", "W", " "}, // 3
                {" ", "W", " ", " ", " ", " ", " ", " ", "W", "W", " "}, // 4
                {" ", " ", "W", "W", "W", "W", "W", "W", "W", " ", " "}, // 5
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 6
        };

        Position doorPos1 = new Position(1, 3);
        ArrayList<String> recorder = new ArrayList<>();
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(doorPos1),
                20,
                1000,
                WD(map),
                false,
                recorder::add
        );
        Room currentRoom = new Room(
                doorPos1, ImmutableList.of(
                InclusiveSpace.from(1, 1).to(9, 4),
                InclusiveSpace.from(1, 4).to(8, 5)
        )
        );
        Function<Position, Optional<Room>> currentState = p -> Optional.of(currentRoom);

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // The first iteration will return null - the detector only returns AFTER checking all doors
        Assertions.assertNull(res);

        res = d.proceed(currentState);
        Assertions.assertNotNull(res);

        // It should be found after one iteration
        Assertions.assertNotNull(res);
        Optional<Room> room = res.get(doorPos1);
        Assertions.assertNotNull(room);
        Assertions.assertTrue(room.isPresent());
    }

    @Test
    public void Test_ShouldFindRoomsWithDoorOnSharedWall_E() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5    6    7    8    9   10
                {" ", " ", " ", " ", " ", " ", " "}, // 0
                {" ", "W", "W", "W", "W", "W", " "}, // 1
                {" ", "D", " ", "D", " ", "W", " "}, // 2
                {" ", "W", "W", "W", "W", "W", " "}, // 3
                {" ", " ", " ", " ", " ", " ", " "}, // 4
        };

        Position leftDoor = new Position(1, 2);
        Position midDoor = new Position(3, 2);
        ArrayList<String> recorder = new ArrayList<>();
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(leftDoor, midDoor),
                20,
                1000,
                WD(map),
                false,
                recorder::add
        );
        Function<Position, Optional<Room>> currentState = p -> Optional.empty();

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // COMMENTS IN THIS TEST ASSUME "WALL WALKING" METHOD
        // The test should still pass with other methods, but might
        // require a different number of "processing" iterations

        // The first iteration should fine the room for the left door and
        // stash it on the detector but it will return null, waiting for
        // post-processing
        Assertions.assertNull(res);

        // The second iteration should fine the room for the middle door and
        // stash it on the detector but it will return null, waiting for
        // post-processing
        res = d.proceed(currentState);
        Assertions.assertNull(res);

        // The third iteration will post-process the found rooms. This will
        // take care of the overlapping spaces that get detected in earlier
        // stages.
        res = d.proceed(currentState);
        Assertions.assertNotNull(res);

        Optional<Room> leftRoom = res.get(leftDoor);
        Optional<Room> midRoom = res.get(midDoor);
        Assertions.assertTrue(leftRoom.isPresent());
        Assertions.assertTrue(midRoom.isPresent());

        Assertions.assertEquals(1, leftRoom.get().getSpaces().size());
        Assertions.assertEquals(1, midRoom.get().getSpaces().size());

        Assertions.assertEquals(new Position(1, 1), leftRoom.get().getSpaces().get(0).getCornerA());
        Assertions.assertEquals(new Position(3, 3), leftRoom.get().getSpaces().get(0).getCornerB());

        Assertions.assertEquals(new Position(3, 1), midRoom.get().getSpaces().get(0).getCornerA());
        Assertions.assertEquals(new Position(5, 3), midRoom.get().getSpaces().get(0).getCornerB());
    }

    @Test
    public void Test_ShouldFindRoomsWithDoorOnSharedWall_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5    6    7    8    9   10
                {" ", " ", " ", " ", " ", " ", " "}, // 0
                {" ", "W", "W", "W", "W", "W", " "}, // 1
                {" ", "W", " ", "D", " ", "D", " "}, // 2
                {" ", "W", "W", "W", "W", "W", " "}, // 3
                {" ", " ", " ", " ", " ", " ", " "}, // 4
        };

        Position rightDoor = new Position(5, 2);
        Position mid = new Position(3, 2);
        ArrayList<String> recorder = new ArrayList<>();
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(mid, rightDoor),
                20,
                1000,
                WD(map),
                false,
                recorder::add
        );
        Function<Position, Optional<Room>> currentState = p -> Optional.empty();

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // COMMENTS IN THIS TEST ASSUME "WALL WALKING" METHOD
        // The test should still pass with other methods, but might
        // require a different number of "processing" iterations

        // The first iteration should find the RIGHT space for the left door
        // and stash it on the detector. But, it will return null in because
        // post-processing will be required to correctly associate it with
        // the LEFT space.
        Assertions.assertNull(res);

        // The second iteration will not find anything for the right door,
        // because it searches north and tries to walk the wall in a clockwise
        // direction - which is not possible. Another iteration will be needed.
        res = d.proceed(currentState);
        Assertions.assertNull(res);

        // The third iteration will not find anything for the right door,
        // because it searches east and tries to walk the wall in a clockwise
        // direction - which is not possible. Another iteration will be needed.
        res = d.proceed(currentState);
        Assertions.assertNull(res);

        // The fourth iteration WILL find a room for the right door, although
        // that room will be the full space from 1,1 to 5,3. Nothing is
        // returned yet, because we still need the post-processing step.
        res = d.proceed(currentState);
        Assertions.assertNull(res);

        // The fifth iteration will post-process the found rooms. This will
        // take care of the overlapping spaces that were detected in earlier
        // stages.
        res = d.proceed(currentState);
        Assertions.assertNotNull(res);

        Optional<Room> midRoom = res.get(mid);
        Optional<Room> rightRoom = res.get(rightDoor);
        Assertions.assertTrue(midRoom.isPresent());
        Assertions.assertTrue(rightRoom.isPresent());

        Assertions.assertEquals(1, midRoom.get().getSpaces().size());
        Assertions.assertEquals(1, rightRoom.get().getSpaces().size());

        ImmutableList<Room> expected = ImmutableList.of(
                new Room(mid, InclusiveSpace.from(1, 1).to(3, 3)),
                new Room(rightDoor, InclusiveSpace.from(3, 1).to(5, 3))
        );

        ImmutableList<Room> actual = ImmutableList.of(
                midRoom.get(),
                rightRoom.get()
        );

        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void Test_ShouldFindRoomsInSimpleSquare_W() {

        // A = air
        // W = wall
        // D = door
        String[][] map = {
                //0    1    2    3    4    5    6    7    8    9   10
                {" ", " ", " ", " ", " ", " ", " "}, // 0
                {" ", " ", " ", "W", "W", "W", " "}, // 1
                {" ", " ", " ", "W", " ", "D", " "}, // 2
                {" ", " ", " ", "W", "W", "W", " "}, // 3
                {" ", " ", " ", " ", " ", " ", " "}, // 4
        };

        Position rightDoor = new Position(5, 2);
        LevelRoomDetector d = new LevelRoomDetector(
                ImmutableList.of(rightDoor),
                20,
                1000,
                WD(map),
                false,
                System.out::println
        );
        Function<Position, Optional<Room>> currentState = p -> Optional.empty();

        @Nullable ImmutableMap<Position, Optional<Room>> res = d.proceed(currentState);

        // COMMENTS IN THIS TEST ASSUME "WALL WALKING" METHOD
        // The test should still pass with other methods, but might
        // require a different number of "processing" iterations

        // The first iteration will not find a room, because it searches
        // north and exits if it finds no initial walls
        Assertions.assertNull(res);

        // The second iteration will not find a room, because it searches
        // east and exits if it finds no initial walls
        res = d.proceed(currentState);
        Assertions.assertNull(res);

        // The third iteration WILL find a room, because it searches south
        // and is able to follow a clockwise spiral to detect all walls.
        res = d.proceed(currentState);
        Assertions.assertNull(res);

        // The fourth iteration will post-process the found room (which,
        // for a single, simple room is basically a no-op) and return
        // the result.
        res = d.proceed(currentState);
        Assertions.assertNotNull(res);

        Optional<Room> rightRoom = res.get(rightDoor);
        Assertions.assertTrue(rightRoom.isPresent());

        Assertions.assertEquals(1, rightRoom.get().getSpaces().size());

        InclusiveSpace expected = InclusiveSpace.from(3, 1).to(5, 3);

        InclusiveSpace actual = rightRoom.get().getSpaces().get(0);

        Assertions.assertEquals(expected, actual);
    }
}