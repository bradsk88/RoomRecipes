package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.core.space.ThreePosition;
import ca.bradj.roomrecipes.recipes.RecipeDetectionClean.Match;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RecipeDetectionCleanTest {

    private static String getBlockAt(
            String[][] map,
            int x,
            int y
    ) {
        if (x < 0 || y < 0 || x >= map[0].length || y >= map.length) {
            return null;
        }
        return map[y][x];
    }

    record TestRecipe(String name, ImmutableList<String> ingredients) implements Comparable<TestRecipe> {
        @Override
        public int compareTo(@NotNull RecipeDetectionCleanTest.TestRecipe o) {
            return this.name.compareTo(o.name);
        }
    }

    // Regression test
    @Test
    void testRoomWithMissingCornerAndInsetCorner() {
        // @formatter:off
        String[][] map = {
                {"W", "W", "W", "W", "W", "W"},
                {"W", "_", "B", "B", "W", "W"}, // <-- Inset corner
                {"D", "_", "_", "_", "_", "W"},
                {"W", "_", "_", "_", "_", "W"},
                {"W", "W", "W", "W", "W", "_"} // <-- Missing corner
        };
        // @formatter:on

        Match<Room, TestRecipe, String> result = RecipeDetectionClean.getActiveRecipes(
                tp -> getBlockAt(map, tp.x, tp.z),
                new Room(
                        new Position(0, 2),
                        ImmutableList.of(InclusiveSpace.from(0, 0).to(4, 1), InclusiveSpace.from(0, 1).to(5, 4))
                ),
                0,
                0,
                () -> ImmutableList.of(new TestRecipe("bedroom", ImmutableList.of("B", "B"))),
                (recipe, blocks) -> blocks.containsAll(recipe.ingredients()),
                recipe -> true
        );
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.recipeIDs().size());
        Assertions.assertEquals("bedroom", result.recipeIDs().get(0).name);
    }

    @Test
    void testGetBlocksInRoomWithTwoSpaces() {

        // @formatter:off
        String[][] map = {
                {"W", "W", "W", "W", "W", "W"},
                {"D", "0", "1", "2", "W", "W"},
                {"W", "3", "4", "5", "6", "W"},
                {"W", "7", "8", "9", "A", "W"},
                {"W", "W", "W", "W", "W", "_"}
        };
        // @formatter:on

        ImmutableMap<ThreePosition, String> result = RecipeDetectionClean.getBlocksInRoom(
                tp -> getBlockAt(map, tp.x, tp.z),
                ImmutableList.of(
                        InclusiveSpace.from(0, 0).to(4, 1),
                        InclusiveSpace.from(0, 2).to(5, 4)
                ),
                0,
                0
        );
        ImmutableList<String> expected = ImmutableList.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A");
        Assertions.assertEquals(expected, ImmutableList.sortedCopyOf(result.values()));
    }
}