package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class ActiveRecipesTest {

    private ActiveRecipes<Integer> activeRecipes;
    private ActiveRecipes.ChangeListener<Integer> listener;

    @BeforeEach
    public void setUp() {
        activeRecipes = new ActiveRecipes<>();
        listener = Mockito.mock(ActiveRecipes.ChangeListener.class);
        activeRecipes.addChangeListener(listener);
    }

    @Test
    public void testCreateRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int recipeId = 1;

        activeRecipes.update(room.getDoorPos(), recipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room.getDoorPos(), recipeId);
    }

    @Test
    public void testChangeRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int oldRecipeId = 1;
        int newRecipeId = 2;

        activeRecipes.update(room.getDoorPos(), oldRecipeId);
        activeRecipes.update(room.getDoorPos(), newRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room.getDoorPos(), oldRecipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeChanged(room.getDoorPos(), oldRecipeId, newRecipeId);
    }

    @Test
    public void testUnchangedRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int sameRecipeId = 1;

        activeRecipes.update(room.getDoorPos(), sameRecipeId);
        activeRecipes.update(room.getDoorPos(), sameRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room.getDoorPos(), sameRecipeId);
        Mockito.verify(listener, Mockito.never()).roomRecipeChanged(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(listener, Mockito.never()).roomRecipeDestroyed(Mockito.any(), Mockito.anyInt());
    }

    @Test
    public void testDestroyRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int recipeId = 1;

        activeRecipes.update(room.getDoorPos(), recipeId);
        activeRecipes.update(room.getDoorPos(), null);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room.getDoorPos(), recipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeDestroyed(room.getDoorPos(), recipeId);
    }

    @Test
    public void testDestroyNonexistentRecipe() {
        Room room = new Room(new Position(0, 0), new InclusiveSpace(new Position(0, 0), new Position(1, 1)));
        int recipeId = 1;

        activeRecipes.update(room.getDoorPos(), null);
        activeRecipes.update(room.getDoorPos(), null);

        Mockito.verify(listener, Mockito.never()).roomRecipeCreated(room.getDoorPos(), recipeId);
        Mockito.verify(listener, Mockito.never()).roomRecipeDestroyed(room.getDoorPos(), recipeId);
    }
}
