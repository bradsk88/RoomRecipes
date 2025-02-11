package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class RoomRecipeMatches<ROOM> extends RoomRecipeMatch<ROOM> {
    public RoomRecipeMatches(
            ROOM room,
            ImmutableList<ResourceLocation> recipeIDs,
            Iterable<Map.Entry<BlockPos, Block>> containedBlocks
    ) {
        super(room, recipeIDs, containedBlocks);
    }

    public RoomRecipeMatches<ROOM> with(ResourceLocation... additional) {
        ImmutableList.Builder<ResourceLocation> list = ImmutableList.builder();
        list.addAll(getRecipeIDs());
        list.add(additional);
        return new RoomRecipeMatches<>(
            room, list.build(), containedBlocks.entrySet()
        );
    }
}
