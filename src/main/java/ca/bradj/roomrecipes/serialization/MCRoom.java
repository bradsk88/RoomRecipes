package ca.bradj.roomrecipes.serialization;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;

import java.util.Collection;

public class MCRoom extends Room {
    public final int yCoord;

    public MCRoom(Position doorPos, Collection<InclusiveSpace> spaces, int y) {
        super(doorPos, spaces);
        this.yCoord = y;
    }
}
