package ca.bradj.roomrecipes.recipes;

import com.google.common.collect.ImmutableMultiset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoMCRomRecipeTest {

    @Test
    void foundMatchRecipe_WhenRoomHasRightItemsWrongQuantity() {
        boolean result = NoMCRomRecipe.foundMatchRecipe(
                ImmutableMultiset.of("F", "T", "C", "T", "T"),
                ImmutableMultiset.of("T", "C", "C", "T")
        );
        assertFalse(result);
    }
    @Test
    void foundMatchRecipe_WhenRoomHasExtra() {
        boolean result = NoMCRomRecipe.foundMatchRecipe(
                ImmutableMultiset.of("F", "T", "C", "T"),
                ImmutableMultiset.of("F", "T", "C")
        );
        assertTrue(result);
    }
}