package ca.bradj.roomrecipes.core.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

public class ModItemGroup {

    public static final CreativeModeTab ROOMRECIPES_GROUP = CreativeModeTab.builder()
            .title(Component.translatable("roomrecipes"))
            .icon(() -> Items.DIAMOND.getDefaultInstance())
            .build();
}
