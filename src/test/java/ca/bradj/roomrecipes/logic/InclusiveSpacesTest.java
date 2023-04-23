package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import org.junit.jupiter.api.Test;

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
                new InclusiveSpace(new Position(-6, -6), new Position(-1,  -1))
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
    }
}