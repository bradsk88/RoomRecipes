package ca.bradj.roomrecipes.serialization;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.adapter.RoomRecipeMatch;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
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
    private static final String NBT_RECIPE_ROOM_AA_X = "recipe_room_space_aa_x";
    private static final String NBT_RECIPE_ROOM_AA_Z = "recipe_room_space_aa_z";
    private static final String NBT_RECIPE_ROOM_BB_X = "recipe_room_space_bb_x";
    private static final String NBT_RECIPE_ROOM_BB_Z = "recipe_room_space_bb_z";
    private static final String NBT_RECIPE_ROOM_Y = "recipe_room_y";
    private static final String NBT_RECIPE_ID = "recipe_id";

    // TODO: Serialize/Deserialize blocks map

    public CompoundTag serializeNBT(ActiveRecipes<MCRoom, RoomRecipeMatch> recipes) {
        CompoundTag c = new CompoundTag();
        c.putInt(NBT_NUM_ACTIVE_RECIPES, recipes.size());
        ListTag aq = new ListTag();
        for (Map.Entry<MCRoom, RoomRecipeMatch> e : recipes.entrySet()) {
            CompoundTag rc = new CompoundTag();
            rc.putInt(NBT_RECIPE_ROOM_DOORPOS_X, e.getKey().getDoorPos().x);
            rc.putInt(NBT_RECIPE_ROOM_DOORPOS_Z, e.getKey().getDoorPos().z);
            rc.putInt(NBT_RECIPE_ROOM_AA_X, e.getKey().getSpace().getCornerA().x);
            rc.putInt(NBT_RECIPE_ROOM_AA_Z, e.getKey().getSpace().getCornerA().z);
            rc.putInt(NBT_RECIPE_ROOM_BB_X, e.getKey().getSpace().getCornerB().x);
            rc.putInt(NBT_RECIPE_ROOM_BB_Z, e.getKey().getSpace().getCornerB().z);
            rc.putString(NBT_RECIPE_ID, e.getValue().getRecipeID().toString());
            aq.add(rc);
        }
        c.put(NBT_ACTIVE_RECIPES, aq);
        return c;
    }

    public ActiveRecipes<MCRoom, RoomRecipeMatch> deserializeNBT(CompoundTag nbt) {
        Map<MCRoom, RoomRecipeMatch> aqs = new HashMap<>();
        int num = nbt.getInt(NBT_NUM_ACTIVE_RECIPES);
        ListTag aq = nbt.getList(NBT_ACTIVE_RECIPES, Tag.TAG_COMPOUND);
        for (int i = 0; i < num; i++) {
            CompoundTag compound = aq.getCompound(i);
            int x = compound.getInt(NBT_RECIPE_ROOM_DOORPOS_X);
            int z = compound.getInt(NBT_RECIPE_ROOM_DOORPOS_Z);
            Position doorPos = new Position(x, z);

            int aaX = compound.getInt(NBT_RECIPE_ROOM_AA_X);
            int aaZ = compound.getInt(NBT_RECIPE_ROOM_AA_Z);
            int bbX = compound.getInt(NBT_RECIPE_ROOM_BB_X);
            int bbZ = compound.getInt(NBT_RECIPE_ROOM_BB_Z);
            Position aa = new Position(aaX, aaZ);
            Position bb = new Position(bbX, bbZ);
            InclusiveSpace space = new InclusiveSpace(aa, bb);

            int y = compound.getInt(NBT_RECIPE_ROOM_Y);
            MCRoom key = new MCRoom(doorPos, ImmutableList.of(space), y);
            if (aqs.containsKey(key)) {
                RoomRecipes.LOGGER.error("Room is already present in map. This is probably a bug!");
            }

            aqs.put(key, new RoomRecipeMatch(
                    key, new ResourceLocation(compound.getString(NBT_RECIPE_ID)),
                    ImmutableList.of())
            );
        }
        return new ActiveRecipes<>(aqs.entrySet());
    }
}