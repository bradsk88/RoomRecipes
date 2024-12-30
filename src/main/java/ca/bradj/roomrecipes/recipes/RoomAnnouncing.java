package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.core.space.Position;

import java.util.Collection;

public interface RoomAnnouncing {
    void skipAnnounceOnFirstDetect(Collection<Position> roomsToSkip);
}
