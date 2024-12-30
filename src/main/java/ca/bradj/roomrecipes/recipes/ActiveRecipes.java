package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;

import javax.annotation.Nullable;
import java.util.*;

// ActiveRecipes is a unit testable module for the active recipes of a town
public class ActiveRecipes<ROOM extends Room, KEY> implements RoomAnnouncing {

    protected final Map<ROOM, KEY> activeRecipes = new HashMap<>();

    // TODO: Support multiple?
    private ChangeListener<ROOM, KEY> changeListener;

    private final Stack<Position> roomsToSkipInitialAnnounce = new Stack<>();

    public ActiveRecipes() {
        this(Set.of());
    }
    public ActiveRecipes(Set<Map.Entry<ROOM, KEY>> recipes) {
        recipes.forEach(e -> activeRecipes.put(e.getKey(), e.getValue()));
    }

    public void update(
            @Nullable ROOM oldRoom,
            @Nullable ROOM newRoom,
            @Nullable KEY recipe
    ) {
        RoomRecipes.LOGGER.trace("Updating recipe at {} to {} at {}", oldRoom, recipe, newRoom);

        if (recipe != null) {
            if (activeRecipes.containsKey(oldRoom)) {
                KEY oldRecipe = activeRecipes.get(oldRoom);
                if (oldRecipe.equals(recipe)) {
                    if (oldRoom.equals(newRoom)) {
                        return;
                    }
                    this.activeRecipes.remove(oldRoom);
                    if (newRoom == null) {
                        this.changeListener.roomRecipeDestroyed(oldRoom, recipe);
                        return;
                    }

                    this.activeRecipes.put(newRoom, recipe);
                    this.changeListener.roomRecipeChanged(oldRoom, recipe, newRoom, recipe);
                    return;
                }
                this.activeRecipes.put(newRoom, recipe);
                this.changeListener.roomRecipeChanged(oldRoom, oldRecipe, newRoom, recipe);
                return;
            }

            this.activeRecipes.put(newRoom, recipe);
            if (newRoom != null && roomsToSkipInitialAnnounce.contains(newRoom.doorPos)) {
                roomsToSkipInitialAnnounce.remove(newRoom.doorPos);
            } else {
                this.changeListener.roomRecipeCreated(newRoom, recipe);
            }
            return;
        }

        if (activeRecipes.containsKey(oldRoom)) {
            KEY oldRecipeId = activeRecipes.remove(oldRoom);
            this.changeListener.roomRecipeDestroyed(oldRoom, oldRecipeId);
            return;
        }

        if (recipe != null) {
            RoomRecipes.LOGGER.error(
                    "An unexpected recipe was removed. This is likely a bug. [{}, {}, {}]",
                    oldRoom, newRoom, recipe
            );
        }
    }

    @Override
    public void skipAnnounceOnFirstDetect(Collection<Position> roomsToSkip) {
        this.roomsToSkipInitialAnnounce.addAll(roomsToSkip);
    }

    public void addChangeListener(ChangeListener<ROOM, KEY> cl) {
        this.changeListener = cl;
    }

    public int size() {
        return activeRecipes.size();
    }

    public Set<Map.Entry<ROOM, KEY>> entrySet() {
        return activeRecipes.entrySet();
    }

    public interface ChangeListener<ROOM, KEY> {
        void roomRecipeCreated(ROOM room, KEY recipeId);
        void roomRecipeChanged(ROOM oldRoom, KEY oldRecipeId, ROOM newRoom, KEY newRecipeId);
        void roomRecipeDestroyed(ROOM room, KEY oldRecipeId);
    }

}
