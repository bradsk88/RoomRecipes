package ca.bradj.roomrecipes.rooms;

import ca.bradj.roomrecipes.logic.Direction;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;

public interface Wall<W extends Wall<W>> {
    boolean sameLengthOnAxis(Wall<?> wall);

    W shiftedNegative(int i);
    W shiftedPositive(int i);

    W extendNegative(int i);
    W extendPositive(int i);

    XWall toXWall();

    ZWall toZWall();

    boolean isLargerOnAxis(Wall<?> wall);

    boolean isSameContentOnAxis(
            Wall<?> w,
            WallDetector wd
    );

    Wall<?> shifted(
            Direction s,
            int i
    );
}
