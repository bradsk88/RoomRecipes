package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.rooms.XWall;
import ca.bradj.roomrecipes.rooms.ZWall;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

}