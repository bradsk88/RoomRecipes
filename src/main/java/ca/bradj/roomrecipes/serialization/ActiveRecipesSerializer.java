package ca.bradj.roomrecipes.serialization;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.adapter.RoomRecipeMatch;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.recipes.ActiveRecipes;
import com.google.common.collect.ImmutableList;
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

    private static final String NBT_RECIPE_ROOM_DOORPOS_X = "recipe_room_door_pos_x";
    private static final String NBT_RECIPE_ROOM_DOORPOS_Z = "recipe_room_door_pos_z";
    private static final String NBT_RECIPE_ID = "recipe_id";

    // TODO: Serialize/Deserialize blocks map

    public CompoundTag serializeNBT(ActiveRecipes<RoomRecipeMatch> recipes) {
        CompoundTag c = new CompoundTag();
        c.putInt(NBT_NUM_ACTIVE_RECIPES, recipes.size());
        ListTag aq = new ListTag();
        for (Map.Entry<Position, RoomRecipeMatch> e : recipes.entrySet()) {
            CompoundTag rc = new CompoundTag();
            rc.putInt(NBT_RECIPE_ROOM_DOORPOS_X, e.getKey().x);
            rc.putInt(NBT_RECIPE_ROOM_DOORPOS_Z, e.getKey().z);
            rc.putString(NBT_RECIPE_ID, e.getValue().getRecipeID().toString());
            aq.add(rc);
        }
        c.put(NBT_ACTIVE_RECIPES, aq);
        return c;
    }

    public ActiveRecipes<RoomRecipeMatch> deserializeNBT(CompoundTag nbt) {
        Map<Position, RoomRecipeMatch> aqs = new HashMap<>();
        int num = nbt.getInt(NBT_NUM_ACTIVE_RECIPES);
        ListTag aq = nbt.getList(NBT_ACTIVE_RECIPES, Tag.TAG_COMPOUND);
        for (int i = 0; i < num; i++) {
            CompoundTag compound = aq.getCompound(i);
            int x = compound.getInt(NBT_RECIPE_ROOM_DOORPOS_X);
            int z = compound.getInt(NBT_RECIPE_ROOM_DOORPOS_Z);
            Position doorPos = new Position(x, z);
            if (aqs.containsKey(doorPos)) {
                RoomRecipes.LOGGER.error("Room is already present in map. This is probably a bug!");
            }
            aqs.put(doorPos, new RoomRecipeMatch(new ResourceLocation(compound.getString(NBT_RECIPE_ID)), ImmutableList.of()));
        }
        return new ActiveRecipes<>(aqs.entrySet());
    }
}