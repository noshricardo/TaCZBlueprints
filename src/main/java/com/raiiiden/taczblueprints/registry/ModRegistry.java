package com.raiiiden.taczblueprints.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, com.raiiiden.taczblueprints.TaCZBlueprints.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, com.raiiiden.taczblueprints.TaCZBlueprints.MODID);

    // Example Item Registration
    public static final DeferredHolder<Item, Item> EXAMPLE_ITEM = ITEMS.register("example_item",
            () -> new Item(new Item.Properties()));

    // Example Armor Registration
    public static final DeferredHolder<Item, ArmorItem> EXAMPLE_HELMET = ITEMS.register("example_helmet",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final DeferredHolder<Item, ArmorItem> EXAMPLE_CHESTPLATE = ITEMS.register("example_chestplate",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    // Creative Tab Registration
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TACZBLUEPRINTS_TAB = CREATIVE_MODE_TABS.register("taczblueprints_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.taczblueprints_tab"))
                    .icon(() -> new ItemStack(EXAMPLE_ITEM.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        entries.accept(EXAMPLE_ITEM.get());
                        entries.accept(EXAMPLE_HELMET.get());
                        entries.accept(EXAMPLE_CHESTPLATE.get());
                    })
                    .build()
    );
}
