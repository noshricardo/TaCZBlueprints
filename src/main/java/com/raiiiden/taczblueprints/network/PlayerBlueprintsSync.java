package com.raiiiden.taczblueprints.network;

import com.raiiiden.taczblueprints.TaCZBlueprints;
import com.raiiiden.taczblueprints.attachment.GunUnlocksProvider;
import com.raiiiden.taczblueprints.attachment.IGunUnlocks;
import com.raiiiden.taczblueprints.attachment.ModAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;


import java.util.Set;

@EventBusSubscriber(modid = TaCZBlueprints.MODID)
public class PlayerBlueprintsSync {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            IGunUnlocks unlocked = serverPlayer.getData(ModAttachmentTypes.GUN_UNLOCKS.get());
            serverPlayer.getServer().execute(() -> {

                ModNetworking.sendToPlayer(
                        serverPlayer,
                        new SyncUnlockedGunsPacket(unlocked.getUnlockedGuns())
                );
            });
        }
    }

    // Sync blueprints on login
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            IGunUnlocks unlocked = serverPlayer.getData(ModAttachmentTypes.GUN_UNLOCKS.get());
            serverPlayer.getServer().execute(() -> {

                ModNetworking.sendToPlayer(
                        serverPlayer,
                        new SyncUnlockedGunsPacket(unlocked.getUnlockedGuns())
                );
            });
        }
    }

    // Sync when player changes dimension (optional but recommended)
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            IGunUnlocks unlocked = serverPlayer.getData(ModAttachmentTypes.GUN_UNLOCKS.get());
            serverPlayer.getServer().execute(() -> {

                ModNetworking.sendToPlayer(
                        serverPlayer,
                        new SyncUnlockedGunsPacket(unlocked.getUnlockedGuns())
                );
            });
        }
    }
}