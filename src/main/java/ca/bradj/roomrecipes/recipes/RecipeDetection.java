package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.adapter.Positions;
import ca.bradj.roomrecipes.adapter.RoomRecipeMatch;
import ca.bradj.roomrecipes.logic.DoorDetection;
import ca.bradj.roomrecipes.serialization.MCRoom;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RecipeDetection {

    public static Optional<RoomRecipeMatch<MCRoom>> getActiveRecipe(
            Level level,
            MCRoom room,
            DoorDetection.DoorChecker doorChecker
    ) {
        Map<BlockPos, Block> blocksInSpace = getBlocksInRoom(level, room, false);
        RecipeManager recipeManager = level.getRecipeManager();

        List<Block> blocksList = ImmutableList.copyOf(blocksInSpace.values());
        SimpleContainer inv = new SimpleContainer(blocksList.size());
        for (int i = 0; i < blocksList.size(); i++) {
            ItemStack stackInSlot = new ItemStack(blocksList.get(i), 1);
            inv.setItem(i, stackInSlot);
        }

        List<RoomRecipe> recipes = recipeManager.getAllRecipesFor(RecipesInit.ROOM);
        recipes = Lists.reverse(ImmutableList.sortedCopyOf(recipes));

        Optional<RoomRecipe> matchedRecipe = recipes.stream().filter(r -> r.matches(inv, level)).findFirst();
        return matchedRecipe.map(v -> new RoomRecipeMatch<>(
                room, v.getId(), blocksInSpace.entrySet())
        );
    }

    public static ImmutableMap<BlockPos, Block> getBlocksInRoom(
            Level level,
            MCRoom room,
            boolean includeWallBlocks
    ) {
        BlockPos pos1 = Positions.ToBlock(room.getSpace().getCornerA(), room.yCoord);
        BlockPos pos2 = Positions.ToBlock(room.getSpace().getCornerB(), room.yCoord).above();

        ImmutableMap.Builder<BlockPos, Block> b = ImmutableMap.builder();

        // Get the chunk containing the starting and ending coordinates
        int xMin = Math.min(pos1.getX(), pos2.getX());
        int xMax = Math.max(pos1.getX(), pos2.getX());
        int zMin = Math.min(pos1.getZ(), pos2.getZ());
        int zMax = Math.max(pos1.getZ(), pos2.getZ());
        if (!includeWallBlocks) {
            xMin = xMin + 1;
            xMax = xMax - 1;
            zMin = zMin + 1;
            zMax = zMax - 1;
        }
        int chunkXMin = xMin >> 4;
        int chunkXMax = xMax >> 4;
        int chunkZMin = zMin >> 4;
        int chunkZMax = zMax >> 4;
        for (int chunkX = chunkXMin; chunkX <= chunkXMax; chunkX++) {
            for (int chunkZ = chunkZMin; chunkZ <= chunkZMax; chunkZ++) {
                // Iterate over all blocks in the chunk and add them to the list
                int blockXMin = Math.max(xMin, chunkX << 4);
                int blockXMax = Math.min(xMax, (chunkX << 4) + 15);
                int blockZMin = Math.max(zMin, chunkZ << 4);
                int blockZMax = Math.min(zMax, (chunkZ << 4) + 15);
                for (int blockX = blockXMin; blockX <= blockXMax; blockX++) {
                    for (int blockZ = blockZMin; blockZ <= blockZMax; blockZ++) {
                        int yMin = Math.min(pos1.getY(), pos2.getY());
                        int yMax = Math.max(pos1.getY(), pos2.getY());
                        for (int blockY = yMin; blockY <= yMax; blockY++) {
                            BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
                            Block block = level.getBlockState(blockPos).getBlock();
                            b.put(blockPos, block);
                        }
                    }
                }
            }
        }
        return b.build();
    }

}
