package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomHintsTest {

    @Test
    public void testIsRoomWithAllWalls() {
        RoomHints hints = new RoomHints(
                new XWall(new Position(0, 0), new Position(2, 0)),
                new XWall(new Position(0, 2), new Position(2, 2)),
                new ZWall(new Position(0, 0), new Position(0, 2)),
                new ZWall(new Position(2, 0), new Position(2, 2))
        );
        assertTrue(hints.isRoom(RoomDetection.WallExclusion.mustHaveAllWalls()));
    }

    @Test
    public void testIsRoomWithOpenNorthWall() {
        RoomHints hints = new RoomHints(
                null,
                new XWall(new Position(0, 2), new Position(2, 2)),
                new ZWall(new Position(0, 0), new Position(0, 2)),
                new ZWall(new Position(2, 0), new Position(2, 2))
        );
        assertTrue(hints.isRoom(RoomDetection.WallExclusion.allowNorthOpen()));
        assertFalse(hints.isRoom(RoomDetection.WallExclusion.mustHaveAllWalls()));
    }

    @Test
    public void testIsRoomWithOpenSouthWall() {
        RoomHints hints = new RoomHints(
                new XWall(new Position(0, 0), new Position(2, 0)),
                null,
                new ZWall(new Position(0, 0), new Position(0, 2)),
                new ZWall(new Position(2, 0), new Position(2, 2))
        );
        assertTrue(hints.isRoom(RoomDetection.WallExclusion.allowSouthOpen()));
        assertFalse(hints.isRoom(RoomDetection.WallExclusion.mustHaveAllWalls()));
    }

    @Test
    public void testIsRoomWithOpenWestWall() {
        RoomHints hints = new RoomHints(
                new XWall(new Position(0, 0), new Position(2, 0)),
                new XWall(new Position(0, 2), new Position(2, 2)),
                null,
                new ZWall(new Position(2, 0), new Position(2, 2))
        );
        assertTrue(hints.isRoom(RoomDetection.WallExclusion.allowWestOpen()));
        assertFalse(hints.isRoom(RoomDetection.WallExclusion.mustHaveAllWalls()));
    }

    @Test
    public void testIsRoomWithOpenEastWall() {
        RoomHints hints = new RoomHints(
                new XWall(new Position(0, 0), new Position(2, 0)),
                new XWall(new Position(0, 2), new Position(2, 2)),
                new ZWall(new Position(0, 0), new Position(0, 2)),
                null
        );
        assertTrue(hints.isRoom(RoomDetection.WallExclusion.allowEastOpen()));
        assertFalse(hints.isRoom(RoomDetection.WallExclusion.mustHaveAllWalls()));
    }

    @Test
    void adjoinedTo() {
        RoomHints southRoomWithNorthOpening = RoomHints.empty()
                .withNorthWall(new XWall(new Position(0, 2), new Position(4, 2)))
                .withNorthOpening(new XWall(new Position(2, 2), new Position(4, 2)))
                .withSouthWall(new XWall(new Position(0, 4), new Position(4, 4)))
                .withWestWall(new ZWall(new Position(0, 2), new Position(0, 4)))
                .withEastWall(new ZWall(new Position(4, 2), new Position(4, 4)));
        RoomHints northRoomWithSouthOpening = RoomHints.empty()
                .withNorthWall(new XWall(new Position(2, 0), new Position(4, 0)))
                .withSouthWall(new XWall(new Position(2, 2), new Position(4, 2)))
                .withSouthOpening(new XWall(new Position(2, 2), new Position(4, 2)))
                .withWestWall(new ZWall(new Position(2, 0), new Position(2, 2)))
                .withEastWall(new ZWall(new Position(4, 0), new Position(4, 2)));
        Optional<InclusiveSpace> adj = southRoomWithNorthOpening.adjoinedTo(
                new Position(0, 1),
                northRoomWithSouthOpening
        );
        Assertions.assertTrue(adj.isPresent());
    }
    @Test
    void adjoinedTo_2() {
        RoomHints roomWithNorthAndSouthOpening = RoomHints.empty()
                .withNorthWall(new XWall(new Position(0, 1), new Position(3, 1)))
                .withSouthWall(new XWall(new Position(0, 3), new Position(3, 3)))
                .withWestWall(new ZWall(new Position(0, 1), new Position(0, 3)))
                .withEastWall(new ZWall(new Position(3, 1), new Position(3, 3)))
                .withNorthOpening(new XWall(new Position(0, 1), new Position(2, 1)))
                .withSouthOpening(new XWall(new Position(0, 3), new Position(2, 3)));
        RoomHints toAdjoin = RoomHints.empty()
                .withNorthWall(new XWall(new Position(0, 0), new Position(2, 0)))
                .withSouthWall(new XWall(new Position(0, 1), new Position(2, 1)))
                .withSouthOpening(new XWall(new Position(0, 1), new Position(2, 1)))
                .withWestWall(new ZWall(new Position(0, 0), new Position(0, 1)))
                .withEastWall(new ZWall(new Position(2, 0), new Position(2, 1)));
        Optional<InclusiveSpace> adj = roomWithNorthAndSouthOpening.adjoinedTo(
                new Position(0, 1),
                toAdjoin
        );
        Assertions.assertTrue(adj.isPresent());
    }
}