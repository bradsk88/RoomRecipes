package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface IRoomRecipeMatch<ROOM, RECIPE, POS, BLOCK> {

    ImmutableList<RECIPE> getRecipeIDs();

    ROOM getRoom();

    ImmutableMap<POS, BLOCK> getContainedBlocks();
}
