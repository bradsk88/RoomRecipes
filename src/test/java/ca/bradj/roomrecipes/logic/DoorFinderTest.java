package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoorFinderTest {

    @Test
    public void Test() {

        boolean[][] map = {
                {true, false, false, true},
                {false, true, false, false},
                {false, false, true, false},
                {false, false, false, false}
        };

        ImmutableList<Position> expected = ImmutableList.of(
                new Position(0, 0),
                new Position(3, 0),
                new Position(1, 1),
                new Position(2, 2)
        );

        Collection<Position> dps = DoorDetection.LocateDoorsAroundPosition(
                new Position(0, 0),
                (Position dp) -> {
                    if (dp.x < 0 || dp.z < 0) {
                        return false;
                    }
                    if (dp.x >= map[0].length || dp.z >= map.length) {
                        return false;
                    }
                    return map[dp.z][dp.x];
                },
                5 // Random
        );
        assertEquals(expected, dps);
    }

}