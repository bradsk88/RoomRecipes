package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// ActiveRecipes is a unit testable module for the active recipes of a town
public class ActiveRecipes<KEY> {

    protected final Map<Room, KEY> activeRecipes = new HashMap<>();

    // TODO: Support multiple?
    private ChangeListener<KEY> changeListener;

    public ActiveRecipes() {
        this(Set.of());
    }
    public ActiveRecipes(Set<Map.Entry<Room, KEY>> recipes) {
        recipes.forEach(e -> activeRecipes.put(e.getKey(), e.getValue()));
    }

    public void update(
            Room room,
            Optional<KEY> recipe
    ) {
        RoomRecipes.LOGGER.trace("Updating recipe at " + room.getDoorPos() + " to " + recipe + " for room with space " + room.getSpace());

        if (recipe.isPresent()) {
            if (activeRecipes.containsKey(room)) {
                KEY oldRecipe = activeRecipes.get(room);
                if (oldRecipe.equals(recipe.get())) {
                    return;
                }
                this.activeRecipes.put(room, recipe.get());
                this.changeListener.roomRecipeChanged(room, oldRecipe, recipe.get());
                return;
            }

            this.activeRecipes.put(room, recipe.get());
            this.changeListener.roomRecipeCreated(room, recipe.get());
            return;
        }

        if (activeRecipes.containsKey(room)) {
            KEY oldRecipeId = activeRecipes.remove(room);
            this.changeListener.roomRecipeDestroyed(room, oldRecipeId);
            return;
        }

        if (recipe.isPresent()) {
            RoomRecipes.LOGGER.error("An unexpected recipe was removed. This is likely a bug.");
        }
    }

    public interface ChangeListener<KEY> {
        void roomRecipeCreated(Room room, KEY recipeId);
        void roomRecipeChanged(Room room, KEY oldRecipeId, KEY newRecipeId);
        void roomRecipeDestroyed(Room room, KEY oldRecipeId);
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
