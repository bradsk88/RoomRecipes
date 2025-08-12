package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WallPositionsToRoomsTest {

    WallPositionToRooms SINGLETON = new WallPositionToRooms(100);

    @Test
    public void test_Point() {
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(0, 0)
        );
        ImmutableList<InclusiveSpace> result = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void test_VerticalLine() {
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(0, 0),
                new Position(0, 1)
        );
        ImmutableList<InclusiveSpace> result = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void test_HorizontalLine() {
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(0, 0),
                new Position(1, 0)
        );
        ImmutableList<InclusiveSpace> result = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void test_TooSmallSquare() {
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(0, 0),
                new Position(1, 1)
        );
        ImmutableList<InclusiveSpace> result = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void test_Square() {
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(0, 0),
                new Position(1, 0),
                new Position(2, 0),
                new Position(0, 1),
                new Position(2, 1),
                new Position(0, 2),
                new Position(1, 2),
                new Position(2, 2)
        );
        ImmutableList<InclusiveSpace> result = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(new Position(0, 0), result.get(0).getCornerA());
        Assertions.assertEquals(new Position(2, 2), result.get(0).getCornerB());
    }

    @Test
    public void test_RoomWithMissingSideCornersAndOneInsetCorner() {
        // This list was generated from Test_ShouldFindRoomWithMissingCornersOneSideAndInsetCorner in another file
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(8, 5),
                new Position(9, 2),
                new Position(2, 1),
                new Position(1, 3),
                new Position(1, 2),
                new Position(1, 1),
                new Position(3, 1),
                new Position(4, 1),
                new Position(5, 1),
                new Position(6, 1),
                new Position(7, 1),
                new Position(8, 1),
                new Position(9, 1),
                new Position(9, 3),
                new Position(9, 4),
                new Position(7, 5),
                new Position(6, 5),
                new Position(5, 5),
                new Position(4, 5),
                new Position(3, 5),
                new Position(2, 5),
                new Position(1, 4)
        );
        ImmutableList<InclusiveSpace> spaces = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(2, spaces.size());
        InclusiveSpace space1 = spaces.get(0);
        Assertions.assertEquals(new Position(1, 1), space1.getCornerA());
        Assertions.assertEquals(new Position(8, 5), space1.getCornerB());
        InclusiveSpace space2 = spaces.get(1);
        Assertions.assertEquals(new Position(8, 1), space2.getCornerA());
        Assertions.assertEquals(new Position(9, 4), space2.getCornerB());

    }

    @Test
    public void test_RoomWithNarrowEntrance() {
        // This list was generated from Test_DetectNarrowEntrance_N in another file
        ImmutableSet<Position> positions = ImmutableSet.of(
                new Position(4, 3),
                new Position(1, 0),
                new Position(4, 4),
                new Position(1, 1),
                new Position(3, 4),
                new Position(0, 1),
                new Position(2, 4),
                new Position(0, 2),
                new Position(1, 4),
                new Position(0, 3),
                new Position(0, 4),
                new Position(3, 0),
                new Position(4, 1),
                new Position(2, 0),
                new Position(3, 1),
                new Position(4, 2)
        );
        ImmutableList<InclusiveSpace> spaces = SINGLETON.getSpaces(positions);
        Assertions.assertEquals(2, spaces.size());
        Assertions.assertEquals(ImmutableList.of(
                // TODO: Expected values will depend on implementation, or we can use assertSpacesEqual
//                InclusiveSpace.from(0, 1).to(1, 4),
//                InclusiveSpace.from(3, 1).to(4, 4)
        ), spaces);
    }

}