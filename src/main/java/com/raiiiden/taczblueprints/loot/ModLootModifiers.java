package com.raiiiden.taczblueprints.loot;

import com.mojang.serialization.MapCodec;
import com.raiiiden.taczblueprints.TaCZBlueprints;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class ModLootModifiers {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TaCZBlueprints.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BlueprintLootModifier>> BLUEPRINT_CHEST_LOOT =
            LOOT_MODIFIERS.register("blueprint_chest_loot", BlueprintLootModifier.CHEST_CODEC);

    public static void register(IEventBus modEventBus) {
        LOOT_MODIFIERS.register(modEventBus);
        TaCZBlueprints.LOGGER.info("[{}] Loot modifiers registered", TaCZBlueprints.MODID);
    }
}
