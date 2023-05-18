package ca.bradj.roomrecipes.serialization;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.recipes.ActiveRecipes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ActiveRecipesSerializer {

    public static final ActiveRecipesSerializer INSTANCE = new ActiveRecipesSerializer();

    private static final String NBT_NUM_ACTIVE_RECIPES = "num_active_recipes";
    private static final String NBT_ACTIVE_RECIPES = "active_recipes";

    private static final String NBT_RECIPE_ROOM = "recipe_room";
    private static final String NBT_RECIPE_ID = "recipe_id";

    public CompoundTag serializeNBT(ActiveRecipes<ResourceLocation> recipes) {
        CompoundTag c = new CompoundTag();
        c.putInt(NBT_NUM_ACTIVE_RECIPES, recipes.size());
        ListTag aq = new ListTag();
        for (Map.Entry<Room, ResourceLocation> e : recipes.entrySet()) {
            CompoundTag rc = new CompoundTag();
            rc.put(NBT_RECIPE_ROOM, RoomSerializer.INSTANCE.serializeNBT(e.getKey()));
            rc.putString(NBT_RECIPE_ID, e.getValue().toString());
            aq.add(rc);
        }
        c.put(NBT_ACTIVE_RECIPES, aq);
        return c;
    }

    public ActiveRecipes<ResourceLocation> deserializeNBT(CompoundTag nbt) {
        Map<Room, ResourceLocation> aqs = new HashMap<>();
        int num = nbt.getInt(NBT_NUM_ACTIVE_RECIPES);
        ListTag aq = nbt.getList(NBT_ACTIVE_RECIPES, Tag.TAG_COMPOUND);
        for (int i = 0; i < num; i++) {
            CompoundTag compound = aq.getCompound(i);
            Room room = RoomSerializer.INSTANCE.deserializeNBT(compound.getCompound(NBT_RECIPE_ROOM));
            if (aqs.containsKey(room)) {
                RoomRecipes.LOGGER.error("Room is already present in map. This is probably a bug!");
            }
            aqs.put(room, new ResourceLocation(compound.getString(NBT_RECIPE_ID)));
        }
        return new ActiveRecipes<>(aqs.entrySet());
    }
}