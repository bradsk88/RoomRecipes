package ca.bradj.roomrecipes.adapter;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

public class RoomWithBlocks<ROOM, POS, BLOCK> {
    public final ROOM room;
    public final ImmutableMap<POS, BLOCK> containedBlocks;

    public RoomWithBlocks(
            ROOM room,
            Map<POS, BLOCK> containedBlocks
    ) {
        this(room, containedBlocks.entrySet());
    }

    public RoomWithBlocks(
            ROOM room,
            Iterable<Map.Entry<POS, BLOCK>> containedBlocks
    ) {
        this.room = room;
        ImmutableMap.Builder<POS, BLOCK> b = ImmutableMap.builder();
        containedBlocks.forEach((v) -> b.put(v.getKey(), v.getValue()));
        this.containedBlocks = b.build();
    }


    @Override
    public String toString() {
        return "RoomRecipeMatch{" +
                "room=" + room +
                ", containedBlocks=" + containedBlocks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomWithBlocks<?, ?, ?> that = (RoomWithBlocks<?, ?, ?>) o;
        return Objects.equals(room, that.room) && Objects.equals(containedBlocks, that.containedBlocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, containedBlocks);
    }
}
