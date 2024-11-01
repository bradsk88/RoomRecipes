package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableMap;

public interface IRoomRecipeMatch<ROOM, RECIPE, POS, BLOCK> {
    RECIPE getRecipeID();

    ROOM getRoom();

    ImmutableMap<POS, BLOCK> getContainedBlocks();
}
