package ca.bradj.roomrecipes.core.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModItemGroup {

    public static final CreativeModeTab ROOMRECIPES_GROUP = new CreativeModeTab("roomrecipes") {
        @Override
        public ItemStack makeIcon() {
            return Items.DIAMOND.getDefaultInstance();
        }
    };
}
