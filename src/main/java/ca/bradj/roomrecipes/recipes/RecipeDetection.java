package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.RoomRecipes;
import ca.bradj.roomrecipes.adapter.Positions;
import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.logic.DoorDetection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeDetection {

    public static Optional<RoomRecipe> getActiveRecipe(
            Level level,
            Room room,
            DoorDetection.DoorChecker doorChecker,
            int y
    ) {
        List<Block> blocksInSpace = getBlocksInRoom(level, room, y);
        RecipeManager recipeManager = level.getRecipeManager();

        SimpleContainer inv = new SimpleContainer(blocksInSpace.size());
        for (int i = 0; i < blocksInSpace.size(); i++) {
            ItemStack stackInSlot = new ItemStack(blocksInSpace.get(i), 1);
            inv.setItem(i, stackInSlot);
        }

        List<RoomRecipe> recipes = recipeManager.getAllRecipesFor(RecipesInit.ROOM);
        recipes = Lists.reverse(ImmutableList.sortedCopyOf(recipes));

        return recipes.stream().filter(r -> r.matches(inv, level)).findFirst();
    }

    private static List<Block> getBlocksInRoom(
            Level level,
            Room room,
            int y
    ) {
        List<Block> blockList = new ArrayList<>();
        BlockPos pos1 = Positions.ToBlock(room.getSpace().getCornerA(), y);
        BlockPos pos2 = Positions.ToBlock(room.getSpace().getCornerB(), y).above();

        // Get the chunk containing the starting and ending coordinates
        int xMin = Math.min(pos1.getX(), pos2.getX());
        int xMax = Math.max(pos1.getX(), pos2.getX());
        int zMin = Math.min(pos1.getZ(), pos2.getZ());
        int zMax = Math.max(pos1.getZ(), pos2.getZ());
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
                            blockList.add(block);
                        }
                    }
                }
            }
        }
        return blockList;
    }

}
