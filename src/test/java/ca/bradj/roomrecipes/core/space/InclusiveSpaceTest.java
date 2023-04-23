package ca.bradj.roomrecipes.core.space;

import ca.bradj.roomrecipes.adapter.Positions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InclusiveSpaceTest {

    @Test
    void testGetInclusiveSpaceWithEmptyCollection() {
        Collection<Position> doors = new ArrayList<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Positions.getInclusiveSpace(doors);
        });
    }

    @Test
    void testGetInclusiveSpaceWithSingleDoor() {
        Collection<Position> doors = Arrays.asList(new Position(0, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Positions.getInclusiveSpace(doors);
        });
    }

    @Test
    void testGetInclusiveSpaceWithTwoSameDoors() {
        Collection<Position> doors = Arrays.asList(new Position(1, 1), new Position(1, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Positions.getInclusiveSpace(doors);
        });
    }

    @Test
    void testGetInclusiveSpaceWithTwoDoors() {
        Collection<Position> doors = Arrays.asList(new Position(1, 1), new Position(2, 2));
        InclusiveSpace space = Positions.getInclusiveSpace(doors);
        assertEquals(new InclusiveSpace(new Position(1, 1), new Position(2, 2)), space);
    }

    @Test
    void testGetInclusiveSpaceWithMultipleDoors() {
        Collection<Position> doors = Arrays.asList(
                new Position(1, 3),
                new Position(4, 6),
                new Position(0, -2),
                new Position(7, 9)
        );
        InclusiveSpace space = Positions.getInclusiveSpace(doors);
        assertEquals(new InclusiveSpace(new Position(0, -2), new Position(7, 9)), space);
    }

    @Test
    public void testChopOff_noOverlap() {
        // No overlap
        InclusiveSpace space1 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace space2 = new InclusiveSpace(new Position(4, 4), new Position(6, 6));
        InclusiveSpace result = space1.chopOff(space2);
        assertEquals(space1, result);
    }

    @Test
    public void testChopOff_overlap() {
        // Overlap
        InclusiveSpace space1 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace space2 = new InclusiveSpace(new Position(1, 1), new Position(3, 3));
        InclusiveSpace expected = new InclusiveSpace(new Position(0, 0), new Position(1, 1));
        InclusiveSpace result = space1.chopOff(space2);
        assertEquals(expected, result);
    }

    @Test
    public void testChopOff_sameSpace() {
        // Same space
        InclusiveSpace space1 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace space2 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace expected = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace result = space1.chopOff(space2);
        assertEquals(expected, result);
    }

    @Test
    public void testChopOff_Nested() {
        // Partial overlap
        InclusiveSpace space1 = new InclusiveSpace(new Position(0, 0), new Position(4, 2));
        InclusiveSpace space2 = new InclusiveSpace(new Position(0, 0), new Position(2, 2));
        InclusiveSpace expected = new InclusiveSpace(new Position(2, 0), new Position(4, 2));
        InclusiveSpace result = space1.chopOff(space2);
        assertEquals(expected, result);
    }

}