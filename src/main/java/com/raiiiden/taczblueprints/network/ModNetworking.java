package com.raiiiden.taczblueprints.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "taczblueprints")
public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playBidirectional(
                SyncUnlockedGunsPacket.TYPE,
                SyncUnlockedGunsPacket.STREAM_CODEC,
                SyncUnlockedGunsPacket::handle
        );
    }

    public static void sendToPlayer(ServerPlayer player, SyncUnlockedGunsPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToAll(ServerPlayer sender, SyncUnlockedGunsPacket packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }
}
