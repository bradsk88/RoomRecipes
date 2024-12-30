package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ActiveRecipesTest {

    private ActiveRecipes<Room, Integer> activeRecipes;
    private ActiveRecipes.ChangeListener<Room, Integer> listener;

    @BeforeEach
    public void setUp() {
        activeRecipes = new ActiveRecipes<>();
        listener = Mockito.mock(ActiveRecipes.ChangeListener.class);
        activeRecipes.addChangeListener(listener);
    }

    @Test
    public void testCreateRecipe() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        int recipeId = 1;

        activeRecipes.update(null, room, recipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, recipeId);
    }

    @Test
    public void testChangeRecipe() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        int oldRecipeId = 1;
        int newRecipeId = 2;

        activeRecipes.update(room, room, oldRecipeId);
        activeRecipes.update(room, room, newRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, oldRecipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeChanged(room, oldRecipeId, room, newRecipeId);
    }

    @Test
    public void testChangeRecipeRoom() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        Room newRoom = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(2, 2));
        int sameRecipeId = 1;

        activeRecipes.update(null, room, sameRecipeId);
        activeRecipes.update(room, newRoom, sameRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, sameRecipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeChanged(room, sameRecipeId, newRoom, sameRecipeId);
    }

    @Test
    public void testUnchangedRecipe() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        int sameRecipeId = 1;

        activeRecipes.update(room, room, sameRecipeId);
        activeRecipes.update(room, room, sameRecipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, sameRecipeId);
        Mockito.verify(listener, Mockito.never()).roomRecipeChanged(Mockito.any(), Mockito.anyInt(), Mockito.any(), Mockito.anyInt());
        Mockito.verify(listener, Mockito.never()).roomRecipeDestroyed(Mockito.any(), Mockito.anyInt());
    }

    @Test
    public void testDestroyRecipe() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        int recipeId = 1;

        activeRecipes.update(room, room, recipeId);
        activeRecipes.update(room, room, null);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, recipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeDestroyed(room, recipeId);
    }

    @Test
    public void testDestroyRecipeRoom() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        int recipeId = 1;

        activeRecipes.update(null, room, recipeId);
        activeRecipes.update(room, null, recipeId);

        Mockito.verify(listener, Mockito.times(1)).roomRecipeCreated(room, recipeId);
        Mockito.verify(listener, Mockito.times(1)).roomRecipeDestroyed(room, recipeId);
    }

    @Test
    public void testDestroyNonexistentRecipe() {
        Room room = new Room(new Position(0, 0), InclusiveSpace.from(0, 0).to(1, 1));
        int recipeId = 1;

        activeRecipes.update(room, room, null);
        activeRecipes.update(room, room, null);

        Mockito.verify(listener, Mockito.never()).roomRecipeCreated(room, recipeId);
        Mockito.verify(listener, Mockito.never()).roomRecipeDestroyed(room, recipeId);
    }
}
