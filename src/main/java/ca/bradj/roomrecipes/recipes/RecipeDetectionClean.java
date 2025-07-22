package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import ca.bradj.roomrecipes.core.space.ThreePosition;
import ca.bradj.roomrecipes.logic.InclusiveSpaces;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public class RecipeDetectionClean {

    public record Match<ROOM, RECIPE, B>(ROOM room, ImmutableList<RECIPE> recipeIDs,
                                         Iterable<Map.Entry<ThreePosition, B>> containedBlocks) {
    }

    public static <R extends Room, B, RECIPE extends Comparable<RECIPE>> @Nullable Match<R, RECIPE, B> getActiveRecipes(
            Function<ThreePosition, B> level,
            R room,
            int minY,
            int maxY,
            Supplier<ImmutableList<RECIPE>> recipeSrc,
            BiPredicate<RECIPE, ImmutableList<B>> matches,
            Predicate<RECIPE> include
    ) {
        Map<ThreePosition, B> blocksInSpace = getBlocksInRoom(level, room.getSpaces(), minY, maxY);

        ImmutableList<B> blocksList = ImmutableList.copyOf(blocksInSpace.values());

        List<RECIPE> recipes = recipeSrc.get();
        recipes = Lists.reverse(ImmutableList.sortedCopyOf(recipes));

        List<RECIPE> matchedRecipes = recipes.stream().filter(include).filter(r -> matches.test(r, blocksList))
                                             .toList();
        if (matchedRecipes.isEmpty()) {
            return null;
        }
        return new Match<>(room, ImmutableList.copyOf(matchedRecipes), blocksInSpace.entrySet());
    }


    public static <B> ImmutableMap<ThreePosition, B> getBlocksInRoom(
            Function<ThreePosition, B> level,
            Collection<InclusiveSpace> room,
            int minY,
            int maxY
    ) {
        // Create a map to hold the blocks in the room
        HashMap<ThreePosition, B> blocks = new HashMap<>();
        HashMap<Position, Boolean> positions = new HashMap<>();
        // Iterate over each space in the room
        for (InclusiveSpace space : room) {
            // Get the starting and ending coordinates of the space
            Position pos1 = space.getCornerA();
            Position pos2 = space.getCornerB();
            // Add every block in the space to the map
            addBlocksInSpace(
                    level,
                    new ThreePosition(pos1.x, minY, pos1.z),
                    new ThreePosition(pos2.x, maxY, pos2.z),
                    (pos, block) -> {
                        blocks.put(pos, block);
                        positions.put(pos, true);
                    },
                    (pos, block) -> positions.put(new Position(pos.x, pos.z), true)
            );
        }
        // Iterate only the edges of each space
        for (InclusiveSpace space : room) {
            // FIXME Somehow we need to

            // top
            Position a = space.getNorthXWall().negativeCorner();
            Position b = space.getNorthXWall().positiveCorner();
            for (int i = a.x + 1; i <= b.x - 1; i++) {
                Position above = new Position(i, a.z - 1);
                Position below = new Position(i, a.z + 1);
                tryAddVertical(level, minY, maxY, space, above, positions, i, a, blocks);
                tryAddVertical(level, minY, maxY, space, below, positions, i, a, blocks);
            }
            // bottom
            a = space.getSouthXWall().negativeCorner();
            b = space.getSouthXWall().positiveCorner();
            for (int i = a.x + 1; i <= b.x - 1; i++) {
                Position above = new Position(i, a.z - 1);
                Position below = new Position(i, a.z + 1);
                tryAddVertical(level, minY, maxY, space, above, positions, i, a, blocks);
                tryAddVertical(level, minY, maxY, space, below, positions, i, a, blocks);
            }
            // left
            a = space.getWestZWall().negativeCorner();
            b = space.getWestZWall().positiveCorner();
            for (int i = a.z + 1; i <= b.z - 1; i++) {
                Position leftOf = new Position(a.x -1 , i);
                Position rightOf = new Position(a.x + 1, i);
                tryAddHorizontal(level, minY, maxY, space, leftOf, positions, i, a, blocks);
                tryAddHorizontal(level, minY, maxY, space, rightOf, positions, i, a, blocks);
            }
            // right
            a = space.getEastZWall().negativeCorner();
            b = space.getEastZWall().positiveCorner();
            for (int i = a.z + 1; i <= b.z - 1; i++) {
                Position leftOf = new Position(a.x -1 , i);
                Position rightOf = new Position(a.x + 1, i);
                tryAddHorizontal(level, minY, maxY, space, leftOf, positions, i, a, blocks);
                tryAddHorizontal(level, minY, maxY, space, rightOf, positions, i, a, blocks);
            }
        }
        // Return the map of blocks in the room
        return ImmutableMap.copyOf(blocks);
    }

    private static <B> void tryAddVertical(
            Function<ThreePosition, B> level,
            int minY,
            int maxY,
            InclusiveSpace space,
            Position above,
            HashMap<Position, Boolean> positions,
            int i,
            Position a,
            HashMap<ThreePosition, B> blocks
    ) {
        if (!InclusiveSpaces.contains(space, above)) {
            if (positions.containsKey(above)) {
                addBlocksInSpace(
                        level,
                        new ThreePosition(i, minY, a.z),
                        new ThreePosition(i, maxY, a.z),
                        skipNull(blocks),
                        skipNull(blocks)
                );
            }
        }
    }
    private static <B> void tryAddHorizontal(
            Function<ThreePosition, B> level,
            int minY,
            int maxY,
            InclusiveSpace space,
            Position above,
            HashMap<Position, Boolean> positions,
            int i,
            Position a,
            HashMap<ThreePosition, B> blocks
    ) {
        if (InclusiveSpaces.contains(space, above)) {
            return;
        }
        if (positions.containsKey(above)) {
            addBlocksInSpace(
                    level,
                    new ThreePosition(a.x, minY, i),
                    new ThreePosition(a.x, maxY, i),
                    skipNull(blocks),
                    skipNull(blocks)
            );
        }
    }

    private static <B> BiConsumer<ThreePosition, B> skipNull(HashMap<ThreePosition, B> b) {
        return (pos, block) -> {
            if (block != null) {
                b.put(pos, block);
            }
        };
    }

    private static <B> void addBlocksInSpace(
            Function<ThreePosition, B> level,
            ThreePosition pos1,
            ThreePosition pos2,
            BiConsumer<ThreePosition, B> ifInterior,
            BiConsumer<ThreePosition, B> ifWall
    ) {
        int xMin = Math.min(pos1.x, pos2.x);
        int xMax = Math.max(pos1.x, pos2.x);
        int zMin = Math.min(pos1.z, pos2.z);
        int zMax = Math.max(pos1.z, pos2.z);
        // Get the chunk containing the starting and ending coordinates
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
                    boolean isXWall = false;
                    if (blockX == xMin) {
                        isXWall = true;
                    }
                    if (blockX == xMax) {
                        isXWall = true;
                    }
                    for (int blockZ = blockZMin; blockZ <= blockZMax; blockZ++) {
                        boolean isZWall = false;
                        if (blockZ == zMin) {
                            isZWall = true;
                        }
                        if (blockZ == zMax) {
                            isZWall = true;
                        }

                        int yMin = Math.min(pos1.getY(), pos2.getY());
                        int yMax = Math.max(pos1.getY(), pos2.getY());
                        for (int blockY = yMin; blockY <= yMax; blockY++) {
                            ThreePosition blockPos = new ThreePosition(blockX, blockY, blockZ);
                            B block = level.apply(blockPos);
                            if (isXWall || isZWall) {
                                ifWall.accept(blockPos, block);
                            } else {
                                ifInterior.accept(blockPos, block);
                            }
                        }
                    }
                }
            }
        }
    }

}
