package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.space.Position;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// ActiveRecipes is a unit testable module for the active recipes of a town
public class ActiveRecipes<KEY> {

    protected final Map<Position, KEY> activeRecipes = new HashMap<>();

    // TODO: Support multiple?
    private ChangeListener<KEY> changeListener;

    public ActiveRecipes() {
        this(Set.of());
    }
    public ActiveRecipes(Set<Map.Entry<Position, KEY>> recipes) {
        recipes.forEach(e -> activeRecipes.put(e.getKey(), e.getValue()));
    }

    public void update(
            Position roomDoorPos,
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
        void roomRecipeCreated(Position roomDoorPos, KEY recipeId);
        void roomRecipeChanged(Position roomDoorPos, KEY oldRecipeId, KEY newRecipeId);
        void roomRecipeDestroyed(Position roomDoorPos, KEY oldRecipeId);
    }

    public void addChangeListener(ChangeListener<KEY> cl) {
        this.changeListener = cl;
    }

    public int size() {
        return activeRecipes.size();
    }

    public Set<Map.Entry<Position, KEY>> entrySet() {
        return activeRecipes.entrySet();
    }

}
