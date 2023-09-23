package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
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
        Optional<Room> adj = southRoomWithNorthOpening.adjoinedTo(
                new Position(0, 1),
                northRoomWithSouthOpening
        );
        Assertions.assertTrue(adj.isPresent());
    }
}