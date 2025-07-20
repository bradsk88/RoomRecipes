package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Objects;

/**
 * @deprecated Use RoomRecipeMatches
 */
@Deprecated(since="1.18.2-0.0.6-alpha.3")
public class RoomRecipeMatch<ROOM> extends RoomWithBlocks<ROOM, BlockPos, Block> implements IRoomRecipeMatch<ROOM, ResourceLocation, BlockPos, Block> {
    private final ImmutableList<ResourceLocation> recipeIDs;

    public RoomRecipeMatch(
            ROOM room,
            ImmutableList<ResourceLocation> recipeIDs,
            Iterable<Map.Entry<BlockPos, Block>> containedBlocks
    ) {
        super(room, containedBlocks);
        this.recipeIDs = recipeIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoomRecipeMatch<?> that = (RoomRecipeMatch<?>) o;
        return Objects.equals(recipeIDs, that.recipeIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recipeIDs);
    }

    public boolean anyMatch(ResourceLocation recipeId) {
        return recipeIDs.stream().anyMatch(recipeId::equals);
    }

    @Override
    public ImmutableList<ResourceLocation> getRecipeIDs() {
        return recipeIDs;
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
