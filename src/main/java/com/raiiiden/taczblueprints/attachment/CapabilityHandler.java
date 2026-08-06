//package com.raiiiden.taczblueprints.capability;
//
//import com.raiiiden.taczblueprints.TaCZBlueprints;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.common.Mod;
//import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//
//
//public class CapabilityHandler {
//
//    private static final ResourceLocation CAPABILITY_ID =ResourceLocation.fromNamespaceAndPath(TaCZBlueprints.MODID, "gun_unlocks");
//
//    @EventBusSubscriber(modid = TaCZBlueprints.MODID, bus = EventBusSubscriber.Bus.MOD)
//    public static class ModBusEvents {
//
//        @SubscribeEvent
//        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
//            event.register(IGunUnlocks.class);
//            // TaCZBlueprints.LOGGER.info("[{}] Registered IGunUnlocks capability", TaCZBlueprints.MODID);
//        }
//    }
//
//    @EventBusSubscriber(modid = TaCZBlueprints.MODID, bus = EventBusSubscriber.Bus.GAME)
//    public static class ForgeBusEvents {
//
//        @SubscribeEvent
//        public static void onAttachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
//            if (event.getObject() instanceof Player) {
//                // Create a new provider with a fresh GunUnlocks instance
//                GunUnlocksProvider provider = new GunUnlocksProvider();
//                event.addCapability(CAPABILITY_ID, provider);
//
//                // TaCZBlueprints.LOGGER.debug("[{}] Attached gun unlocks capability to player", TaCZBlueprints.MODID);
//            }
//        }
//    }
//}