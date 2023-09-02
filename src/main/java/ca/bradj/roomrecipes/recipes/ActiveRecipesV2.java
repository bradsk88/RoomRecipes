package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// ActiveRecipes is a unit testable module for the active recipes of a town
public class ActiveRecipesV2<KEY> {

    protected final Map<Room, KEY> activeRecipes = new HashMap<>();

    // TODO: Support multiple?
    private ChangeListener<KEY> changeListener;

    public ActiveRecipesV2() {
        this(Set.of());
    }
    public ActiveRecipesV2(Set<Map.Entry<Room, KEY>> recipes) {
        recipes.forEach(e -> activeRecipes.put(e.getKey(), e.getValue()));
    }

    public void update(
            Room roomDoorPos,
            @Nullable KEY recipe
    ) {
        RoomRecipes.LOGGER.trace("Updating recipe at " + roomDoorPos + " to " + recipe);

        if (recipe != null) {
            if (activeRecipes.containsKey(roomDoorPos)) {
                KEY oldRecipe = activeRecipes.get(roomDoorPos);
                if (oldRecipe.equals(recipe)) {
                    return;
                }
                this.activeRecipes.put(roomDoorPos, recipe);
                this.changeListener.roomRecipeChanged(roomDoorPos, oldRecipe, recipe);
                return;
            }

            this.activeRecipes.put(roomDoorPos, recipe);
            this.changeListener.roomRecipeCreated(roomDoorPos, recipe);
            return;
        }

        if (activeRecipes.containsKey(roomDoorPos)) {
            KEY oldRecipeId = activeRecipes.remove(roomDoorPos);
            this.changeListener.roomRecipeDestroyed(roomDoorPos, oldRecipeId);
            return;
        }

        if (recipe != null) {
            RoomRecipes.LOGGER.error("An unexpected recipe was removed. This is likely a bug.");
        }
    }

    public interface ChangeListener<KEY> {
        void roomRecipeCreated(Room roomDoorPos, KEY recipeId);
        void roomRecipeChanged(Room roomDoorPos, KEY oldRecipeId, KEY newRecipeId);
        void roomRecipeDestroyed(Room roomDoorPos, KEY oldRecipeId);
    }

    public void addChangeListener(ChangeListener<KEY> cl) {
        this.changeListener = cl;
    }

    public int size() {
        return activeRecipes.size();
    }

    public Set<Map.Entry<Room, KEY>> entrySet() {
        return activeRecipes.entrySet();
    }

}
