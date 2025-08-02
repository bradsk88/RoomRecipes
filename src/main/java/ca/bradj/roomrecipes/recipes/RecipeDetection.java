package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.adapter.RoomRecipeMatches;
import ca.bradj.roomrecipes.core.space.ThreePosition;
import ca.bradj.roomrecipes.serialization.MCRoom;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RecipeDetection {

    public static Optional<RoomRecipeMatches<MCRoom>> getActiveRecipes(
            Level level,
            MCRoom room,
            boolean getFarmRecipesOnly
    ) {
        RecipeDetectionClean.Match<MCRoom, RoomRecipe, Block> activeRecipes = RecipeDetectionClean.getActiveRecipes(
                tp -> level.getBlockState(pos(tp)).getBlock(),
                room,
                room.yCoord,
                room.yCoord + 1,
                () -> ImmutableList.copyOf(level.getRecipeManager().getAllRecipesFor(RecipesInit.ROOM)),
                (recipe, blocks) -> recipe.matches(simulateContainer(blocks), level),
                recipe -> getFarmRecipesOnly || !recipe.isFarmRecipe()
        );
        if (activeRecipes == null) {
            return Optional.empty();
        }
        return Optional.of(new RoomRecipeMatches<>(
                activeRecipes.room(),
                activeRecipes.recipeIDs().stream().map(RoomRecipe::getId).collect(ImmutableList.toImmutableList()),
                RecipeDetection.pos(activeRecipes.containedBlocks())
        ));
    }

    private static Iterable<Map.Entry<BlockPos, Block>> pos(Iterable<Map.Entry<ThreePosition, Block>> entries) {
        return Lists.transform(
                ImmutableList.copyOf(entries),
                e -> new AbstractMap.SimpleImmutableEntry<>(pos(e.getKey()), e.getValue())
        );
    }

    private static @NotNull SimpleContainer simulateContainer(ImmutableList<Block> blocks) {
        ItemStack[] list = blocks.stream().map(b -> new ItemStack(b, 1)).toArray(ItemStack[]::new);
        return new SimpleContainer(list);
    }

    public static ImmutableMap<BlockPos, Block> getBlocksInRoom(
            Level level,
            MCRoom room,
            boolean includeWallBlocks
    ) {
        return getBlocksInRoomV2((bp) -> level.getBlockState(bp).getBlock(), room, includeWallBlocks);
    }

    public static ImmutableMap<BlockPos, Block> getBlocksInRoomV2(
            Function<BlockPos, Block> level,
            MCRoom room,
            boolean includeWallBlocks
    ) {
        ImmutableMap<ThreePosition, Block> blocks = RecipeDetectionClean.getBlocksInRoom(
                tp -> level.apply(pos(tp)),
                room.getSpaces(),
                room.yCoord,
                room.yCoord + 1
        );
        return ImmutableMap.copyOf(
                Lists.transform(
                        ImmutableList.copyOf(blocks.entrySet()),
                        e -> new AbstractMap.SimpleImmutableEntry<>(pos(e.getKey()), e.getValue())
                )
        );
    }

    private static BlockPos pos(ThreePosition tp) {
        return new BlockPos(tp.getX(), tp.getY(), tp.getZ());
    }

}
