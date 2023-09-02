package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;

public class RoomComparison {

    public static <R extends Room> boolean isLikelySame(R room1, R room2) {
        // If the rooms share a door position AND two corners, I'm
        // theorizing that they are probably "the same room" (just resized).
        // We'll operate under this assumption until a contradicting
        // scenario is found.
        Position doorPos = room1.getDoorPos();
        if (room1.getBackZWall().isPresent() && room2.getBackZWall().isEmpty()) {
            return false;
        }
        if (room1.getBackZWall().isEmpty() && room2.getBackZWall().isPresent()) {
            return false;
        }

        // Handle when door wall is same
        if (room1.getBackZWall().isPresent() && room2.getBackZWall().isPresent()) {
            if (room1.getBackZWall().get().getX() > doorPos.x) {
                return room2.getBackZWall().get().getX() > doorPos.x;
            }
        }

        // Handle when door wall is different
        if (room1.getBackZWall().isPresent() && room2.getBackZWall().isPresent()) {
            if (room1.getBackZWall().get().getX() > doorPos.x) {
                return room2.getBackZWall().get().getX() > doorPos.x;
            }
        }

        // FIXME: We have not handled BackXWall (like above).
        //  This will likely cause bugs.

        return true;
    }

}
