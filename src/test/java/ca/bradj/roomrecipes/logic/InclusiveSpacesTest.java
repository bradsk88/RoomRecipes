package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class InclusiveSpacesTest {


    @Test
    public void testOverlapOnXZPlane() {
        // Test cases where spaces overlap
        assertTrue(InclusiveSpaces.overlapOnXZPlane(
                new InclusiveSpace(new Position(0, 0), new Position(5, 5)),
                new InclusiveSpace(new Position(3, 3), new Position(8, 8))
        ));

        assertTrue(InclusiveSpaces.overlapOnXZPlane(
                new InclusiveSpace(new Position(0, 0), new Position(5, 5)),
                new InclusiveSpace(new Position(-3, -3), new Position(2, 2))
        ));

        assertTrue(InclusiveSpaces.overlapOnXZPlane(
                new InclusiveSpace(new Position(0, 0), new Position(5, 5)),
                new InclusiveSpace(new Position(0, 0), new Position(5, 5))
        ));

        // Test cases where spaces do not overlap
        assertFalse(InclusiveSpaces.overlapOnXZPlane(
                new InclusiveSpace(new Position(0, 0), new Position(5, 10)),
                new InclusiveSpace(new Position(5, 5), new Position(10, 10))
        ));

        assertFalse(InclusiveSpaces.overlapOnXZPlane(
                new InclusiveSpace(new Position(0, 0), new Position(5, 5)),
                new InclusiveSpace(new Position(6, 6), new Position(10, 10))
        ));

        assertFalse(InclusiveSpaces.overlapOnXZPlane(
                new InclusiveSpace(new Position(0, 0), new Position(5, 5)),
                new InclusiveSpace(new Position(-6, -6), new Position(-1, -1))
        ));
    }

    @Test
    public void testCalculateArea() {
        // Test a simple 2x2 space
        InclusiveSpace space1 = new InclusiveSpace(new Position(0, 0), new Position(1, 1));
        double expectedArea1 = 4;
        assertEquals(expectedArea1, InclusiveSpaces.calculateArea(space1), 0.001);

        // Test a larger 5x5 space
        InclusiveSpace space2 = new InclusiveSpace(new Position(0, 0), new Position(4, 4));
        double expectedArea2 = 25;
        assertEquals(expectedArea2, InclusiveSpaces.calculateArea(space2), 0.001);

        // Test a space with negative length and width (should have positive area)
        InclusiveSpace space4 = new InclusiveSpace(new Position(-2, -2), new Position(2, 2));
        double expectedArea4 = 25;
        assertEquals(expectedArea4, InclusiveSpaces.calculateArea(space4), 0.001);

        // Test a collection of simple 2x2 spaces
        ImmutableList<InclusiveSpace> spaces = ImmutableList.of(
                new InclusiveSpace(new Position(0, 0), new Position(1, 1)),
                new InclusiveSpace(new Position(1, 1), new Position(2, 2))
        );
        assertEquals(8, InclusiveSpaces.calculateArea(spaces), 0.001);
    }

    @Test
    void getAllEnclosedPositions() {

        InclusiveSpace space = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        Collection<Position> posz = InclusiveSpaces.getAllEnclosedPositions(space);
        Assertions.assertEquals(ImmutableList.of(
                new Position(1, 1)
        ), ImmutableList.copyOf(posz));

    }

    @Test
    void getAllEnclosedPositions_2() {

        InclusiveSpace space = new InclusiveSpace(new Position(0, 0), new Position(3, 3));
        Collection<Position> posz = InclusiveSpaces.getAllEnclosedPositions(space);
        Assertions.assertEquals(ImmutableList.of(
                new Position(1, 1),
                new Position(2, 1),
                new Position(1, 2),
                new Position(2, 2)
        ), ImmutableList.copyOf(posz));

    }

    @Test
    void getRandomEnclosedPosition() {
        Position pos = InclusiveSpaces.getRandomEnclosedPosition(new InclusiveSpace(
                new Position(0, 0),
                new Position(2, 2)
        ), new Random());
        Assertions.assertTrue(pos.x == 1 || pos.z == 1);
    }

    @Test
    void getMidpoint() {
        Position pos = InclusiveSpaces.getMidpoint(new InclusiveSpace(
                new Position(0, 0),
                new Position(2, 2)
        ));
        Assertions.assertEquals(new Position(1, 1), pos);
    }
}