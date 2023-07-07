package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Objects;

public class RoomRecipeMatch {
    private final ResourceLocation recipeID; // TODO: Make this optional
    private final ImmutableMap<BlockPos, Block> containedBlocks;

    public RoomRecipeMatch(
            ResourceLocation recipeID,
            Iterable<Map.Entry<BlockPos, Block>> containedBlocks
    ) {
        this.recipeID = recipeID;
        ImmutableMap.Builder<BlockPos, Block> b = ImmutableMap.builder();
        containedBlocks.forEach(e -> b.put(e.getKey(), e.getValue()));
        this.containedBlocks = b.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomRecipeMatch that = (RoomRecipeMatch) o;
        return Objects.equals(recipeID, that.recipeID) && Objects.equals(
                containedBlocks,
                that.containedBlocks
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeID, containedBlocks);
    }

    @Override
    public String toString() {
        return "RoomRecipeMatch{" +
                "recipeID=" + recipeID +
                ", containedBlocks=" + containedBlocks +
                '}';
    }

    public ResourceLocation getRecipeID() {
        return recipeID;
    }

    public ImmutableMap<BlockPos, Block> getContainedBlocks() {
        return containedBlocks;
    }
}
