package ca.bradj.roomrecipes.logic;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.interfaces.WallDetector;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Random;
import java.util.logging.ConsoleHandler;

import static org.junit.jupiter.api.Assertions.*;

class InclusiveSpacesTest {


    @Test
    public void testOverlapOnXZPlane() {
        // Test cases where spaces overlap
        assertTrue(InclusiveSpaces.overlapOnXZPlane(
                InclusiveSpace.from(0, 0).to(5, 5),
                InclusiveSpace.from(3, 3).to(8, 8)
        ));

        assertTrue(InclusiveSpaces.overlapOnXZPlane(
                InclusiveSpace.from(0, 0).to(5, 5),
                InclusiveSpace.from(-3, -3).to(2, 2)
        ));

        assertTrue(InclusiveSpaces.overlapOnXZPlane(
                InclusiveSpace.from(0, 0).to(5, 5),
                InclusiveSpace.from(0, 0).to(5, 5)
        ));

        // Test cases where spaces do not overlap
        assertFalse(InclusiveSpaces.overlapOnXZPlane(
                InclusiveSpace.from(0, 0).to(5, 10),
                InclusiveSpace.from(5, 5).to(10, 10)
        ));

        assertFalse(InclusiveSpaces.overlapOnXZPlane(
                InclusiveSpace.from(0, 0).to(5, 5),
                InclusiveSpace.from(6, 6).to(10, 10)
        ));

        assertFalse(InclusiveSpaces.overlapOnXZPlane(
                InclusiveSpace.from(0, 0).to(5, 5),
                InclusiveSpace.from(-6, -6).to(-1, -1)
        ));
    }

    @Test
    public void testCalculateArea() {
        // Test a simple 2x2 space
        InclusiveSpace space1 = InclusiveSpace.from(0, 0).to(1, 1);
        double expectedArea1 = 4;
        assertEquals(expectedArea1, InclusiveSpaces.calculateArea(space1), 0.001);

        // Test a larger 5x5 space
        InclusiveSpace space2 = InclusiveSpace.from(0, 0).to(4, 4);
        double expectedArea2 = 25;
        assertEquals(expectedArea2, InclusiveSpaces.calculateArea(space2), 0.001);

        // Test a space with negative length and width (should have positive area)
        InclusiveSpace space4 = InclusiveSpace.from(-2, -2).to(2, 2);
        double expectedArea4 = 25;
        assertEquals(expectedArea4, InclusiveSpaces.calculateArea(space4), 0.001);

        // Test a collection of simple 2x2 spaces
        ImmutableList<InclusiveSpace> spaces = ImmutableList.of(
                InclusiveSpace.from(0, 0).to(1, 1),
                InclusiveSpace.from(1, 1).to(2, 2)
        );
        assertEquals(8, InclusiveSpaces.calculateArea(spaces), 0.001);
    }

    @Disabled("Low priority")
    @Test
    void getAllEnclosedPositions() {

        InclusiveSpace space = InclusiveSpace.from(0, 0).to(2, 2);
        Collection<Position> posz = InclusiveSpaces.getAllEnclosedPositions(space);
        Assertions.assertEquals(
                ImmutableList.of(
                        new Position(1, 1)
                ), ImmutableList.copyOf(posz)
        );

    }

    @Disabled("Low priority")
    @Test
    void getAllEnclosedPositions_2() {

        InclusiveSpace space = InclusiveSpace.from(0, 0).to(3, 3);
        Collection<Position> posz = InclusiveSpaces.getAllEnclosedPositions(space);
        Assertions.assertEquals(
                ImmutableList.of(
                        new Position(1, 1),
                        new Position(2, 1),
                        new Position(1, 2),
                        new Position(2, 2)
                ), ImmutableList.copyOf(posz)
        );

    }

    @Test
    void getRandomEnclosedPosition() {
        Position pos = InclusiveSpaces.getRandomEnclosedPosition(
                InclusiveSpace.from(
                        new Position(0, 0)
                ).to(
                        new Position(2, 2)
                ), new Random()::nextInt
        );
        Assertions.assertTrue(pos.x == 1 || pos.z == 1);
    }

    @Test
    void getMidpoint() {
        Position pos = InclusiveSpaces.getMidpoint(InclusiveSpace.from(
                new Position(0, 0)
        ).to(
                new Position(2, 2)
        ));
        Assertions.assertEquals(new Position(1, 1), pos);
    }

    @Test
    void contains() {
        InclusiveSpace space = InclusiveSpace.from(107, -136).to(114, -132);
        InclusiveSpaces.contains(ImmutableList.of(space), new Position(110, -131));
    }

    @Test
    public void Test_IsWhole_DetectInsetCorners_N() { // TODO: East,South,West
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "W", "_", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "W", "_", "W", "W"},
                {"W", "W", "W", "W", "W"}
        };

        WallDetector wd = TestHelpers.WD(map);

        InclusiveSpace space = InclusiveSpace.from(0, 0).to(4, 4);
        boolean result = InclusiveSpaces.isWhole(space, wd::IsWall);
        assertTrue(result);
    }
    @Test
    public void Test_IsWhole_DividedDownMiddle_Z() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "_", "W", "_", "W"},
                {"W", "_", "W", "_", "W"},
                {"W", "_", "W", "_", "W"},
                {"W", "W", "W", "W", "W"}
        };

        WallDetector wd = TestHelpers.WD(map);

        InclusiveSpace space = InclusiveSpace.from(0, 0).to(4, 4);
        boolean result = InclusiveSpaces.isWhole(space, wd::IsWall);
        assertFalse(result);
    }
    @Test
    public void Test_IsWhole_DividedDownMiddle_X() {
        java.util.logging.Logger.getLogger(RoomRecipes.LOGGER.getName()).addHandler(new ConsoleHandler());
        Configurator.setLevel(RoomRecipes.LOGGER.getName(), Level.TRACE);
        // _ = air
        // W = wall
        // D = door
        String[][] map = {
                {"W", "W", "W", "W", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "w", "W", "w", "W"},
                {"W", "_", "_", "_", "W"},
                {"W", "W", "W", "W", "W"}
        };

        WallDetector wd = TestHelpers.WD(map);

        InclusiveSpace space = InclusiveSpace.from(0, 0).to(4, 4);
        boolean result = InclusiveSpaces.isWhole(space, wd::IsWall);
        assertFalse(result);
    }
}