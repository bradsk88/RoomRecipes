package ca.bradj.roomrecipes.serialization;

import ca.bradj.roomrecipes.core.Room;
import ca.bradj.roomrecipes.core.space.InclusiveSpace;
import ca.bradj.roomrecipes.core.space.Position;
import net.minecraft.nbt.CompoundTag;

public class RoomSerializer {

    public static final RoomSerializer INSTANCE = new RoomSerializer();

    private static final String NBT_DOOR_POS_X = "door_pos_x";
    private static final String NBT_DOOR_POS_Z = "door_pos_z";

    private static final String NBT_SPACE_AA_X = "space_aa_x";
    private static final String NBT_SPACE_AA_Z = "space_aa_z";

    private static final String NBT_SPACE_BB_X = "space_bb_x";
    private static final String NBT_SPACE_BB_Z = "space_bb_z";

    public CompoundTag serializeNBT(Room recipes) {
        CompoundTag c = new CompoundTag();
        c.putInt(NBT_DOOR_POS_X, recipes.getDoorPos().x);
        c.putInt(NBT_DOOR_POS_Z, recipes.getDoorPos().z);
        c.putInt(NBT_SPACE_AA_X, recipes.getSpace().getCornerA().x);
        c.putInt(NBT_SPACE_AA_Z, recipes.getSpace().getCornerA().z);
        c.putInt(NBT_SPACE_BB_X, recipes.getSpace().getCornerB().x);
        c.putInt(NBT_SPACE_BB_Z, recipes.getSpace().getCornerB().z);
        return c;
    }

    public Room deserializeNBT(
            CompoundTag nbt
    ) {
        int x = nbt.getInt(NBT_DOOR_POS_X);
        int z = nbt.getInt(NBT_DOOR_POS_Z);
        Position doorPos = new Position(x, z);
        Position aa = new Position(nbt.getInt(NBT_SPACE_AA_X), nbt.getInt(NBT_SPACE_AA_Z));
        Position bb = new Position(nbt.getInt(NBT_SPACE_BB_X), nbt.getInt(NBT_SPACE_BB_Z));
        InclusiveSpace space = new InclusiveSpace(aa, bb);
        return new Room(doorPos, space);
    }
}
