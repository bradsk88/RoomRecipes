package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Objects;

public class RoomRecipeMatch<ROOM> extends RoomWithBlocks<ROOM, BlockPos, Block> implements IRoomRecipeMatch<ROOM, ResourceLocation, BlockPos, Block> {
    private final ResourceLocation recipeID;

    public RoomRecipeMatch(
            ROOM room,
            ResourceLocation recipeID,
            Iterable<Map.Entry<BlockPos, Block>> containedBlocks
    ) {
        super(room, containedBlocks);
        this.recipeID = recipeID;
    }

    public boolean isSameRoomAndRecipe(RoomRecipeMatch<ROOM> o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(room, o.room) &&
                Objects.equals(recipeID, o.recipeID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoomRecipeMatch<?> that = (RoomRecipeMatch<?>) o;
        return Objects.equals(recipeID, that.recipeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recipeID);
    }

    @Override
    public ResourceLocation getRecipeID() {
        return recipeID;
    }

    @Override
    public ROOM getRoom() {
        return room;
    }

    @Override
    public ImmutableMap<BlockPos, Block> getContainedBlocks() {
        return containedBlocks;
    }
}
