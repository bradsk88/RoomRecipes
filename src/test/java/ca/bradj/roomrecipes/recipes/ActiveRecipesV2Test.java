package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ActiveRecipesV2Test {

    private ActiveRecipesV2<Integer> activeRecipes;
    private ActiveRecipesV2.ChangeListener<Integer> listener;

    @BeforeEach
    public void setUp() {
        activeRecipes = new ActiveRecipesV2<>();
        listener = Mockito.mock(ActiveRecipesV2.ChangeListener.class);
        activeRecipes.addChangeListener(listener);
    }

    @Test
    public void testCreateRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int recipeId = 1;

        activeRecipes.update(room, recipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, recipeId);
    }

    @Test
    public void testChangeRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int oldRecipeId = 1;
        int newRecipeId = 2;

        activeRecipes.update(room, oldRecipeId);
        activeRecipes.update(room, newRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, oldRecipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeChanged(room, oldRecipeId, newRecipeId);
    }

    @Test
    public void testUnchangedRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int sameRecipeId = 1;

        activeRecipes.update(room, sameRecipeId);
        activeRecipes.update(room, sameRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, sameRecipeId);
        Mockito.verify(listener, Mockito.never()).roomRecipeChanged(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(listener, Mockito.never()).roomRecipeDestroyed(Mockito.any(), Mockito.anyInt());
    }

    @Test
    public void testDestroyRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int recipeId = 1;

        activeRecipes.update(room, recipeId);
        activeRecipes.update(room, null);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, recipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeDestroyed(room, recipeId);
    }

    @Test
    public void testDestroyNonexistentRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int recipeId = 1;

        activeRecipes.update(room, null);
        activeRecipes.update(room, null);

        Mockito.verify(listener, Mockito.never()).roomRecipeCreated(room, recipeId);
        Mockito.verify(listener, Mockito.never()).roomRecipeDestroyed(room, recipeId);
    }
}
